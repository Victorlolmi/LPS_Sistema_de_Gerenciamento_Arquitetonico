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
import view.screens.dialogs.FrHome;
/**
 *
 * @author Viktin
 */
public class GerenciadorProjetosArq {

     public static void main(String[] args) {
 
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrLogin telaLogin = new FrLogin(); 
                telaLogin.setVisible(true);
            }
        });
    }
}
