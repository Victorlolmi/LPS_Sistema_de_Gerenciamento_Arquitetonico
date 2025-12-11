/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import javax.swing.JOptionPane;
import model.dao.UsuarioDAO;
import model.entities.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import view.screens.FrLogin;
import view.screens.FrCadastro;
import view.screens.FrRecuperacaoSenha;
/**
 *
 * @author Viktin
 */
public class LoginController {
     private final FrLogin view;
    private final UsuarioDAO usuarioDAO;

    public LoginController(FrLogin view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }

    public void realizarLogin() {
        // 1. Obter dados da View
        String identificador = view.getIdentificador();
        String senhaDigitada = view.getSenha();

        // 2. Validar dados de entrada (tratamento de exceções de negócio)
        if (identificador.isEmpty()) {
            view.exibeMensagem("Erro: O campo 'Login' é obrigatório.");
            return;
        }
        if (senhaDigitada.isEmpty()) {
            view.exibeMensagem("Erro: O campo 'Senha' é obrigatório.");
            return;
        }

        // 3. Buscar o usuário no banco de dados
        Usuario usuarioDoBanco = usuarioDAO.findByEmailOrCpf(identificador);

        // 4. Tomar a decisão
        if (usuarioDoBanco == null) {
            view.exibeMensagem("Falha no login: Usuário não encontrado.");
        } else {
            // Usuário encontrado, agora verificar a senha com BCrypt
            if (BCrypt.checkpw(senhaDigitada, usuarioDoBanco.getSenha())) {
                // Sucesso!
                
                //FrMain telaCadastro = new FrMain();
        
                // Torna a nova tela visível
                //telaCadastro.setVisible(true);
        
                // Fecha e libera os recursos da tela de login atual
                //view.dispose();
            } else {
                // Senha incorreta
                view.exibeMensagem("Falha no login: Senha incorreta.");
            }
        }
    }
        public void exibirRecuperacaoSenha() {
        // Cria a nova tela de recuperação
        FrRecuperacaoSenha telaRecuperacao = new FrRecuperacaoSenha();
        telaRecuperacao.setVisible(true);

        // Fecha a tela de login atual
        this.view.dispose(); // 'view' é a sua instância de FrLogin
    }
    public void navegarParaCadastro() {
        
        FrCadastro telaCadastro = new FrCadastro();
        
        // Torna a nova tela visível
        telaCadastro.setVisible(true);
        
        // Fecha e libera os recursos da tela de login atual
        view.dispose();
    }
}