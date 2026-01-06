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
        
        // Verifica se tem CPF e Endereço (Evita NullPointerException)
        jLabel1.setText(c.getCpf() != null ? c.getCpf() : "-"); // jLabel1 é o CPF
        
        if (c.getEndereco() != null) {
            Endereco e = c.getEndereco();
            String endCompleto = String.format("%s, %s - %s, %s", 
                    e.getLogradouro(), e.getNumero(), e.getBairro(), e.getCidade());
            jLabel2.setText(endCompleto); // jLabel2 é o Endereço
            jLabel4.setText(e.getCep());  // jLabel4 é o CEP
        } else {
            jLabel2.setText("Endereço não cadastrado");
            jLabel4.setText("-");
        }

        // 2. Carregar Indicadores e Tabela
        carregarDadosFinanceirosEProjetos();
    }

    private void carregarDadosFinanceirosEProjetos() {
        if (clienteAtual == null || clienteAtual.getId() == null) return;

        List<Projeto> projetos = projetoDAO.buscarPorClienteId(clienteAtual.getId());
        
        // --- CORREÇÃO: Usa o método setDados do HistoricoTableModel ---
        historicoModel.setDados(projetos);
        
        // Cálculo de KPIs
        double valorTotal = 0.0;
        int qtdAtivos = 0;
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        if (projetos != null && !projetos.isEmpty()) {
            for (Projeto p : projetos) {
                if (p.getOrcamento() != null) {
                    valorTotal += p.getOrcamento();
                }
                if ("Em Andamento".equalsIgnoreCase(p.getStatus()) || "Pendente".equalsIgnoreCase(p.getStatus())) {
                    qtdAtivos++;
                }
            }
            
            lblSomaOrcamneto.setText(nf.format(valorTotal));
            double ticketMedio = valorTotal / projetos.size();
            lblticket.setText(nf.format(ticketMedio));
            lblquantidadeProjetos.setText(String.valueOf(qtdAtivos));
        } else {
            lblSomaOrcamneto.setText("R$ 0,00");
            lblticket.setText("R$ 0,00");
            lblquantidadeProjetos.setText("0");
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

        lblNomeCli = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelaHistorico = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblSomaOrcamneto = new javax.swing.JLabel();
        lblticket = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblquantidadeProjetos = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1403, 760));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomeCli.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblNomeCli.setText("Lucas Cabrine");
        getContentPane().add(lblNomeCli, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel1.setText("13879875693");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 20, 80, -1));

        jLabel2.setText("Rio Pomba, Centro, Rua alves de Castro, 98");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 310, -1));

        jLabel3.setText("email@gmail.com");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        jLabel4.setText("36180000");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 20, -1, -1));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 1080, 370));

        jLabel5.setText("Tcket Médio: ");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, -1, -1));

        jLabel6.setText("Valor Total em Contratos:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        lblSomaOrcamneto.setText("3000000");
        getContentPane().add(lblSomaOrcamneto, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 130, -1));

        lblticket.setText("3000000");
        getContentPane().add(lblticket, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 130, -1));

        jLabel8.setText("Projetos Ativos: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 110, -1, -1));

        lblquantidadeProjetos.setText("34");
        getContentPane().add(lblquantidadeProjetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 110, -1, -1));

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabelaHistorico;
    private javax.swing.JLabel lblNomeCli;
    private javax.swing.JLabel lblSomaOrcamneto;
    private javax.swing.JLabel lblquantidadeProjetos;
    private javax.swing.JLabel lblticket;
    // End of variables declaration//GEN-END:variables
}
