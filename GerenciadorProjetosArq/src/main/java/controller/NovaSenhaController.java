/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.UsuarioDAO;
import model.entities.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import view.screens.FrLogin;
import view.screens.FrNovaSenha;

/**
 * @author juans
 */
public class NovaSenhaController {

    private final FrNovaSenha view;
    private final UsuarioDAO usuarioDAO;

    public NovaSenhaController(FrNovaSenha view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }

    public void atualizarSenha() {
        String email = view.getEmailUsuario();
        String novaSenha = view.getNovaSenha();
        String confirmacaoSenha = view.getConfirmacaoSenha();

        if (novaSenha.isEmpty() || confirmacaoSenha.isEmpty()) {
            view.exibeMensagem("Os campos de senha não podem estar vazios.");
            return;
        }

        if (!novaSenha.equals(confirmacaoSenha)) {
            view.exibeMensagem("As senhas não coincidem. Tente novamente.");
            return;
        }

        // TODO: Adicionar validação de força de senha (Regex para min 8 chars, num, symbol)
        
        Usuario usuario = usuarioDAO.findByEmailOrCpf(email);

        if (usuario != null) {
            String senhaCriptografada = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            usuario.setSenha(senhaCriptografada);
            
            // Security: Queima o token imediatamente para evitar Replay Attack
            usuario.setCodigo_recuperacao(null); 
            usuario.setValidade_codigo_recuperacao(null); 

            usuarioDAO.update(usuario);

            view.exibeMensagem("Senha atualizada com sucesso!");
            
            navegarParaLogin();
        } else {
            // Se chegou aqui com email inválido, algo estranho aconteceu no fluxo anterior
            view.exibeMensagem("Ocorreu um erro ao encontrar o usuário. Tente novamente.");
        }
    }
    
    public void navegarParaLogin() {
        FrLogin frLogin = new FrLogin();
        frLogin.setVisible(true);
        this.view.dispose();
    }
}