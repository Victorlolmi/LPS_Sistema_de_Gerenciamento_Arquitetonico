package controller;

import model.dao.UsuarioDAO;
import model.entities.Usuario;
import view.screens.FrLogin;
import view.screens.FrRecuperacaoSenha;

public class RecuperacaoSenhaController {
        
    private final FrRecuperacaoSenha view;
    private final UsuarioDAO usuarioDAO;

    public RecuperacaoSenhaController(FrRecuperacaoSenha view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Inicia o processo de recuperação de senha.
     */
    public void iniciarRecuperacao() {
        String identificador = view.getIdentificador();

        if (identificador.isEmpty()) {
            view.exibeMensagem("Por favor, preencha o campo com seu e-mail ou CPF.");
            return;
        }

        // Tenta encontrar o usuário no banco de dados
        Usuario usuario = usuarioDAO.findByEmailOrCpf(identificador);

        if (usuario != null) {
            // Lógica de recuperação de senha (ex: enviar email)
            // Por enquanto, vamos apenas exibir uma mensagem de sucesso.
            System.out.println("Enviando link de recuperação para o e-mail: " + usuario.getEmail());
            view.exibeMensagem("Se o usuário existir, um link de recuperação será enviado para o e-mail associado.");
            
            // Opcional: voltar para a tela de login após a mensagem
            navegarParaLogin();
        } else {
            // Mesmo que não encontre, exibe uma mensagem genérica por segurança
            view.exibeMensagem("Se o usuário existir, um link de recuperação será enviado para o e-mail associado.");
            navegarParaLogin();
        }
    }
    
    /**
     * Fecha a tela de recuperação e volta para a tela de login.
     */
    public void navegarParaLogin() {
        // Cria e exibe a tela de login
        FrLogin frLogin = new FrLogin();
        frLogin.setVisible(true);
        
        // Fecha a tela atual de recuperação
        this.view.dispose();
    }
}