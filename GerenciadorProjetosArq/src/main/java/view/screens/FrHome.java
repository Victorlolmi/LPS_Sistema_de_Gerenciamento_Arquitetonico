/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.screens;
import view.screens.dialogs.DlgCadastroProjetos;
import view.screens.dialogs.DlgCadastroCliente;
import model.dao.ClienteDAO;
import model.entities.Cliente;
import model.dao.ProjetoDAO;
import model.entities.Projeto;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension; // Importante para definir tamanho
import java.awt.FlowLayout; // Importante para centralizar
import javax.swing.JButton;
import javax.swing.JPanel; // O Container
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import controller.tableModel.ProjetoTableModel;
import controller.tableModel.ClienteTableModel;
import javax.swing.JScrollPane;
/**
 *
 * @author juans
 */
public class FrHome extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrHome.class.getName());
    private final ProjetoTableModel projetoModel;
    private final ClienteTableModel clienteModel;
    
    private final Color corAzulEscuro = new Color(30, 60, 160);
    private final Color corSelecao = new Color(240, 247, 255);
    private final javax.swing.border.Border bordaInferior = javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
    
    private javax.swing.JTextField txtBusca;
    /**
     * Creates new form FrProjetos
     */
    public FrHome() {
        initComponents();
        setLocationRelativeTo(null);
        estilizarAbasModernas();
        
        // 1. Inicializa Models
        this.projetoModel = new ProjetoTableModel();
        this.clienteModel = new ClienteTableModel();
        
        jTProjetos.setModel(projetoModel);
        jTClientes.setModel(clienteModel);
        
        // 2. Configura Layout (Agora usando a função padronizada)
        configurarTabelaProjetos();
        configurarTabelaClientes();
        
        // 3. Configura Funcionalidades
        configurarPlaceholder();
        
        // 4. Carrega Dados
        carregarTabelaClientes();
        carregarTabelaProjetos();
    }
    private void padronizarLayoutTabela(JTable table, JScrollPane scroll) {
        // Configurações Gerais
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(corSelecao);
        table.setSelectionForeground(Color.BLACK);
        
        // ScrollPane Clean
        if(scroll != null) {
            scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            scroll.getViewport().setBackground(Color.WHITE);
        }

        // Cabeçalho Azul
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(corAzulEscuro);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 40));
                return label;
            }
        });
    }

    // --- RENDERIZADOR PADRÃO DE TEXTO (Esquerda + Padding + Linha Cinza) ---
    private DefaultTableCellRenderer criarRendererTexto() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (isSelected) setForeground(Color.BLACK);
                else setForeground(new Color(50, 50, 50));
                
                // Borda Composta: Linha Embaixo + Espaço na Esquerda (Padding 20px)
                javax.swing.border.Border padding = javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 0);
                setBorder(javax.swing.BorderFactory.createCompoundBorder(bordaInferior, padding));
                
                return this;
            }
        };
    }

    // --- RENDERIZADOR PADRÃO DE BOTÃO ---
    private DefaultTableCellRenderer criarRendererBotao() {
        return new DefaultTableCellRenderer() {
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            final JButton btn = new JButton("Ver Detalhes");
            {
                btn.setPreferredSize(new Dimension(110, 30));
                btn.setBackground(new Color(65, 105, 225));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                panel.add(btn);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                panel.setBorder(bordaInferior);
                return panel;
            }
        };
    }

    // --- CONFIGURAÇÃO ESPECÍFICA: PROJETOS ---
    private void configurarTabelaProjetos() {
        // 1. Aplica o visual base
        padronizarLayoutTabela(jTProjetos, jScrollPane2);

        // 2. Define larguras
        jTProjetos.getColumnModel().getColumn(0).setPreferredWidth(200); // Projeto
        jTProjetos.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        jTProjetos.getColumnModel().getColumn(2).setPreferredWidth(120); // Status
        jTProjetos.getColumnModel().getColumn(3).setPreferredWidth(120); // Botão

        // 3. Aplica Renderers
        DefaultTableCellRenderer textoRenderer = criarRendererTexto();
        jTProjetos.getColumnModel().getColumn(0).setCellRenderer(textoRenderer);
        jTProjetos.getColumnModel().getColumn(1).setCellRenderer(textoRenderer);
        jTProjetos.getColumnModel().getColumn(3).setCellRenderer(criarRendererBotao());

        // Renderer Exclusivo de Status (Pill)
        jTProjetos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            final JLabel label = new JLabel();
            {
                label.setOpaque(true);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setPreferredSize(new Dimension(120, 25));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(225, 235, 255), 2));
                panel.add(label);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (value != null) ? value.toString() : "-";
                label.setText(status);
                label.setBackground(new Color(225, 235, 255));
                label.setForeground(corAzulEscuro);
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                panel.setBorder(bordaInferior);
                return panel;
            }
        });
    }

    // --- CONFIGURAÇÃO ESPECÍFICA: CLIENTES ---
    private void configurarTabelaClientes() {
        // 1. Aplica o visual base
        padronizarLayoutTabela(jTClientes, jScrollPane1);

        // 2. Define larguras
        // Colunas: Nome(0), CPF(1), Email(2), Cidade(3), Visualizar(4)
        jTClientes.getColumnModel().getColumn(0).setPreferredWidth(200); 
        jTClientes.getColumnModel().getColumn(1).setPreferredWidth(120); 
        jTClientes.getColumnModel().getColumn(2).setPreferredWidth(200); 
        jTClientes.getColumnModel().getColumn(3).setPreferredWidth(150); 
        jTClientes.getColumnModel().getColumn(4).setPreferredWidth(120); 

        // 3. Aplica Renderers
        DefaultTableCellRenderer textoRenderer = criarRendererTexto();
        jTClientes.getColumnModel().getColumn(0).setCellRenderer(textoRenderer); // Nome
        jTClientes.getColumnModel().getColumn(1).setCellRenderer(textoRenderer); // CPF
        jTClientes.getColumnModel().getColumn(2).setCellRenderer(textoRenderer); // Email
        jTClientes.getColumnModel().getColumn(3).setCellRenderer(textoRenderer); // Cidade
        
        jTClientes.getColumnModel().getColumn(4).setCellRenderer(criarRendererBotao()); // Botão
    }

    public void carregarTabelaProjetos() {
        ProjetoDAO dao = new ProjetoDAO();
        List<Projeto> lista = dao.listarTodos();
        projetoModel.setDados(lista);
    }
    
    public void carregarTabelaClientes() {
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> lista = dao.listarTodos();
        clienteModel.setDados(lista); // Agora usa o Model!
    }

    private void estilizarAbasModernas() {
        jTabbedPane2.setBackground(Color.WHITE);
        jTabbedPane2.setForeground(new Color(64, 86, 213)); 
        jTabbedPane2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        jTabbedPane2.setOpaque(true);
        
        jTabbedPane2.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new java.awt.Insets(10, 60, 10, 60); 
                selectedTabPadInsets = new java.awt.Insets(0, 0, 0, 0);
                contentBorderInsets = new java.awt.Insets(0, 0, 0, 0); 
            }
            @Override
            protected void paintTabBackground(java.awt.Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) g.setColor(new Color(240, 245, 255)); 
                else g.setColor(Color.WHITE);
                g.fillRect(x, y, w, h);
            }
            @Override
            protected void paintTabBorder(java.awt.Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(64, 86, 213));
                    g.fillRect(x, h - 3, w, 3); 
                }
            }
            @Override
            protected void paintContentBorder(java.awt.Graphics g, int tabPlacement, int selectedIndex) {}
        });
    }
    
    private void configurarPlaceholder() {
        // 1. Configuração Inicial (Estado "Vazio")
        lblBusca.setText("  Buscar por projeto ou cliente...");
        lblBusca.setForeground(new Color(150, 150, 150)); // Cinza claro

        // 2. Adiciona o Ouvinte de Foco
        lblBusca.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Quando clica no campo: Se tiver o texto fantasma, limpa e poe cor normal
                if (lblBusca.getText().equals("  Buscar por projeto ou cliente...")) {
                    lblBusca.setText("");
                    lblBusca.setForeground(Color.BLACK); // Cor normal do texto
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Quando sai do campo: Se estiver vazio, repõe o fantasma
                if (lblBusca.getText().trim().isEmpty()) {
                    lblBusca.setText("  Buscar por projeto ou cliente...");
                    lblBusca.setForeground(new Color(150, 150, 150)); // Cinza fantasma
                }
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etchedBorder1 = (javax.swing.border.EtchedBorder)javax.swing.BorderFactory.createEtchedBorder();
        etchedBorder2 = (javax.swing.border.EtchedBorder)javax.swing.BorderFactory.createEtchedBorder();
        etchedBorder3 = (javax.swing.border.EtchedBorder)javax.swing.BorderFactory.createEtchedBorder();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnCadastrarprojetos = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTProjetos = new javax.swing.JTable();
        lblBusca = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTClientes = new javax.swing.JTable();
        btnCadastrarCliente = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setBackground(new java.awt.Color(249, 250, 251));

        jPanel3.setBackground(new java.awt.Color(249, 250, 251));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCadastrarprojetos.setBackground(new java.awt.Color(64, 86, 213));
        btnCadastrarprojetos.setForeground(new java.awt.Color(255, 255, 255));
        btnCadastrarprojetos.setText("Novo Projeto");
        btnCadastrarprojetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarprojetosActionPerformed(evt);
            }
        });
        jPanel3.add(btnCadastrarprojetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 20, 170, 30));

        jTProjetos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTProjetos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTProjetosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTProjetos);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1270, 410));

        lblBusca.setToolTipText("");
        lblBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblBuscaActionPerformed(evt);
            }
        });
        lblBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblBuscaKeyReleased(evt);
            }
        });
        jPanel3.add(lblBusca, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 340, 30));
        jPanel3.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 63, 340, 0));

        jTabbedPane2.addTab("Projetos", jPanel3);

        jPanel4.setBackground(new java.awt.Color(249, 250, 251));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTClientes);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1170, 410));

        btnCadastrarCliente.setBackground(new java.awt.Color(64, 86, 213));
        btnCadastrarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCadastrarCliente.setText("Novo Cliente");
        btnCadastrarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarClienteActionPerformed(evt);
            }
        });
        jPanel4.add(btnCadastrarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 20, 130, 40));

        jTabbedPane2.addTab("Clientes", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab3", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab4", jPanel6);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 1360, 660));
        jTabbedPane2.getAccessibleContext().setAccessibleName("Projetos");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Logo.png"))); // NOI18N
        jPanel7.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 70));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(44, 58, 78));
        jLabel2.setText("Gestor");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(44, 58, 78));
        jLabel3.setText("Victor Emmanuel");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 20, -1, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/sair.png"))); // NOI18N
        jLabel4.setText("jLabel4");
        jPanel7.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 20, 40, 40));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 80));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1376, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarprojetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarprojetosActionPerformed
        DlgCadastroProjetos dlg = new DlgCadastroProjetos(this, true);
    
        // 2. Centraliza a janela na tela (opcional, mas recomendado)
        dlg.setLocationRelativeTo(null);
    
        // 3. Exibe o diálogo
        dlg.setVisible(true);
        carregarTabelaProjetos();
    }//GEN-LAST:event_btnCadastrarprojetosActionPerformed

    private void btnCadastrarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarClienteActionPerformed
        DlgCadastroCliente dlg = new DlgCadastroCliente(this, true);
    
        // 2. Centraliza a janela na tela (opcional, mas recomendado)
        dlg.setLocationRelativeTo(null);
    
        // 3. Exibe o diálogo
        dlg.setVisible(true);
        carregarTabelaClientes();
    }//GEN-LAST:event_btnCadastrarClienteActionPerformed

    private void jTProjetosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTProjetosMouseClicked
        int linha = jTProjetos.rowAtPoint(evt.getPoint());
        int coluna = jTProjetos.columnAtPoint(evt.getPoint());
        
        if (linha != -1) {
            // VERIFICA SE O CLIQUE FOI NA COLUNA 3 (Onde está o botão ABRIR)
            boolean clicouNoBotao = (coluna == 3);
            boolean duploCliqueGeral = (evt.getClickCount() == 2);
            
            // Se clicou no botão ou deu duplo clique em qualquer lugar, abre
            if (clicouNoBotao || duploCliqueGeral) {
                
                Projeto projetoCompleto = projetoModel.getProjeto(linha);
                
                view.screens.dialogs.DlgVisualizarProjeto dlg = new view.screens.dialogs.DlgVisualizarProjeto(this, true);
                dlg.setProjeto(projetoCompleto);
                dlg.setVisible(true);
                
                carregarTabelaProjetos();
            }
        }
    }//GEN-LAST:event_jTProjetosMouseClicked

    private void lblBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBuscaKeyReleased
        String textoDigitado = lblBusca.getText(); 
        
        ProjetoDAO dao = new ProjetoDAO();
        List<Projeto> resultados;

        if (textoDigitado.trim().isEmpty()) {
            resultados = dao.listarTodos();
        } else {
            resultados = dao.buscarDinamica(textoDigitado);
        }

        projetoModel.setDados(resultados);
    }//GEN-LAST:event_lblBuscaKeyReleased

    private void lblBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBuscaActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FrHome().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrarCliente;
    private javax.swing.JButton btnCadastrarprojetos;
    private javax.swing.border.EtchedBorder etchedBorder1;
    private javax.swing.border.EtchedBorder etchedBorder2;
    private javax.swing.border.EtchedBorder etchedBorder3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTClientes;
    private javax.swing.JTable jTProjetos;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField lblBusca;
    // End of variables declaration//GEN-END:variables
}
