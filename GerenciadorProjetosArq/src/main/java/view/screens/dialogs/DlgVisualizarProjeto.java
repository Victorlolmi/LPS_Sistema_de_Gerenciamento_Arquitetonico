/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view.screens.dialogs;
import model.entities.Projeto;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import model.dao.ProjetoDAO;
import view.screens.dialogs.DlgCadastroTerreno;
import model.entities.Terreno;
import controller.DocumentoController; 
import controller.tableModel.DocumentoTableModel;
import model.dao.TerrenoDAO;
import controller.ProjetoController;
import java.awt.Color; 
import java.awt.Graphics;
import model.entities.Endereco;
import model.entities.Terreno;
import java.awt.Desktop; 
import java.io.File;
import java.util.List;
import model.entities.Documento;
import controller.DespesaController;
import controller.tableModel.DespesaTableModel;
import model.entities.Despesa;


/**
 *
 * @author Viktin
 */
public class DlgVisualizarProjeto extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgVisualizarProjeto.class.getName());
    private Projeto projetoAtual;
    private final ProjetoController controller;
    
    private final DocumentoController docController;
    private final DocumentoTableModel docModel;
    
    private final DespesaController despesaController;
    private final DespesaTableModel despesaModel;
    /**
     * Creates new form DlgVisualizarProjeto
     */
    public DlgVisualizarProjeto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        estilizarAbasModernas();
        
        this.controller = new ProjetoController();
        this.docController = new DocumentoController();
        this.docModel = new DocumentoTableModel();
        this.despesaController = new DespesaController();
        this.despesaModel = new DespesaTableModel();
        
        // Vincula o Model à Tabela visual
        jTDocs.setModel(docModel);
        jTable1.setModel(despesaModel);
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
                tabInsets = new java.awt.Insets(10, 30, 10, 30); // Mais gordinha e espaçada
                selectedTabPadInsets = new java.awt.Insets(0, 0, 0, 0);
                contentBorderInsets = new java.awt.Insets(0, 0, 0, 0); // Remove borda do conteúdo
            }

            // Pinta o Fundo da Aba
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                            int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(240, 245, 255)); // Azul bem clarinho quando selecionado
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(x, y, w, h);
            }

            // Remove as bordas 3D antigas e faz a linha azul embaixo
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                                        int x, int y, int w, int h, boolean isSelected) {
                // Desenha apenas uma linha azul embaixo se estiver selecionado
                if (isSelected) {
                    g.setColor(new Color(64, 86, 213));
                    g.fillRect(x, h - 3, w, 3); // Linha grossa azul na base
                }
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
               // Não desenha borda ao redor do painel principal (clean)
            }
        });
    }
    
   public void setProjeto(Projeto p) {
        if (p == null) return;
        this.projetoAtual = p;
        
        // --- ABA 1: VISÃO GERAL ---
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String inicio = (p.getDataInicio() != null) ? dtf.format(p.getDataInicio()) : "-";
        String fim = (p.getDataPrevisao() != null) ? dtf.format(p.getDataPrevisao()) : "-";
        
        lblNomeProjeto.setText(p.getNome());
        lblNomeCliente.setText((p.getCliente() != null) ? p.getCliente().getNome() : "Sem Cliente");
        lblStatus.setText((p.getStatus() != null) ? p.getStatus() : "-");
        lblDataInicio.setText(inicio);
        lblPrevisao.setText(fim);
        
        Double valor = p.getOrcamento();
        if (valor != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            lblOrcamento.setText(nf.format(valor));
        } else {
            lblOrcamento.setText("R$ 0,00");
        }
        
        jTDescricao.setEditable(false);
        jTDescricao.setLineWrap(true); 
        jTDescricao.setWrapStyleWord(true);
        jTDescricao.setText(p.getDescricao());

        // --- ABA 2: TERRENO (Atualiza os botões e os dados) ---
        if (p.getTerreno() != null) {
            btnAdicionarTerreno.setText("Editar Terreno");
            btnExcluirTerreno.setVisible(true);
            
            // CHAMA O MÉTODO QUE PREENCHE OS CAMPOS
            atualizarDadosTerreno(p.getTerreno());
            controlarVisibilidadeCamposTerreno(true);
            
        } else {
            btnAdicionarTerreno.setText("Adicionar Terreno");
            btnExcluirTerreno.setVisible(false);
            
            controlarVisibilidadeCamposTerreno(false);
            
            // 2. DEFINE A MENSAGEM DE STATUS
            lblNomeTerreno.setText("Nenhum terreno vinculado");
        }
        
        atualizarTabelaDocumentos();
        atualizarAbaFinanceiro();
    }
   
   private void atualizarAbaFinanceiro() {
        if (this.projetoAtual == null || this.projetoAtual.getId() == null) return;

        // 1. Carrega a Tabela
        List<Despesa> despesas = despesaController.listarDespesasDoProjeto(this.projetoAtual.getId());
        despesaModel.setDados(despesas);

        // 2. Busca Totais
        Double orcamento = (this.projetoAtual.getOrcamento() != null) ? this.projetoAtual.getOrcamento() : 0.0;
        Double totalGasto = despesaController.buscarTotalGasto(this.projetoAtual.getId());
        Double saldo = orcamento - totalGasto;

        // 3. Formata Moeda
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        lblResumoOrcamento.setText(nf.format(orcamento));
        lblResumoGasto.setText(nf.format(totalGasto));
        lblResumoSaldo.setText(nf.format(saldo));

        // 4. Cor do Saldo
        if (saldo < 0) {
            lblResumoSaldo.setForeground(Color.RED);
        } else {
            lblResumoSaldo.setForeground(new Color(0, 153, 51)); // Verde
        }

        // 5. Barra de Progresso
        if (orcamento > 0) {
            int percentual = (int) ((totalGasto / orcamento) * 100);
            barProgressoFinanceiro.setValue(percentual);
            barProgressoFinanceiro.setString(percentual + "% Usado");
            
            // Se passar de 100%, pinta a barra de vermelho
            if (percentual > 100) {
                barProgressoFinanceiro.setForeground(Color.RED);
            } else {
                barProgressoFinanceiro.setForeground(new Color(64, 86, 213)); // Azul padrão
            }
        } else {
            barProgressoFinanceiro.setValue(0);
            barProgressoFinanceiro.setString("Sem Orçamento");
        }
    }
   
   private void controlarVisibilidadeCamposTerreno(boolean visivel) {
        // Rótulos Fixos (Labels como "Topografia:", "C.A.:", etc)
        ref.setVisible(visivel);
        lblTopgrafia.setVisible(visivel);
        lblTipoSolo.setVisible(visivel);
        lblCA.setVisible(visivel);
        lblAreaTotal.setVisible(visivel);
        lblValor.setVisible(visivel);
        lbldescricao.setVisible(visivel);
        
        // Campos de Valor (Onde aparecem os dados)
        lblReferenciaTerreno.setVisible(visivel);
        lblCidadeBairroRuaNumero.setVisible(visivel);
        lblTopografiaTerreno.setVisible(visivel);
        lblTipoSoloTerreno.setVisible(visivel);
        lblCATerreno.setVisible(visivel);
        lblAreaTotalTerreno.setVisible(visivel);
        lblValorTerreno.setVisible(visivel);
        jScrollPane2.setVisible(visivel); // Área da descrição
    }
   
   private void atualizarTabelaDocumentos() {
        if (this.projetoAtual != null && this.projetoAtual.getId() != null) {
            List<Documento> lista = docController.listarDocumentosDoProjeto(this.projetoAtual.getId());
            docModel.setDados(lista);
        } else {
            docModel.limpar();
        }
    }
    
    // --- NOVO MÉTODO: Preenche os labels da aba Terreno ---
    private void atualizarDadosTerreno(Terreno t) {
        lblNomeTerreno.setText(t.getNome());
        lblReferenciaTerreno.setText(t.getReferencia() != null ? t.getReferencia() : "-");
        lblTopografiaTerreno.setText(t.getTopografia());
        lblTipoSoloTerreno.setText(t.getTipoSolo());
        
        // Números
        lblCATerreno.setText(t.getCoeficienteAproveitamento() != null ? String.valueOf(t.getCoeficienteAproveitamento()) : "-");
        lblAreaTotalTerreno.setText(t.getAreaTotal() != null ? t.getAreaTotal() + " m²" : "-");
        
        // Valor Monetário
        if (t.getValorCompra() != null) {
             NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
             lblValorTerreno.setText(nf.format(t.getValorCompra()));
        } else {
            lblValorTerreno.setText("-");
        }
        
        // Descrição
        jTDescricaoTerreno.setText(t.getDescricao());
        jTDescricaoTerreno.setEditable(false);
        jTDescricaoTerreno.setLineWrap(true);
        jTDescricaoTerreno.setWrapStyleWord(true);
        
        // Montagem do Endereço Completo
        Endereco e = t.getEndereco();
        if (e != null) {
            String enderecoCompleto = String.format("%s, %s, %s, %s", 
                    e.getCidade(), 
                    e.getBairro(), 
                    e.getLogradouro(), 
                    (e.getNumero() != null && !e.getNumero().isEmpty() ? e.getNumero() : "S/N")
            );
            lblCidadeBairroRuaNumero.setText(enderecoCompleto);
        } else {
            lblCidadeBairroRuaNumero.setText("Endereço não cadastrado");
        }
    }

    // --- NOVO MÉTODO: Limpa os labels se o terreno for excluído ---
    private void limparDadosTerreno() {
        lblNomeTerreno.setText("Nenhum terreno vinculado");
        lblReferenciaTerreno.setText("-");
        lblCidadeBairroRuaNumero.setText("-");
        lblTopografiaTerreno.setText("-");
        lblTipoSoloTerreno.setText("-");
        lblCATerreno.setText("-");
        lblAreaTotalTerreno.setText("-");
        lblValorTerreno.setText("-");
        jTDescricaoTerreno.setText("");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        lblNomeCliente = new javax.swing.JLabel();
        lblNomeProjeto = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDataInicio = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblPrevisao = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblOrcamento = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTDescricao = new javax.swing.JTextArea();
        bntEditar = new javax.swing.JButton();
        btnExcluirProjeto = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnAdicionarTerreno = new javax.swing.JButton();
        btnExcluirTerreno = new javax.swing.JButton();
        lblNomeTerreno = new javax.swing.JLabel();
        lblReferenciaTerreno = new javax.swing.JLabel();
        lblCidadeBairroRuaNumero = new javax.swing.JLabel();
        lblTopografiaTerreno = new javax.swing.JLabel();
        ref = new javax.swing.JLabel();
        lblTopgrafia = new javax.swing.JLabel();
        lblTipoSoloTerreno = new javax.swing.JLabel();
        lblTipoSolo = new javax.swing.JLabel();
        lblCA = new javax.swing.JLabel();
        lblCATerreno = new javax.swing.JLabel();
        lblAreaTotal = new javax.swing.JLabel();
        lblAreaTotalTerreno = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        lblValorTerreno = new javax.swing.JLabel();
        lbldescricao = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTDescricaoTerreno = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        lblResumoSaldo = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblResumoOrcamento = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblResumoGasto = new javax.swing.JLabel();
        barProgressoFinanceiro = new javax.swing.JProgressBar();
        JTDespesa = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnExcluirDespesa = new javax.swing.JToggleButton();
        btnLancarDespesa = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTDocs = new javax.swing.JTable();
        btnExcluirDoc = new javax.swing.JButton();
        btnAdicionarDoc = new javax.swing.JButton();
        btnAbrirDoc = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomeCliente.setText("Victor Emmanuel");
        jPanel1.add(lblNomeCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 230, -1));

        lblNomeProjeto.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblNomeProjeto.setText("Casas Duplas");
        jPanel1.add(lblNomeProjeto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 570, -1));

        jLabel3.setText("Cliente:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));
        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 260, -1, -1));

        jTabbedPane2.setBackground(new java.awt.Color(249, 250, 251));

        jPanel3.setBackground(new java.awt.Color(249, 250, 251));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Informações do Projeto");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel7.setText("Status:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, -1, -1));

        lblStatus.setText("Status");
        jPanel3.add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 120, -1));

        jLabel9.setText("Descrição:");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jLabel8.setText("Data de Início:");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblDataInicio.setText("01/12/2023");
        jPanel3.add(lblDataInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, -1, -1));

        jLabel6.setText("Previsão de fim:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        lblPrevisao.setText("01/12/2024");
        jPanel3.add(lblPrevisao, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, -1, -1));

        jLabel10.setText("Orçamento:");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        lblOrcamento.setText("30,232");
        jPanel3.add(lblOrcamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 100, -1));

        jTDescricao.setColumns(20);
        jTDescricao.setRows(5);
        jScrollPane1.setViewportView(jTDescricao);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 420, 190));

        bntEditar.setText("Editar");
        bntEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntEditarActionPerformed(evt);
            }
        });
        jPanel3.add(bntEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 110, -1));

        btnExcluirProjeto.setText("Excluir Projeto");
        btnExcluirProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirProjetoActionPerformed(evt);
            }
        });
        jPanel3.add(btnExcluirProjeto, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 350, 130, -1));

        jTabbedPane2.addTab("Visão Geral", jPanel3);

        jPanel5.setBackground(new java.awt.Color(249, 250, 251));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAdicionarTerreno.setText("Adicionar Terreno");
        btnAdicionarTerreno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarTerrenoActionPerformed(evt);
            }
        });
        jPanel5.add(btnAdicionarTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, -1, -1));

        btnExcluirTerreno.setText("Excluir Terreno");
        btnExcluirTerreno.setToolTipText("");
        btnExcluirTerreno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirTerrenoActionPerformed(evt);
            }
        });
        jPanel5.add(btnExcluirTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 360, 130, -1));

        lblNomeTerreno.setText("Terreno Casas Nobres");
        jPanel5.add(lblNomeTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        lblReferenciaTerreno.setText("Perto do Hospital");
        jPanel5.add(lblReferenciaTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        lblCidadeBairroRuaNumero.setText("Rio Pomba, Centro, Rua alves de Castro, 98");
        jPanel5.add(lblCidadeBairroRuaNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblTopografiaTerreno.setText("Plano");
        jPanel5.add(lblTopografiaTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, -1, -1));

        ref.setText("Referência:");
        jPanel5.add(ref, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        lblTopgrafia.setText("Topografia: ");
        jPanel5.add(lblTopgrafia, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        lblTipoSoloTerreno.setText("Arenoso");
        jPanel5.add(lblTipoSoloTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, -1, -1));

        lblTipoSolo.setText("Tipo de Solo: ");
        jPanel5.add(lblTipoSolo, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, -1, -1));

        lblCA.setText("C.A.: ");
        jPanel5.add(lblCA, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        lblCATerreno.setText("1,5");
        jPanel5.add(lblCATerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        lblAreaTotal.setText("Área Total: ");
        jPanel5.add(lblAreaTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, -1, -1));

        lblAreaTotalTerreno.setText("245 m²");
        jPanel5.add(lblAreaTotalTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 80, -1));

        lblValor.setText("Valor do Terreno:");
        jPanel5.add(lblValor, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 140, -1, -1));

        lblValorTerreno.setText("234,00");
        jPanel5.add(lblValorTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, 80, -1));

        lbldescricao.setText("Descrição:");
        jPanel5.add(lbldescricao, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        jTDescricaoTerreno.setColumns(20);
        jTDescricaoTerreno.setRows(5);
        jScrollPane2.setViewportView(jTDescricaoTerreno);

        jPanel5.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 380, 110));

        jTabbedPane2.addTab("Terreno", jPanel5);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblResumoSaldo.setText("$R$ 0,00");
        jPanel6.add(lblResumoSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 150, -1));

        jLabel5.setText("Orçamento Total:");
        jPanel6.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        jLabel11.setText("Saldo DIsponivel:");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        lblResumoOrcamento.setText("$R$ 0,00");
        jPanel6.add(lblResumoOrcamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 100, -1));

        jLabel12.setText("Total Gasto:");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        lblResumoGasto.setText("$R$ 0,00");
        jPanel6.add(lblResumoGasto, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 150, -1));

        barProgressoFinanceiro.setStringPainted(true);
        jPanel6.add(barProgressoFinanceiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 230, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        JTDespesa.setViewportView(jTable1);

        jPanel6.add(JTDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 590, 310));

        btnExcluirDespesa.setText("Excluir Despesa");
        btnExcluirDespesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirDespesaActionPerformed(evt);
            }
        });
        jPanel6.add(btnExcluirDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, -1, -1));

        btnLancarDespesa.setText("Lançar Despesa");
        btnLancarDespesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLancarDespesaActionPerformed(evt);
            }
        });
        jPanel6.add(btnLancarDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, -1, -1));

        jTabbedPane2.addTab("Controle de Gastos", jPanel6);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTDocs.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTDocs);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 510, 316));

        btnExcluirDoc.setText("Excluir");
        btnExcluirDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnExcluirDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 340, 120, -1));

        btnAdicionarDoc.setText("Novo Documento");
        btnAdicionarDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdicionarDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, -1, -1));

        btnAbrirDoc.setText("Abrir");
        btnAbrirDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnAbrirDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 340, 120, -1));

        jTabbedPane2.addTab("Documentos", jPanel2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Visualizador 3D", jPanel4);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Feedback", jPanel7);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 900, 450));
        jTabbedPane2.getAccessibleContext().setAccessibleName("Visão Geral");

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 930, 610));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEditarActionPerformed
        DlgCadastroProjetos dlg = new DlgCadastroProjetos(null, true);
        
        // 2. Passa o projeto atual para a tela de cadastro preencher os campos
        dlg.setProjetoParaEdicao(this.projetoAtual);
        
        // 3. Centraliza e Exibe
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        
        // 4. ATUALIZAR A TELA DE VISUALIZAÇÃO (Pós-Edição)
        // Quando o 'dlg' fecha (após salvar), o código continua aqui.
        // O objeto 'this.projetoAtual' foi modificado na memória pelo Hibernate/Java.
        // Chamamos o setProjeto de novo para atualizar os Labels com os novos dados.
        this.setProjeto(this.projetoAtual);
    }//GEN-LAST:event_bntEditarActionPerformed

    private void btnExcluirProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirProjetoActionPerformed
        if (this.projetoAtual == null || this.projetoAtual.getId() == null) {
            JOptionPane.showMessageDialog(this, "Erro: Nenhum projeto selecionado.");
            return;
        }

        // 2. Confirmação do Usuário
        int confirmacao = JOptionPane.showConfirmDialog(
            this, 
            "Tem certeza que deseja EXCLUIR o projeto '" + projetoAtual.getNome() + "'?\nEssa ação não pode ser desfeita.", 
            "Excluir Projeto", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        // Se o usuário clicou em SIM (YES_OPTION = 0)
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                // 3. Chama o DAO para remover
                ProjetoDAO dao = new ProjetoDAO();
                dao.remover(projetoAtual.getId());
                
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
                
                // 4. Fecha a janela de detalhes, pois o projeto sumiu
                this.dispose();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnExcluirProjetoActionPerformed

    private void btnAdicionarTerrenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarTerrenoActionPerformed
        DlgCadastroTerreno dlg = new DlgCadastroTerreno(null, true);
        
        dlg.setProjetoVinculado(this.projetoAtual);
        
        if (this.projetoAtual.getTerreno() != null) {
            dlg.carregarTerreno(this.projetoAtual.getTerreno());
        } 
        
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true); 
        
        if (this.projetoAtual.getId() != null) {
            Projeto projetoAtualizado = controller.buscarPorId(this.projetoAtual.getId());
            this.setProjeto(projetoAtualizado);
        }
    }//GEN-LAST:event_btnAdicionarTerrenoActionPerformed

    private void btnExcluirTerrenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirTerrenoActionPerformed
        if (this.projetoAtual.getTerreno() == null) return;

        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover o terreno deste projeto?\nTodos os dados do terreno serão perdidos.",
                "Excluir Terreno",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                Long idTerreno = this.projetoAtual.getTerreno().getId();

                this.projetoAtual.setTerreno(null);
                new ProjetoDAO().salvar(this.projetoAtual);
                new TerrenoDAO().remover(idTerreno);

                JOptionPane.showMessageDialog(this, "Terreno removido com sucesso!");

                Projeto projetoAtualizado = controller.buscarPorId(this.projetoAtual.getId());
                this.setProjeto(projetoAtualizado);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir terreno: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnExcluirTerrenoActionPerformed

    private void btnExcluirDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirDocActionPerformed
        int linhaSelecionada = jTDocs.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um documento para excluir.");
            return;
        }
        
        Documento doc = docModel.getDocumento(linhaSelecionada);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja excluir o documento '" + doc.getNome() + "'?", 
                "Excluir", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = docController.excluirDocumento(doc.getId());
            if (sucesso) {
                atualizarTabelaDocumentos();
                JOptionPane.showMessageDialog(this, "Documento removido.");
            }
        }
    }//GEN-LAST:event_btnExcluirDocActionPerformed

    private void btnAbrirDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirDocActionPerformed
        int linhaSelecionada = jTDocs.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um documento na tabela para abrir.");
            return;
        }
        
        Documento doc = docModel.getDocumento(linhaSelecionada);
        File arquivo = new File(doc.getCaminhoArquivo());
        
        if (arquivo.exists()) {
            try {
                // Comando mágico do Java para abrir o arquivo com o programa padrão do Windows
                Desktop.getDesktop().open(arquivo);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar abrir o arquivo: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Arquivo não encontrado no computador!\nCaminho: " + doc.getCaminhoArquivo());
        }
    }//GEN-LAST:event_btnAbrirDocActionPerformed

    private void btnAdicionarDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarDocActionPerformed
        DlgCadastroDocumento dlg = new DlgCadastroDocumento(null, true);
        dlg.setProjetoVinculado(this.projetoAtual);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        
        // Ao voltar, atualiza a tabela
        atualizarTabelaDocumentos();
    }//GEN-LAST:event_btnAdicionarDocActionPerformed

    private void btnExcluirDespesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirDespesaActionPerformed
        int linha = jTable1.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir.");
            return;
        }
        
        Despesa d = despesaModel.getDespesa(linha);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja excluir a despesa: " + d.getDescricao() + "?", 
                "Excluir Gasto", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = despesaController.excluirDespesa(d.getId());
            if (sucesso) {
                atualizarAbaFinanceiro(); // Recalcula tudo
                JOptionPane.showMessageDialog(this, "Despesa excluída.");
            }
        }
    }//GEN-LAST:event_btnExcluirDespesaActionPerformed

    private void btnLancarDespesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLancarDespesaActionPerformed
        DlgCadastroDespesa dlg = new DlgCadastroDespesa(null, true);
        dlg.setProjetoVinculado(this.projetoAtual);
        dlg.setVisible(true);
        
        // Ao voltar, atualiza o dashboard financeiro
        atualizarAbaFinanceiro();
    }//GEN-LAST:event_btnLancarDespesaActionPerformed

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
                DlgVisualizarProjeto dialog = new DlgVisualizarProjeto(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane JTDespesa;
    private javax.swing.JProgressBar barProgressoFinanceiro;
    private javax.swing.JButton bntEditar;
    private javax.swing.JButton btnAbrirDoc;
    private javax.swing.JButton btnAdicionarDoc;
    private javax.swing.JButton btnAdicionarTerreno;
    private javax.swing.JToggleButton btnExcluirDespesa;
    private javax.swing.JButton btnExcluirDoc;
    private javax.swing.JButton btnExcluirProjeto;
    private javax.swing.JButton btnExcluirTerreno;
    private javax.swing.JToggleButton btnLancarDespesa;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTDescricao;
    private javax.swing.JTextArea jTDescricaoTerreno;
    private javax.swing.JTable jTDocs;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblAreaTotal;
    private javax.swing.JLabel lblAreaTotalTerreno;
    private javax.swing.JLabel lblCA;
    private javax.swing.JLabel lblCATerreno;
    private javax.swing.JLabel lblCidadeBairroRuaNumero;
    private javax.swing.JLabel lblDataInicio;
    private javax.swing.JLabel lblNomeCliente;
    private javax.swing.JLabel lblNomeProjeto;
    private javax.swing.JLabel lblNomeTerreno;
    private javax.swing.JLabel lblOrcamento;
    private javax.swing.JLabel lblPrevisao;
    private javax.swing.JLabel lblReferenciaTerreno;
    private javax.swing.JLabel lblResumoGasto;
    private javax.swing.JLabel lblResumoOrcamento;
    private javax.swing.JLabel lblResumoSaldo;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTipoSolo;
    private javax.swing.JLabel lblTipoSoloTerreno;
    private javax.swing.JLabel lblTopgrafia;
    private javax.swing.JLabel lblTopografiaTerreno;
    private javax.swing.JLabel lblValor;
    private javax.swing.JLabel lblValorTerreno;
    private javax.swing.JLabel lbldescricao;
    private javax.swing.JLabel ref;
    // End of variables declaration//GEN-END:variables
}
