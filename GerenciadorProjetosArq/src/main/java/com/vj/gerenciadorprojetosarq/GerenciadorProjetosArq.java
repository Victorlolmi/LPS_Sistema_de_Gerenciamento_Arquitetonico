/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.vj.gerenciadorprojetosarq;
import model.dao.UsuarioDAO;
import model.entities.Cliente;
import model.entities.Gestor;
import javax.swing.SwingUtilities;
import view.screens.FrLogin;
import com.formdev.flatlaf.FlatLightLaf; // Para o tema claro
import com.formdev.flatlaf.FlatDarkLaf;
import view.screens.dialogs.Gestor.FrHome;
/**
 *
 * @author Viktin
 */
public class GerenciadorProjetosArq {

     public static void main(String[] args) {
 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrHome telaLogin = new FrHome();
                telaLogin.setVisible(true);
            }
        });
    }
}
