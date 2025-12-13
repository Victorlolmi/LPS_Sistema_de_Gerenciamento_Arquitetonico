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
/**
 *
 * @author juans
 */
public class FrHome extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrHome.class.getName());

    /**
     * Creates new form FrProjetos
     */
    public FrHome() {
        initComponents();
        setLocationRelativeTo(null);
        estilizarAbasModernas();
        configurarTabelaClientes();
        carregarTabelaClientes();
        
        configurarTabelaProjetos();
        carregarTabelaProjetos();
    }
    private void estilizarAbasModernas() {
        // 1. Cores e Fontes Globais do Painel
        jTabbedPane2.setBackground(Color.WHITE);
        jTabbedPane2.setForeground(new Color(64, 86, 213)); // Azul do seu tema
        jTabbedPane2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTabbedPane2.setOpaque(true);
        
        // 2. Customização Profunda (UI Manager)
        jTabbedPane2.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            
            // Define o Padding (Espaçamento interno) da aba
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new java.awt.Insets(10, 25, 10, 25); // Mais gordinha e espaçada
                selectedTabPadInsets = new java.awt.Insets(0, 0, 0, 0);
                contentBorderInsets = new java.awt.Insets(0, 0, 0, 0); // Remove borda do conteúdo
            }

            // Pinta o Fundo da Aba
            @Override
            protected void paintTabBackground(java.awt.Graphics g, int tabPlacement, int tabIndex, 
                                            int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(240, 245, 255)); // Azul bem clarinho quando selecionado
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x, y, w, h);
            }

            // Remove as bordas 3D antigas
            @Override
            protected void paintTabBorder(java.awt.Graphics g, int tabPlacement, int tabIndex, 
                                        int x, int y, int w, int h, boolean isSelected) {
                // Desenha apenas uma linha azul embaixo se estiver selecionado
                if (isSelected) {
                    g.setColor(new Color(64, 86, 213));
                    g.fillRect(x, h - 3, w, 3); // Linha grossa azul na base
                }
            }
            
            @Override
            protected void paintContentBorder(java.awt.Graphics g, int tabPlacement, int selectedIndex) {
               // Não desenha borda ao redor do painel principal
            }
        });
    }
    
    private void configurarTabelaClientes() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTClientes.setModel(modelo);
        modelo.setColumnCount(0); 
        
        // Definição das Colunas
        modelo.addColumn("ID");      // 0: Oculto
        modelo.addColumn("Nome");    // 1: Botão Azul
        modelo.addColumn("CPF");     // 2: Centralizado
        modelo.addColumn("E-mail");  // 3: Centralizado
        modelo.addColumn("Cidade");  // 4: Centralizado
        
        // --- ESCONDER COLUNA ID ---
        jTClientes.getColumnModel().getColumn(0).setMinWidth(0);
        jTClientes.getColumnModel().getColumn(0).setMaxWidth(0);
        jTClientes.getColumnModel().getColumn(0).setWidth(0);
        
        // --- LARGURAS ---
        jTClientes.getColumnModel().getColumn(1).setPreferredWidth(250); // Nome
        jTClientes.getColumnModel().getColumn(2).setPreferredWidth(120); // CPF
        jTClientes.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        jTClientes.getColumnModel().getColumn(4).setPreferredWidth(150); // Cidade

        // --- RENDERIZADOR DO BOTÃO (Coluna 1 - Nome) ---
        jTClientes.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7));
            final JButton btn = new JButton();

            {
                // Estilo igual ao de Projetos
                btn.setPreferredSize(new Dimension(220, 30)); 
                btn.setBackground(new Color(64, 86, 213));    
                btn.setForeground(Color.WHITE);               
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
                btn.setFocusPainted(false);
                btn.setBorderPainted(false); 
                panel.add(btn);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Texto do botão é o Nome do Cliente
                btn.setText((value != null) ? value.toString() : "Cliente");
                
                if (isSelected) {
                    panel.setBackground(table.getSelectionBackground());
                } else {
                    panel.setBackground(table.getBackground());
                }
                return panel; 
            }
        });

        // --- CENTRALIZAR DEMAIS COLUNAS ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        jTClientes.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTClientes.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTClientes.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // --- VISUAL GERAL ---
        jTClientes.setRowHeight(45); // Espaçamento maior
        jTClientes.setShowVerticalLines(false);
        jTClientes.setGridColor(new Color(230, 230, 230));
    }
    
    public void carregarTabelaClientes() {
        // 1. Instancia o DAO
        ClienteDAO dao = new ClienteDAO();
        
        // 2. Busca a lista do banco
        List<Cliente> lista = dao.listarTodos();
        
        // 3. Pega o modelo da tabela para manipular
        DefaultTableModel modelo = (DefaultTableModel) jTClientes.getModel();
        modelo.setNumRows(0); // Limpa a tabela para não duplicar dados ao recarregar
        
        // 4. Adiciona linha por linha
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getCpf(),
                c.getEmail(),
                // Verifica se tem endereço antes de pegar cidade para não dar erro
                (c.getEndereco() != null) ? c.getEndereco().getCidade() : "N/A"
            });
        }
    }
    private void configurarTabelaProjetos() {
        // 1. Modelo (Mantém igual - bloqueia edição)
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTProjetos.setModel(modelo);
        modelo.setColumnCount(0); 
        
        // 2. Colunas
        modelo.addColumn("ID");             
        modelo.addColumn("Projeto"); 
        modelo.addColumn("Cliente");       
        modelo.addColumn("Status");         
        
        // 3. Esconder ID
        jTProjetos.getColumnModel().getColumn(0).setMinWidth(0);
        jTProjetos.getColumnModel().getColumn(0).setMaxWidth(0);
        jTProjetos.getColumnModel().getColumn(0).setWidth(0);
        
        // 4. Larguras (Ajustei levemente para dar mais espaço ao cliente)
        jTProjetos.getColumnModel().getColumn(1).setPreferredWidth(200); // Coluna do botão
        jTProjetos.getColumnModel().getColumn(2).setPreferredWidth(250); // Cliente maior
        jTProjetos.getColumnModel().getColumn(3).setPreferredWidth(100); // Status

        // --- MÁGICA DO BOTÃO AZUL ---
        
        jTProjetos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            // Criamos um Painel para servir de fundo e centralizar
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7)); // O '7' ajusta o alinhamento vertical
            // Criamos o botão
            final JButton btn = new JButton();

            // Bloco de inicialização (roda uma vez na criação)
            {
                // ESTILO DO BOTÃO
                btn.setPreferredSize(new Dimension(255, 30)); // Tamanho harmônico
                btn.setBackground(new Color(64, 86, 213));    // Azul (Mesmo do botão 'Novo Projeto')
                btn.setForeground(Color.WHITE);               // Texto Branco
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fonte mais bonita e negrito
                
                // Remove bordas feias de foco
                btn.setFocusPainted(false);
                btn.setBorderPainted(false); // Deixa o botão "chapado" (flat), mais moderno
                
                // Adiciona o botão no painel
                panel.add(btn);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Texto do botão (Nome do Projeto)
                btn.setText((value != null) ? value.toString() : "Abrir");
                
                // --- CONTROLE DE CORES DO FUNDO (Painel) ---
                if (isSelected) {
                    panel.setBackground(table.getSelectionBackground());
                } else {
                    // Alternar cores das linhas (Zebrado) para facilitar leitura?
                    // Se quiser linhas alternadas, descomente a lógica abaixo:
                    /*
                    if (row % 2 == 0) {
                        panel.setBackground(Color.WHITE);
                    } else {
                        panel.setBackground(new Color(245, 245, 245)); // Cinza bem clarinho
                    }
                    */
                    panel.setBackground(table.getBackground());
                }
                
                return panel; 
            }
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        // Definimos o alinhamento horizontal para o CENTRO
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Aplicamos esse renderizador nas colunas de Cliente (2) e Status (3)
        jTProjetos.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTProjetos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // --- ESPAÇAMENTO DA TABELA ---
        // Aumenta a altura da linha. 45px deixa o visual muito mais limpo e "respirável"
        jTProjetos.setRowHeight(45); 
        
        // Opcional: Remove as linhas de grade verticais para um visual mais limpo
        jTProjetos.setShowVerticalLines(false);
        // Cor da linha de grade horizontal
        jTProjetos.setGridColor(new Color(230, 230, 230));
    }
    public void carregarTabelaProjetos() {
        ProjetoDAO dao = new ProjetoDAO();
        List<Projeto> lista = dao.listarTodos();
        
        DefaultTableModel modelo = (DefaultTableModel) jTProjetos.getModel();
        modelo.setNumRows(0);
        
        for (Projeto p : lista) {
            String nomeCliente = (p.getCliente() != null) ? p.getCliente().getNome() : "---";
            String statusTexto = (p.getStatus() != null) ? p.getStatus() : "Indefinido";

            modelo.addRow(new Object[]{
                p.getId(),    // <--- ID entra na tabela (mas está escondido)
                p.getNome(),
                nomeCliente,
                statusTexto
            });
        }
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
        jPanel2 = new javax.swing.JPanel();
        btnClientes = new javax.swing.JButton();
        btnProjetos = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnCadastrarprojetos = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTProjetos = new javax.swing.JTable();
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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClientes.setText("Clientes");
        btnClientes.setContentAreaFilled(false);
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        jPanel2.add(btnClientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 218, 50));

        btnProjetos.setText("Projetos");
        btnProjetos.setContentAreaFilled(false);
        btnProjetos.setInheritsPopupMenu(true);
        btnProjetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProjetosActionPerformed(evt);
            }
        });
        jPanel2.add(btnProjetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 218, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 230, 680));

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
        jPanel3.add(btnCadastrarprojetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 10, 130, 40));

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

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 950, 410));

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

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 950, 410));

        btnCadastrarCliente.setBackground(new java.awt.Color(64, 86, 213));
        btnCadastrarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCadastrarCliente.setText("Novo Cliente");
        btnCadastrarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarClienteActionPerformed(evt);
            }
        });
        jPanel4.add(btnCadastrarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 10, 130, 40));

        jTabbedPane2.addTab("Clientes", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1120, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab3", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1120, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab4", jPanel6);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, 1120, 630));
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

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 80));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1389, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        jTabbedPane2.setSelectedIndex(1);
        carregarTabelaClientes();
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnCadastrarprojetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarprojetosActionPerformed
        DlgCadastroProjetos dlg = new DlgCadastroProjetos(this, true);
    
        // 2. Centraliza a janela na tela (opcional, mas recomendado)
        dlg.setLocationRelativeTo(null);
    
        // 3. Exibe o diálogo
        dlg.setVisible(true);
        carregarTabelaProjetos();
    }//GEN-LAST:event_btnCadastrarprojetosActionPerformed

    private void btnProjetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProjetosActionPerformed
        jTabbedPane2.setSelectedIndex(0);
        carregarTabelaProjetos();
    }//GEN-LAST:event_btnProjetosActionPerformed

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
        
        // Verifica se clicou numa linha válida
        if (linha != -1) {
            
            // LÓGICA:
            // Se clicou na Coluna 1 (onde está o botão), basta 1 clique.
            // Se clicou nas outras colunas (Cliente/Status), exige 2 cliques (opcional).
            
            boolean clicouNoBotao = (coluna == 1);
            boolean duploCliqueGeral = (evt.getClickCount() == 2);
            
            // Se foi clique no botão OU duplo clique no resto da linha...
            if (clicouNoBotao || duploCliqueGeral) {
                
                // 2. Pega o ID (Coluna 0 escondida)
                Long idProjeto = (Long) jTProjetos.getValueAt(linha, 0);
                
                // 3. Busca no banco
                ProjetoDAO dao = new ProjetoDAO();
                Projeto projetoCompleto = dao.buscarPorId(idProjeto);
                
                // 4. Abre visualização
                view.screens.dialogs.DlgVisualizarProjeto dlg = new view.screens.dialogs.DlgVisualizarProjeto(this, true);
                dlg.setProjeto(projetoCompleto);
                dlg.setVisible(true);
                carregarTabelaProjetos();
            }
        }
    }//GEN-LAST:event_jTProjetosMouseClicked

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
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnProjetos;
    private javax.swing.border.EtchedBorder etchedBorder1;
    private javax.swing.border.EtchedBorder etchedBorder2;
    private javax.swing.border.EtchedBorder etchedBorder3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTClientes;
    private javax.swing.JTable jTProjetos;
    private javax.swing.JTabbedPane jTabbedPane2;
    // End of variables declaration//GEN-END:variables
}
