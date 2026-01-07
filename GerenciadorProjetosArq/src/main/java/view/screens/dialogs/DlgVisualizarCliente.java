/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view.screens.dialogs;
import controller.tableModel.HistoricoTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import model.dao.ProjetoDAO;
import model.entities.Cliente;
import model.entities.Endereco;
import model.entities.Projeto;
import model.entities.Usuario;
/**
 *
 * @author Viktin
 */
public class DlgVisualizarCliente extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgVisualizarCliente.class.getName());
    private Cliente clienteAtual;
    private Usuario usuarioLogado; 
    private final ProjetoDAO projetoDAO;
    
    // Model Customizado
    private final HistoricoTableModel historicoModel;
    
    // Cores para estilização
    private final Color corAzulHeader = new Color(64, 86, 213);
    private final Color corFundoSelecao = new Color(240, 245, 255); 
    private final Color corBotaoVer = new Color(65, 105, 225);
    private final javax.swing.border.Border bordaInferior = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
    private final Color corAzulEscuro = new Color(30, 60, 160);
    /**
     * Creates new form DlgVisualizarCliente
     */
    public DlgVisualizarCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.projetoDAO = new ProjetoDAO();
        
        // 1. Instancia o Model Customizado
        this.historicoModel = new HistoricoTableModel();
        
        // 2. Vincula o model à tabela ANTES de configurar layout
        jTabelaHistorico.setModel(historicoModel);
        
        setLocationRelativeTo(null);
    
        configurarTabela(); // Configura renderers e larguras
        
        adicionarEventoCliqueTabela();
    }
    
    private void adicionarEventoCliqueTabela() {
        jTabelaHistorico.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int linha = jTabelaHistorico.rowAtPoint(evt.getPoint());
                int coluna = jTabelaHistorico.columnAtPoint(evt.getPoint());

                if (linha != -1) {
                    // A coluna 4 é onde está o botão "Visualizar"
                    boolean clicouNoBotao = (coluna == 4);
                    boolean duploClique = (evt.getClickCount() == 2);

                    if (clicouNoBotao || duploClique) {
                        Projeto projetoSelecionado = historicoModel.getProjeto(linha);

                        // Abre o DlgVisualizarProjeto
                        DlgVisualizarProjeto dlg = new DlgVisualizarProjeto((java.awt.Frame) getParent(), true, usuarioLogado);
                        dlg.setProjeto(projetoSelecionado);
                        dlg.setVisible(true);
                        
                        // Recarrega os dados ao voltar (caso algo tenha mudado no projeto)
                        carregarDadosFinanceirosEProjetos();
                    }
                }
            }
        });
    }
    
 
    
    public void setCliente(Cliente c) {
        this.clienteAtual = c;
        if (c == null) return;

        // 1. Preencher Informações Básicas (Mapeando seus Labels)
        lblNomeCli.setText(c.getNome());
        jLabel3.setText(c.getEmail()); // jLabel3 é o Email no seu layout
       

        // 2. Carregar Indicadores e Tabela
        carregarDadosFinanceirosEProjetos();
    }

    private void carregarDadosFinanceirosEProjetos() {
        if (clienteAtual == null || clienteAtual.getId() == null) return;

        List<Projeto> projetos = projetoDAO.buscarPorClienteId(clienteAtual.getId());
        
        // Atualiza a tabela visual
        historicoModel.setDados(projetos);
        
        // Variáveis para os cálculos
        double valorTotal = 0.0;
        int qtdAtivos = 0;
        int qtdConcluidos = 0; // Nova variável para contar os concluídos
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        if (projetos != null && !projetos.isEmpty()) {
            for (Projeto p : projetos) {
                // 1. Soma do Orçamento
                if (p.getOrcamento() != null) {
                    valorTotal += p.getOrcamento();
                }
                
                // 2. Lógica de Contagem de Status
                String status = p.getStatus();
                if (status != null) {
                    // Normaliza para facilitar a comparação (remove espaços extras)
                    status = status.trim();
                    
                    // Lógica para PROJETOS ATIVOS (Andamento + Planejamento)
                    if (status.equalsIgnoreCase("Em Andamento") || 
                        status.equalsIgnoreCase("Planejamento") || 
                        status.equalsIgnoreCase("Em Planejamento")) {
                        qtdAtivos++;
                    }
                    // Lógica para PROJETOS CONCLUÍDOS
                    else if (status.equalsIgnoreCase("Concluido") || 
                             status.equalsIgnoreCase("Concluído")) {
                        qtdConcluidos++;
                    }
                }
            }
            
            // Atualiza os Labels na tela
            lblSomaOrcamneto.setText(nf.format(valorTotal));
            
            // Evita divisão por zero no ticket médio
            double ticketMedio = (projetos.size() > 0) ? valorTotal / projetos.size() : 0.0;
            lblticket.setText(nf.format(ticketMedio));
            
            lblquantidadeProjetosAtivos.setText(String.valueOf(qtdAtivos));
            lblquantidadeProjetosConcluidos.setText(String.valueOf(qtdConcluidos));
            
        } else {
            // Caso não tenha projetos, zera tudo
            lblSomaOrcamneto.setText("R$ 0,00");
            lblticket.setText("R$ 0,00");
            lblquantidadeProjetosAtivos.setText("0");
            lblquantidadeProjetosConcluidos.setText("0");
        }
    }

    private void configurarTabela() {
        // Estilo Geral
        jTabelaHistorico.setRowHeight(45);
        jTabelaHistorico.setShowVerticalLines(false);
        jTabelaHistorico.setGridColor(new Color(230, 230, 230));
        jTabelaHistorico.setIntercellSpacing(new Dimension(0, 0));
        jTabelaHistorico.setSelectionBackground(corFundoSelecao);
        jTabelaHistorico.setSelectionForeground(Color.BLACK);
        
        // Scroll
        if(jScrollPane1 != null) {
            jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
            jScrollPane1.getViewport().setBackground(Color.WHITE);
        }

        // Header
        jTabelaHistorico.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(corAzulHeader);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 40));
                return label;
            }
        });

        // Configuração de Colunas
        if (jTabelaHistorico.getColumnCount() > 0) {
            jTabelaHistorico.getColumnModel().getColumn(0).setPreferredWidth(250); // Nome
            jTabelaHistorico.getColumnModel().getColumn(1).setPreferredWidth(120); // Status
            jTabelaHistorico.getColumnModel().getColumn(2).setPreferredWidth(100); // Inicio
            jTabelaHistorico.getColumnModel().getColumn(3).setPreferredWidth(100); // Orçamento
            jTabelaHistorico.getColumnModel().getColumn(4).setPreferredWidth(120); // Botão
            
            // Renderers
            jTabelaHistorico.getColumnModel().getColumn(0).setCellRenderer(criarRendererTexto());
            jTabelaHistorico.getColumnModel().getColumn(1).setCellRenderer(criarRendererStatus());
            jTabelaHistorico.getColumnModel().getColumn(2).setCellRenderer(criarRendererTextoCentro());
            jTabelaHistorico.getColumnModel().getColumn(3).setCellRenderer(criarRendererTextoCentro());
            jTabelaHistorico.getColumnModel().getColumn(4).setCellRenderer(criarRendererBotao());
        }
    }
    
    private DefaultTableCellRenderer criarRendererTexto() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createCompoundBorder(bordaInferior, BorderFactory.createEmptyBorder(0, 10, 0, 0)));
                return this;
            }
        };
    }
    
    private DefaultTableCellRenderer criarRendererTextoCentro() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(bordaInferior);
                return this;
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
                label.setPreferredSize(new Dimension(100, 25));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(new Color(225, 235, 255), 2));
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
        };
    }
    
    private DefaultTableCellRenderer criarRendererBotao() {
        return new DefaultTableCellRenderer() {
            final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7));
            final JButton btn = new JButton("Visualizar");
            {
                btn.setPreferredSize(new Dimension(100, 30));
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelaHistorico = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblNomeCli = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblSomaOrcamneto = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        lblticket = new javax.swing.JLabel();
        lblquantidadeProjetosConcluidos = new javax.swing.JLabel();
        lblquantidadeProjetosAtivos = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabelaHistorico.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTabelaHistorico);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 1000, 400));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomeCli.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblNomeCli.setText("Lucas Cabrine");
        jPanel1.add(lblNomeCli, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 970, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel3.setText("email@gmail.com");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 260, 20));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel2.setText("Rio Pomba, Centro, Rua alves de Castro, 98");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 690, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(204, 204, 204), new java.awt.Color(204, 204, 204), null, null));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(64, 86, 213));
        jLabel10.setText("Projetos Ativos");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(64, 86, 213));
        jLabel6.setText("Valor Total em Contratos");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 230, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(64, 86, 213));
        jLabel5.setText("Ticket Médio");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(64, 86, 213));
        jLabel11.setText("Projetos Concluidos");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, -1, -1));

        lblSomaOrcamneto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSomaOrcamneto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSomaOrcamneto.setText("3000000");
        jPanel2.add(lblSomaOrcamneto, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 260, -1));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 10, 120));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 0, 10, 120));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 10, 120));

        lblticket.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblticket.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblticket.setText("3000000");
        jPanel2.add(lblticket, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 60, 190, -1));

        lblquantidadeProjetosConcluidos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblquantidadeProjetosConcluidos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblquantidadeProjetosConcluidos.setText("3");
        jPanel2.add(lblquantidadeProjetosConcluidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 60, 110, -1));

        lblquantidadeProjetosAtivos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblquantidadeProjetosAtivos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblquantidadeProjetosAtivos.setText("34");
        jPanel2.add(lblquantidadeProjetosAtivos, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 60, 110, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 1000, 120));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 10, 120));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 660));

        jLabel9.setText("Projetos Ativos");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 110, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DlgVisualizarCliente dialog = new DlgVisualizarCliente(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable jTabelaHistorico;
    private javax.swing.JLabel lblNomeCli;
    private javax.swing.JLabel lblSomaOrcamneto;
    private javax.swing.JLabel lblquantidadeProjetosAtivos;
    private javax.swing.JLabel lblquantidadeProjetosConcluidos;
    private javax.swing.JLabel lblticket;
    // End of variables declaration//GEN-END:variables
}
