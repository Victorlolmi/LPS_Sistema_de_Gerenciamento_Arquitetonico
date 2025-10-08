/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.time.LocalDateTime;
import model.dao.UsuarioDAO;
import model.entities.Usuario;
import view.screens.FrLogin;
import view.screens.FrNovaSenha;
import view.screens.FrVerificacaoCodigo;

/**
 *
 * @author juans
 */

public class VerificacaoCodigoController {
    
    private final FrVerificacaoCodigo view;
    private final UsuarioDAO usuarioDAO;

    public VerificacaoCodigoController(FrVerificacaoCodigo view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public void verificarCodigo() {
        String email = view.getEmailUsuario();
        String codigoDigitado = view.getCodigo();

        if (codigoDigitado.isEmpty()) {
            view.exibeMensagem("Por favor, insira o código de verificação.");
            return;
        }

        Usuario usuario = usuarioDAO.findByEmailOrCpf(email);

        if (usuario != null && usuario.getCodigo_recuperacao().equals(codigoDigitado)) {
            
            if (LocalDateTime.now().isBefore(usuario.getValidade_codigo_recuperacao())) {
                view.exibeMensagem("Código verificado com sucesso!");
                navegarParaNovaSenha(email);
            } else {
                view.exibeMensagem("Código expirado. Por favor, solicite um novo.");
            }
        } else {
            view.exibeMensagem("Código inválido. Tente novamente.");
        }
    }
    public void navegarParaLogin() {
        FrLogin frLogin = new FrLogin();
        frLogin.setVisible(true);
        this.view.dispose();
    }
    private void navegarParaNovaSenha(String email) {
        FrNovaSenha frNovaSenha = new FrNovaSenha(email);
        frNovaSenha.setVisible(true);
        view.dispose();
    }
}
