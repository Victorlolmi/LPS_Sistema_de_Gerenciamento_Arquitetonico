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
import view.screens.dialogs.FrHome;
import model.entities.Gestor;  
import model.entities.Cliente;
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

        // Busca o usuário no banco (Pode vir um Gestor ou um Cliente)
        Usuario usuarioDoBanco = usuarioDAO.findByEmailOrCpf(identificador);

        if (usuarioDoBanco == null) {
            view.exibeMensagem("Falha no login: Usuário não encontrado.");
        } else {
            // Valida o hash da senha
            if (BCrypt.checkpw(senhaDigitada, usuarioDoBanco.getSenha())) {
                
                if (usuarioDoBanco instanceof Gestor) {
                    System.out.println("Logado como GESTOR: " + usuarioDoBanco.getNome());
                    abrirTelaPrincipal(usuarioDoBanco);
                    
                } else if (usuarioDoBanco instanceof Cliente) {
                    System.out.println("Logado como CLIENTE: " + usuarioDoBanco.getNome());
                    // O Cliente usa a mesma tela, mas ela vai se comportar diferente
                    abrirTelaPrincipal(usuarioDoBanco);
                    
                } else {
                    view.exibeMensagem("Tipo de usuário desconhecido.");
                }

            } else {
                view.exibeMensagem("Falha no login: Senha incorreta.");
            }
        }
    }
    private void abrirTelaPrincipal(Usuario usuario) {
        // NOTA: Precisarás alterar o construtor da FrHome para receber (Usuario usuario)
        FrHome telaPrincipal = new FrHome(usuario); 
        telaPrincipal.setVisible(true);
        view.dispose();
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