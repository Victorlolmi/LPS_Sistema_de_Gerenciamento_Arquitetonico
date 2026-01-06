/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.screens.dialogs;

import view.screens.dialogs.DlgCadastroProjetos;
import view.screens.dialogs.DlgCadastroCliente;
import view.screens.dialogs.DlgVisualizarProjeto;
import model.dao.ClienteDAO;
import model.dao.GestorDAO;

import controller.tableModel.GestorTableModel;
import model.dao.GestorDAO; // Certifique-se que esta classe existe
import model.entities.Gestor;
import model.entities.Cliente;
import model.entities.Usuario;
import model.entities.Gestor;

import model.dao.ProjetoDAO;
import model.entities.Projeto;
import controller.tableModel.ProjetoTableModel;
import controller.tableModel.ClienteTableModel;

import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author juans
 */
public class FrHome extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrHome.class.getName());
    
    private final Usuario usuarioLogado;
    
    private final ProjetoTableModel projetoModel;
    private final ClienteTableModel clienteModel;
    private final controller.tableModel.GestorTableModel gestorModel;
    
    private final Color corAzulEscuro = new Color(30, 60, 160);
    private final Color corSelecao = new Color(240, 247, 255);
    private final Color corBotaoVer = new Color(65, 105, 225);
    private final javax.swing.border.Border bordaInferior = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
    
    public FrHome(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        initComponents();
        setLocationRelativeTo(null);
        estilizarAbasModernas();
        
        configurarVisaoUsuario();
        
        // Inicializa Models
        this.projetoModel = new ProjetoTableModel();
        this.clienteModel = new ClienteTableModel();
        this.gestorModel = new controller.tableModel.GestorTableModel(); 
        
        jTProjetos.setModel(projetoModel);
        jTClientes.setModel(clienteModel);
        jTGestores.setModel(gestorModel);
        
        // Configura Layout das Tabelas
        configurarTabelaProjetos();
        configurarTabelaClientes();
        configurarTabelaGestores(); 
        
        configurarPlaceholders();
        
        // Carrega Dados Iniciais
        carregarTabelaClientes();
        carregarTabelaProjetos();
        carregarTabelaGestores();
    }
    
    // aqui é onde eu atualizo as informações para o usuario especifico
    private void configurarVisaoUsuario() {
        
        jLabel3.setText(usuarioLogado.getNome()); 

        if (usuarioLogado instanceof Gestor) {
            lblTipo_usuario.setText("Gestor");
            if (jTabbedPane2.getTabCount() > 2) {
                jTabbedPane2.removeTabAt(2); 
            }
            
        } else {
            lblTipo_usuario.setText("Cliente");
            
            btnCadastrarprojetos.setVisible(false); 
            btnCadastrarCliente.setVisible(false); 

            if (jTabbedPane2.getTabCount() > 1) {
                jTabbedPane2.removeTabAt(1); 
            }
        }
    }
   
   private void padronizarLayoutTabela(JTable table, JScrollPane scroll) {
        table.setRowHeight(50); // Altura confortável
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(corSelecao);
        table.setSelectionForeground(Color.BLACK);
        
        if(scroll != null) {
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getViewport().setBackground(Color.WHITE);
        }

        // Cabeçalho Personalizado
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

    private void configurarTabelaProjetos() {
        padronizarLayoutTabela(jTProjetos, jScrollPane2);

        // Larguras
        jTProjetos.getColumnModel().getColumn(0).setPreferredWidth(200); // Projeto
        jTProjetos.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        jTProjetos.getColumnModel().getColumn(2).setPreferredWidth(120); // Status
        jTProjetos.getColumnModel().getColumn(3).setPreferredWidth(120); // Botão

        // Renderers
        jTProjetos.getColumnModel().getColumn(0).setCellRenderer(criarRendererTexto());
        jTProjetos.getColumnModel().getColumn(1).setCellRenderer(criarRendererTexto());
        jTProjetos.getColumnModel().getColumn(2).setCellRenderer(criarRendererStatus()); // Pill Style
        jTProjetos.getColumnModel().getColumn(3).setCellRenderer(criarRendererBotao());  // Button Style
    }

    private void configurarTabelaClientes() {
        padronizarLayoutTabela(jTClientes, jScrollPane1);

        jTClientes.getColumnModel().getColumn(0).setPreferredWidth(200); 
        jTClientes.getColumnModel().getColumn(1).setPreferredWidth(120); 
        jTClientes.getColumnModel().getColumn(2).setPreferredWidth(200); 
        jTClientes.getColumnModel().getColumn(3).setPreferredWidth(150); 
        jTClientes.getColumnModel().getColumn(4).setPreferredWidth(120); 

        jTClientes.getColumnModel().getColumn(0).setCellRenderer(criarRendererTexto());
        jTClientes.getColumnModel().getColumn(1).setCellRenderer(criarRendererTexto());
        jTClientes.getColumnModel().getColumn(2).setCellRenderer(criarRendererTexto());
        jTClientes.getColumnModel().getColumn(3).setCellRenderer(criarRendererTexto());
        jTClientes.getColumnModel().getColumn(4).setCellRenderer(criarRendererBotao());
    }
    private void configurarTabelaGestores() {
        padronizarLayoutTabela(jTGestores, jScrollPane3);

        jTGestores.getColumnModel().getColumn(0).setPreferredWidth(200); 
        jTGestores.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTGestores.getColumnModel().getColumn(2).setPreferredWidth(200); 
        jTGestores.getColumnModel().getColumn(3).setPreferredWidth(120); 

        jTGestores.getColumnModel().getColumn(0).setCellRenderer(criarRendererTexto());
        jTGestores.getColumnModel().getColumn(1).setCellRenderer(criarRendererTexto());
        jTGestores.getColumnModel().getColumn(2).setCellRenderer(criarRendererTexto());
        jTGestores.getColumnModel().getColumn(3).setCellRenderer(criarRendererBotao());
    }
    

    private DefaultTableCellRenderer criarRendererTexto() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(isSelected ? Color.BLACK : new Color(50, 50, 50));
                // Padding left 20px
                setBorder(BorderFactory.createCompoundBorder(bordaInferior, BorderFactory.createEmptyBorder(0, 20, 0, 0)));
                return this;
            }
        };
    }

    private DefaultTableCellRenderer criarRendererBotao() {
        return new DefaultTableCellRenderer() {
            // Cria o painel e o botão uma única vez
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            final JButton btn = new JButton("Ver Detalhes");
            {
                btn.setPreferredSize(new Dimension(110, 30));
                btn.setBackground(corBotaoVer);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
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
    
    private DefaultTableCellRenderer criarRendererStatus() {
        return new DefaultTableCellRenderer() {
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            final JLabel label = new JLabel();
            {
                label.setOpaque(true);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setPreferredSize(new Dimension(120, 25));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(new Color(225, 235, 255), 2));
                panel.add(label);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (value != null) ? value.toString() : "-";
                label.setText(status);
                
                // Estilo "Pill" (Cápsula)
                label.setBackground(new Color(225, 235, 255));
                label.setForeground(corAzulEscuro);
                
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                panel.setBorder(bordaInferior);
                return panel;
            }
        };
    }
    

    // --- CARREGAMENTO DE DADOS ---

    public void carregarTabelaProjetos() {
        ProjetoDAO dao = new ProjetoDAO();
        List<Projeto> lista;

        if (usuarioLogado instanceof Cliente) {
            Cliente clienteLogado = (Cliente) usuarioLogado;
            lista = dao.buscarPorClienteId(clienteLogado.getId());
            
        } else if (usuarioLogado instanceof Gestor) {
            lista = dao.buscarPorGestorId(usuarioLogado.getId());
            
        } else {
            lista = dao.listarTodos();
        }

        projetoModel.setDados(lista);
    }
    
    public void carregarTabelaClientes() {
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> lista;
        
        if (usuarioLogado instanceof Gestor) {
            lista = dao.buscarPorGestor(usuarioLogado.getId());
            
        } else {
            lista = dao.listarTodos();
        }
        
        clienteModel.setDados(lista);
    }

    public void carregarTabelaGestores() {
        GestorDAO dao = new GestorDAO();
        List<Gestor> lista;
        if (usuarioLogado instanceof Cliente) {
            lista = dao.buscarPorCliente(usuarioLogado.getId());
        } else {
            lista = dao.listarTodos();
        }

        gestorModel.setDados(lista);
    }
    // --- UI HELPERS ---

    private void estilizarAbasModernas() {
        jTabbedPane2.setBackground(Color.WHITE);
        jTabbedPane2.setForeground(new Color(64, 86, 213)); 
        jTabbedPane2.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
    
    private void configurarPlaceholders() {
        // Configura Busca de Projetos
        String phProjetos = "  Buscar por projeto...";
        lblBusca.setText(phProjetos);
        lblBusca.setForeground(new Color(150, 150, 150));
        adicionarEfeitoPlaceholder(lblBusca, phProjetos);

        // Configura Busca de Clientes
        String phClientes = "  Buscar por nome...";
        lblBuscaCliente.setText(phClientes);
        lblBuscaCliente.setForeground(new Color(150, 150, 150));
        adicionarEfeitoPlaceholder(lblBuscaCliente, phClientes);
        
        // Configura Busca de Gestores
        String phGestores = "  Buscar gestor por nome...";
        lblBuscaGestor.setText(phGestores);
        lblBuscaGestor.setForeground(new Color(150, 150, 150));
        adicionarEfeitoPlaceholder(lblBuscaGestor, phGestores);
    }
    private void adicionarEfeitoPlaceholder(javax.swing.JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Se o texto for igual ao placeholder, limpa e põe cor preta
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Se saiu e tá vazio, repõe o placeholder e a cor cinza
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
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
        lblBuscaCliente = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lblBuscaGestor = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTGestores = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTipo_usuario = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sairButton = new javax.swing.JLabel();

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

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1270, 540));

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

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 1170, 410));

        btnCadastrarCliente.setBackground(new java.awt.Color(64, 86, 213));
        btnCadastrarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnCadastrarCliente.setText("Novo Cliente");
        btnCadastrarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarClienteActionPerformed(evt);
            }
        });
        jPanel4.add(btnCadastrarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 20, 130, 40));

        lblBuscaCliente.setToolTipText("");
        lblBuscaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblBuscaClienteActionPerformed(evt);
            }
        });
        lblBuscaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblBuscaClienteKeyReleased(evt);
            }
        });
        jPanel4.add(lblBuscaCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 340, 30));

        jTabbedPane2.addTab("Clientes", jPanel4);

        lblBuscaGestor.setToolTipText("");
        lblBuscaGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblBuscaGestorActionPerformed(evt);
            }
        });
        lblBuscaGestor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblBuscaGestorKeyReleased(evt);
            }
        });

        jTGestores.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTGestores);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBuscaGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(156, 156, 156))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblBuscaGestor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Gestores", jPanel5);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1360, 660));
        jTabbedPane2.getAccessibleContext().setAccessibleName("Projetos");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Logo.png"))); // NOI18N
        jPanel7.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 70));

        lblTipo_usuario.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblTipo_usuario.setForeground(new java.awt.Color(44, 58, 78));
        lblTipo_usuario.setText("Gestor");
        jPanel7.add(lblTipo_usuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(44, 58, 78));
        jLabel3.setText("Victor Emmanuel");
        jPanel7.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 20, -1, -1));

        sairButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/sair.png"))); // NOI18N
        sairButton.setText("jLabel4");
        sairButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sairButtonMouseClicked(evt);
            }
        });
        jPanel7.add(sairButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 20, 40, 40));

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

    private void sairButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sairButtonMouseClicked
        int confirmacao = javax.swing.JOptionPane.showConfirmDialog(
                this, 
                "Tem certeza que deseja sair do sistema?", 
                "Sair", 
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == javax.swing.JOptionPane.YES_OPTION) {
            
            view.screens.FrLogin telaLogin = new view.screens.FrLogin();
            telaLogin.setVisible(true);
            
            this.dispose();
        }
    }//GEN-LAST:event_sairButtonMouseClicked

    private void lblBuscaClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBuscaClienteKeyReleased
        String texto = lblBuscaCliente.getText();

        // Ignora se for o texto do placeholder
        if (texto.equals("  Buscar por nome...")) return;

        ClienteDAO dao = new ClienteDAO();
        List<Cliente> resultados;

        if (texto.trim().isEmpty()) {
            // Se estiver vazio, traz tudo
            resultados = dao.listarTodos();
        } else {
            // Se tiver texto, busca APENAS pelo nome
            resultados = dao.buscarPorNome(texto);
        }

        // Atualiza a tabela com o resultado
        clienteModel.setDados(resultados);
    }//GEN-LAST:event_lblBuscaClienteKeyReleased

    private void lblBuscaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBuscaClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBuscaClienteActionPerformed

    private void btnCadastrarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarClienteActionPerformed
        DlgCadastroCliente dlg = new DlgCadastroCliente(this, true);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        carregarTabelaClientes();
    }//GEN-LAST:event_btnCadastrarClienteActionPerformed

    private void lblBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBuscaKeyReleased
        String texto = lblBusca.getText();

        // Evita buscar o texto do placeholder
        if (texto.contains("Buscar por")) return;

        ProjetoDAO dao = new ProjetoDAO();
        List<Projeto> resultados;

        if (texto.trim().isEmpty()) {
            resultados = dao.listarTodos();
        } else {
            resultados = dao.buscarDinamica(texto);
        }
        projetoModel.setDados(resultados);
    }//GEN-LAST:event_lblBuscaKeyReleased

    private void lblBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBuscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBuscaActionPerformed

    private void jTProjetosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTProjetosMouseClicked
        int linha = jTProjetos.rowAtPoint(evt.getPoint());
        int coluna = jTProjetos.columnAtPoint(evt.getPoint());

        if (linha != -1) {
            // Coluna 3 é onde está o botão "Ver Detalhes"
            boolean clicouNoBotao = (coluna == 3);
            boolean duploClique = (evt.getClickCount() == 2);

            if (clicouNoBotao || duploClique) {
                Projeto projetoSelecionado = projetoModel.getProjeto(linha);

                // Adicionei 'this.usuarioLogado' como terceiro parâmetro
                DlgVisualizarProjeto dlg = new DlgVisualizarProjeto(this, true, this.usuarioLogado);

                dlg.setProjeto(projetoSelecionado);
                dlg.setVisible(true);

                // Recarrega a tabela na volta (caso tenha editado/excluido)
                carregarTabelaProjetos();
            }
        }
    }//GEN-LAST:event_jTProjetosMouseClicked

    private void btnCadastrarprojetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarprojetosActionPerformed
        DlgCadastroProjetos dlg = new DlgCadastroProjetos(this, true, this.usuarioLogado);
        
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        carregarTabelaProjetos();
    }//GEN-LAST:event_btnCadastrarprojetosActionPerformed

    private void lblBuscaGestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblBuscaGestorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBuscaGestorActionPerformed

    private void lblBuscaGestorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBuscaGestorKeyReleased
        String texto = lblBuscaGestor.getText();

        if (texto.contains("Buscar")) return;

        model.dao.GestorDAO dao = new model.dao.GestorDAO();
        java.util.List<model.entities.Gestor> resultados;

        if (texto.trim().isEmpty()) {
            resultados = dao.listarTodos();
        } else {
            resultados = dao.buscarPorNome(texto);
        }

        gestorModel.setDados(resultados);
    }//GEN-LAST:event_lblBuscaGestorKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            // Precisamos simular um usuário ou passar null
            new FrHome(null).setVisible(true); 
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrarCliente;
    private javax.swing.JButton btnCadastrarprojetos;
    private javax.swing.border.EtchedBorder etchedBorder1;
    private javax.swing.border.EtchedBorder etchedBorder2;
    private javax.swing.border.EtchedBorder etchedBorder3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTClientes;
    private javax.swing.JTable jTGestores;
    private javax.swing.JTable jTProjetos;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField lblBusca;
    private javax.swing.JTextField lblBuscaCliente;
    private javax.swing.JTextField lblBuscaGestor;
    private javax.swing.JLabel lblTipo_usuario;
    private javax.swing.JLabel sairButton;
    // End of variables declaration//GEN-END:variables
}
