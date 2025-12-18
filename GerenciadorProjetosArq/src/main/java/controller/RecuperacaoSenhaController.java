package controller;

import java.time.LocalDateTime;
import java.util.UUID;
import model.dao.UsuarioDAO;
import model.entities.Usuario;
import model.services.EmailService;
import view.screens.FrLogin;
import view.screens.FrRecuperacaoSenha;
import view.screens.FrVerificacaoCodigo;
/**
 *
 * @author juans
 */
public class RecuperacaoSenhaController {

    private final FrRecuperacaoSenha view;
    private final UsuarioDAO usuarioDAO;
    private final EmailService emailService;

    public RecuperacaoSenhaController(FrRecuperacaoSenha view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        this.emailService = new EmailService();
    }

    public void iniciarRecuperacao() {
        String identificador = view.getIdentificador();

        if (identificador.isEmpty()) {
            view.exibeMensagem("Por favor, preencha o campo com seu e-mail.");
            return;
        }

        Usuario usuario = usuarioDAO.findByEmailOrCpf(identificador);

        // Se o usuário não existir, falhamos silenciosamente para não vazar dados
        if (usuario != null) {
            String codigo = gerarCodigoRecuperacao();
            
            // Janela de 15 min para uso do token
            LocalDateTime validade = LocalDateTime.now().plusMinutes(15);
            
            usuario.setCodigo_recuperacao(codigo);
            usuario.setValidade_codigo_recuperacao(validade);
            
            usuarioDAO.update(usuario);
            
            // TODO: Mover para processamento assíncrono (fila) para não travar a UI
            emailService.enviarEmailRecuperacao(usuario.getEmail(), codigo);
        }

        // Security: Mensagem genérica proposital para evitar Enumeração de Usuários (User Enumeration Attack).
        // O atacante não deve saber se o e-mail existe ou não.
        view.exibeMensagem("Se o e-mail informado estiver em nossa base de dados, um código de recuperação será enviado.");
        
        navegarParaVerificacao(identificador);
    }

    private String gerarCodigoRecuperacao() {
        // Gera token curto alfanumérico (6 chars). UUID substring é suficiente aqui.
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
    }

    public void navegarParaVerificacao(String email) {
        FrVerificacaoCodigo frVerificacao = new FrVerificacaoCodigo(email);
        frVerificacao.setVisible(true);
        this.view.dispose();
    }
    
    public void navegarParaLogin() {
        FrLogin frLogin = new FrLogin();
        frLogin.setVisible(true);
        this.view.dispose();
    }
}