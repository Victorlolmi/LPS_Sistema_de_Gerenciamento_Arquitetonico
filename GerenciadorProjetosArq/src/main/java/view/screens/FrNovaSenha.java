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
        edtSenhaNovaConfirm = new javax.swing.JTextField();
        edtSenhaNova = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        edtIdentificador2.setForeground(new java.awt.Color(86, 86, 86));
        edtIdentificador2.setText("Seu email de cadastro");
        edtIdentificador2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtIdentificador2ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        cadastroButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/BotaoVoltar.png"))); // NOI18N
        cadastroButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cadastroButton2MouseClicked(evt);
            }
        });

        enviarButton.setBackground(new java.awt.Color(64, 86, 213));
        enviarButton.setForeground(new java.awt.Color(255, 255, 255));
        enviarButton.setText("Atualizar senha");
        enviarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarButtonActionPerformed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Logo.png"))); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(86, 86, 86));
        jLabel2.setText("Recuperar Senha");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(30, 42, 58));
        jLabel5.setText("ArchFlow");

        edtSenhaNovaConfirm.setForeground(new java.awt.Color(86, 86, 86));
        edtSenhaNovaConfirm.setText("Seu email de cadastro");
        edtSenhaNovaConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtSenhaNovaConfirmActionPerformed(evt);
            }
        });

        edtSenhaNova.setForeground(new java.awt.Color(86, 86, 86));
        edtSenhaNova.setText("Seu email de cadastro");
        edtSenhaNova.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtSenhaNovaActionPerformed(evt);
            }
        });

        jLabel7.setText("Nova senha");

        jLabel8.setText("Confirme sua senha");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cadastroButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(enviarButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(cadastroButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cadastroButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edtSenhaNovaConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(edtSenhaNova, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cadastroButton2)
                        .addGap(27, 27, 27)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(4, 4, 4)))
                .addComponent(edtSenhaNova, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtSenhaNovaConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(enviarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cadastroButton1)
                    .addComponent(cadastroButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

    private void edtSenhaNovaConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtSenhaNovaConfirmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtSenhaNovaConfirmActionPerformed

    private void edtIdentificador2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtIdentificador2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtIdentificador2ActionPerformed

    private void edtSenhaNovaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtSenhaNovaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edtSenhaNovaActionPerformed

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
    private javax.swing.JTextField edtSenhaNova;
    private javax.swing.JTextField edtSenhaNovaConfirm;
    private javax.swing.JButton enviarButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables
}
