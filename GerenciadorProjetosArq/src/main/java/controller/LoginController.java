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
import view.screens.dialogs.Gestor.FrHome;

/**
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
        String identificador = view.getIdentificador();
        String senhaDigitada = view.getSenha();

        if (identificador.isEmpty()) {
            view.exibeMensagem("Erro: O campo 'Login' é obrigatório.");
            return;
        }
        if (senhaDigitada.isEmpty()) {
            view.exibeMensagem("Erro: O campo 'Senha' é obrigatório.");
            return;
        }

        Usuario usuarioDoBanco = usuarioDAO.findByEmailOrCpf(identificador);

        if (usuarioDoBanco == null) {
            // FIXME: Risco de segurança (Enumeração de Usuários). 
            // Em produção, retorne mensagem genérica ("Usuário ou senha inválidos") independente do erro.
            view.exibeMensagem("Falha no login: Usuário não encontrado.");
        } else {
            // Valida o hash (nunca comparar strings puras em auth)
            if (BCrypt.checkpw(senhaDigitada, usuarioDoBanco.getSenha())) {
                FrHome telaPrincipal = new FrHome();
                telaPrincipal.setVisible(true);
                view.dispose();
            } else {
                view.exibeMensagem("Falha no login: Senha incorreta.");
            }
        }
    }

    public void exibirRecuperacaoSenha() {
        FrRecuperacaoSenha telaRecuperacao = new FrRecuperacaoSenha();
        telaRecuperacao.setVisible(true);
        this.view.dispose(); 
    }

    public void navegarParaCadastro() {
        FrCadastro telaCadastro = new FrCadastro();
        telaCadastro.setVisible(true);
        view.dispose();
    }
}