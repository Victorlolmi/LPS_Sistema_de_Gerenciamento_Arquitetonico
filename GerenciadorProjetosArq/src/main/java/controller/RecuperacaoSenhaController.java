package controller;

import java.time.LocalDateTime;
import java.util.UUID;
import model.dao.UsuarioDAO;
import model.entities.Usuario;
import model.services.EmailService;
import view.screens.FrLogin;
import view.screens.FrRecuperacaoSenha;
import view.screens.FrVerificacaoCodigo;

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

        if (usuario != null) {
            String codigo = gerarCodigoRecuperacao();
            LocalDateTime validade = LocalDateTime.now().plusMinutes(15);
            
            usuario.setCodigo_recuperacao(codigo);
            usuario.setValidade_codigo_recuperacao(validade);
            
            usuarioDAO.update(usuario);
            
            emailService.enviarEmailRecuperacao(usuario.getEmail(), codigo);
        }

        view.exibeMensagem("Se o e-mail informado estiver em nossa base de dados, um código de recuperação será enviado.");
        
        navegarParaVerificacao(identificador);
    }

    private String gerarCodigoRecuperacao() {
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