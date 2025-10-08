/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.UsuarioDAO;
import model.entities.Usuario;
import view.screens.FrLogin;
import view.screens.FrNovaSenha;

/**
 *
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

        // 3. Buscar o usuário no banco de dados
        Usuario usuario = usuarioDAO.findByEmailOrCpf(email);

        if (usuario != null) {
            String senhaCriptografada = novaSenha; 
            usuario.setSenha(senhaCriptografada);
            usuario.setCodigo_recuperacao(null); 
            usuario.setValidade_codigo_recuperacao(null); 

            usuarioDAO.update(usuario);

            view.exibeMensagem("Senha atualizada com sucesso!");
            
            navegarParaLogin();
        } else {
            view.exibeMensagem("Ocorreu um erro ao encontrar o usuário. Tente novamente.");
        }
    }
    
    public void navegarParaLogin() {
        FrLogin frLogin = new FrLogin();
        frLogin.setVisible(true);
        this.view.dispose();
    }
}