/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.screens;

import controller.NovaSenhaController;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author juans
 */
public class FrNovaSenha extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrNovaSenha.class.getName()); 

    private NovaSenhaController controller;
    private String emailUsuario;

    public FrNovaSenha(String email) {
        initComponents();
        setLocationRelativeTo(null);
        this.emailUsuario = email;
        this.controller = new NovaSenhaController(this);
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public String getNovaSenha() {
        return edtSenhaNova.getText();
    }

    public String getConfirmacaoSenha() {
        return edtSenhaNovaConfirm.getText();
    }

    public void exibeMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        edtIdentificador2 = new javax.swing.JTextField();
        cadastroButton = new javax.swing.JButton();
        cadastroButton1 = new javax.swing.JButton();
        cadastroButton2 = new javax.swing.JLabel();
        enviarButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        edtSenhaNovaConfirm = new javax.swing.JPasswordField();
        edtSenhaNova = new javax.swing.JPasswordField();

        edtIdentificador2.setForeground(new java.awt.Color(86, 86, 86));
        edtIdentificador2.setText("Seu email de cadastro");
        edtIdentificador2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtIdentificador2ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cadastroButton.setForeground(new java.awt.Color(82, 149, 205));
        cadastroButton.setText("Voltar para o Login");
        cadastroButton.setBorder(null);
        cadastroButton.setBorderPainted(false);
        cadastroButton.setContentAreaFilled(false);
        cadastroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cadastroButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cadastroButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(229, 292, -1, -1));

        cadastroButton1.setForeground(new java.awt.Color(86, 86, 86));
        cadastroButton1.setText("Lembrou a senha?");
        cadastroButton1.setBorder(null);
        cadastroButton1.setBorderPainted(false);
        cadastroButton1.setContentAreaFilled(false);
        cadastroButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cadastroButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(cadastroButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 292, -1, -1));

        cadastroButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/BotaoVoltar.png"))); // NOI18N
        cadastroButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cadastroButton2MouseClicked(evt);
            }
        });
        getContentPane().add(cadastroButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 6, -1, -1));

        enviarButton.setBackground(new java.awt.Color(64, 86, 213));
        enviarButton.setForeground(new java.awt.Color(255, 255, 255));
        enviarButton.setText("Atualizar senha");
        enviarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarButtonActionPerformed(evt);
            }
        });
        getContentPane().add(enviarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(89, 246, 240, 40));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Logo.png"))); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 13, 38, 40));

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(86, 86, 86));
        jLabel2.setText("Recuperar Senha");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(132, 71, 150, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(30, 42, 58));
        jLabel5.setText("ArchFlow");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 15, -1, -1));

        jLabel7.setText("Nova senha");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 105, -1, -1));

        jLabel8.setText("Confirme sua senha");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 176, -1, -1));

        edtSenhaNovaConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtSenhaNovaConfirmActionPerformed(evt);
            }
        });
        getContentPane().add(edtSenhaNovaConfirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 198, 258, -1));

        edtSenhaNova.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtSenhaNovaActionPerformed(evt);
            }
        });
        getContentPane().add(edtSenhaNova, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 136, 258, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cadastroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cadastroButtonActionPerformed
        controller.navegarParaLogin();
    }//GEN-LAST:event_cadastroButtonActionPerformed

    private void cadastroButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cadastroButton1ActionPerformed
        controller.navegarParaLogin();
    }//GEN-LAST:event_cadastroButton1ActionPerformed

    private void cadastroButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cadastroButton2MouseClicked
        controller.navegarParaLogin();
    }//GEN-LAST:event_cadastroButton2MouseClicked

    private void enviarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarButtonActionPerformed
        controller.atualizarSenha();
    }//GEN-LAST:event_enviarButtonActionPerformed

    private void edtIdentificador2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtIdentificador2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtIdentificador2ActionPerformed

    private void edtSenhaNovaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtSenhaNovaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtSenhaNovaActionPerformed

    private void edtSenhaNovaConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtSenhaNovaConfirmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtSenhaNovaConfirmActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new FrNovaSenha("teste@exemplo.com").setVisible(true));
    

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cadastroButton;
    private javax.swing.JButton cadastroButton1;
    private javax.swing.JLabel cadastroButton2;
    private javax.swing.JTextField edtIdentificador2;
    private javax.swing.JPasswordField edtSenhaNova;
    private javax.swing.JPasswordField edtSenhaNovaConfirm;
    private javax.swing.JButton enviarButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables
}
