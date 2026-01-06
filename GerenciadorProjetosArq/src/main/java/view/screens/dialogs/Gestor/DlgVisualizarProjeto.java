/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view.screens.dialogs.Gestor;

import controller.*;
import controller.tableModel.*;
import model.dao.*;
import model.entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author Viktin
 */
public class DlgVisualizarProjeto extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgVisualizarProjeto.class.getName());
    
    private Projeto projetoAtual;
    private final ProjetoController controller;
    
    // Controllers e Models
    private final DocumentoController docController;
    private final DocumentoTableModel docModel;
    
    private final DespesaController despesaController;
    private final DespesaTableModel despesaModel;
    
    private final FeedbackController feedbackController;
    private final FeedbackTableModel feedbackModel;
    
    // Estilo
    private final Color corAzulHeader = new Color(64, 86, 213);
    private final Color corFundoSelecao = new Color(240, 245, 255);
    private final javax.swing.border.Border bordaInferior = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
    
    //visualizador 
    private view.components.PainelVisualizador3D visualizador3D;
    public DlgVisualizarProjeto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        estilizarAbasModernas();
        
        // Inicializa Controllers
        this.controller = new ProjetoController(this);
        this.docController = new DocumentoController();
        this.despesaController = new DespesaController();
        this.feedbackController = new FeedbackController();
        
        // Inicializa Models
        this.docModel = new DocumentoTableModel();
        this.despesaModel = new DespesaTableModel();
        this.feedbackModel = new FeedbackTableModel();
        
        // Vincula às Tabelas
        jTDocs.setModel(docModel);
        jTable1.setModel(despesaModel); 
        jTable2.setModel(feedbackModel);
        
        // Aplica Estilo Visual
        configurarTabelaDocs();
        configurarTabelaFinanceiro();
        configurarTabelaFeedback();
        
        inicializarAba3D();
        
        // Listener para carregar dados apenas quando clicar na aba (Performance)
        jTabbedPane2.addChangeListener(evt -> {
            int index = jTabbedPane2.getSelectedIndex();
            if (projetoAtual != null && projetoAtual.getId() != null) {
                switch (index) {
                    case 2: atualizarTabelaDocumentos(); break; // Documentos
                    case 3: atualizarAbaFinanceiro(); break;    // Financeiro
                    case 4: atualizarAbaFeedback(); break;      // Feedback
                }
            }
        });
    }

    public void setProjeto(Projeto p) {
        if (p == null) return;
        this.projetoAtual = p;
        
        // Visão Geral - Labels
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String inicio = (p.getDataInicio() != null) ? dtf.format(p.getDataInicio()) : "-";
        String fim = (p.getDataPrevisao() != null) ? dtf.format(p.getDataPrevisao()) : "-";
        
        lblNomeProjeto.setText(p.getNome());
        lblNomeCliente.setText((p.getCliente() != null) ? p.getCliente().getNome() : "Sem Cliente");
        lblStatus.setText((p.getStatus() != null) ? p.getStatus() : "-");
        lblDataInicio.setText(inicio);
        lblPrevisao.setText(fim);
        
        // Orçamento
        Double valor = p.getOrcamento();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblOrcamento.setText((valor != null) ? nf.format(valor) : "R$ 0,00");
        
        // Descrição (somente leitura)
        jTDescricao.setEditable(false);
        jTDescricao.setLineWrap(true);
        jTDescricao.setWrapStyleWord(true);
        jTDescricao.setText(p.getDescricao());

        // Aba Terreno
        if (p.getTerreno() != null) {
            btnAdicionarTerreno.setText("Editar Terreno");
            btnExcluirTerreno.setVisible(true);
            atualizarDadosTerreno(p.getTerreno());
            controlarVisibilidadeCamposTerreno(true);
        } else {
            btnAdicionarTerreno.setText("Adicionar Terreno");
            btnExcluirTerreno.setVisible(false);
            controlarVisibilidadeCamposTerreno(false);
            lblNomeTerreno.setText("Nenhum terreno vinculado");
        }
        
        if (p.getCaminhoModelo3D() != null && !p.getCaminhoModelo3D().isEmpty()) {
            visualizador3D.carregarModelo(p.getCaminhoModelo3D());
        } else {
            // Se não tem, carrega a simulação padrão (passando null ou vazio)
            visualizador3D.carregarModelo(null);
        }
        
        // Atualiza Abas secundárias
        atualizarTabelaDocumentos();
        atualizarAbaFinanceiro();
        atualizarAbaFeedback();
    }
    public void atualizarAbaFinanceiro() {
        if (this.projetoAtual == null || this.projetoAtual.getId() == null) return;

        // 1. Carrega a lista de despesas na tabela (Visual)
        List<Despesa> despesas = despesaController.listarDespesasDoProjeto(this.projetoAtual.getId());
        despesaModel.setDados(despesas);

        // 2. Obtém o Orçamento Total
        Double orcamento = (this.projetoAtual.getOrcamento() != null) ? this.projetoAtual.getOrcamento() : 0.0;

        // 3. Calcula o total das Despesas lançadas (Mão de obra, materiais, etc.)
        Double totalDespesas = despesaController.buscarTotalGasto(this.projetoAtual.getId());
        
        // 4. Obtém o Custo do Terreno (NOVO CÁLCULO)
        Double custoTerreno = 0.0;
        if (this.projetoAtual.getTerreno() != null && this.projetoAtual.getTerreno().getValorCompra() != null) {
            custoTerreno = this.projetoAtual.getTerreno().getValorCompra();
        }

        // 5. Soma tudo para o Total Gasto Real
        Double totalGasto = totalDespesas + custoTerreno;
        Double saldo = orcamento - totalGasto;

        // 6. Atualiza os Labels da Interface
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblResumoOrcamento.setText(nf.format(orcamento));
        lblResumoGasto.setText(nf.format(totalGasto)); // Agora inclui o terreno
        lblResumoSaldo.setText(nf.format(saldo));

        // Cor do Saldo (Vermelho se negativo, Verde se positivo)
        lblResumoSaldo.setForeground(saldo < 0 ? Color.RED : new Color(0, 153, 51));

        // 7. Atualiza a Barra de Progresso
        if (orcamento > 0) {
            int percentual = (int) ((totalGasto / orcamento) * 100);
            barProgressoFinanceiro.setValue(percentual);
            barProgressoFinanceiro.setString(percentual + "% Usado");
            
            // Fica vermelha se estourar o orçamento
            if (percentual > 100) {
                barProgressoFinanceiro.setForeground(Color.RED);
            } else {
                barProgressoFinanceiro.setForeground(corAzulHeader);
            }
        } else {
            barProgressoFinanceiro.setValue(0);
            barProgressoFinanceiro.setString("Sem Orçamento");
        }
    }
    
    private void atualizarAbaFeedback() {
         if (this.projetoAtual == null || this.projetoAtual.getId() == null) return;
         List<Feedback> lista = feedbackController.listar(this.projetoAtual.getId());
         feedbackModel.setDados(lista);
         edtComentario.setText(""); // Limpa campo de input
    }
    
    public void atualizarTabelaDocumentos() {
        if (this.projetoAtual != null && this.projetoAtual.getId() != null) {
            List<Documento> lista = docController.listarDocumentosDoProjeto(this.projetoAtual.getId());
            docModel.setDados(lista);
        } else {
            docModel.limpar();
        }
    }
    
    private void atualizarDadosTerreno(Terreno t) {
        lblNomeTerreno.setText(t.getNome());
        lblReferenciaTerreno.setText(t.getReferencia() != null ? t.getReferencia() : "-");
        lblTopografiaTerreno.setText(t.getTopografia());
        lblTipoSoloTerreno.setText(t.getTipoSolo());
        lblCATerreno.setText(t.getCoeficienteAproveitamento() != null ? String.valueOf(t.getCoeficienteAproveitamento()) : "-");
        lblAreaTotalTerreno.setText(t.getAreaTotal() != null ? t.getAreaTotal() + " m²" : "-");
        
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblValorTerreno.setText(t.getValorCompra() != null ? nf.format(t.getValorCompra()) : "-");
        
        jTDescricaoTerreno.setText(t.getDescricao());
        jTDescricaoTerreno.setEditable(false);
        jTDescricaoTerreno.setLineWrap(true);
        jTDescricaoTerreno.setWrapStyleWord(true);
        
        Endereco e = t.getEndereco();
        if (e != null) {
            String enderecoCompleto = String.format("%s, %s, %s, %s", 
                    e.getCidade(), e.getBairro(), e.getLogradouro(), 
                    (e.getNumero() != null && !e.getNumero().isEmpty() ? e.getNumero() : "S/N"));
            lblCidadeBairroRuaNumero.setText(enderecoCompleto);
        } else {
            lblCidadeBairroRuaNumero.setText("Endereço não cadastrado");
        }
    }
    
    private void inicializarAba3D() {
        this.visualizador3D = new view.components.PainelVisualizador3D();
        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel4.add(visualizador3D, java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel panelBotoes = new javax.swing.JPanel();
        
        // Botão Carregar
        javax.swing.JButton btnCarregar = new javax.swing.JButton("Carregar/Trocar Modelo (.OBJ)");
        btnCarregar.addActionListener(e -> selecionarESalvarModelo3D());
        
        // Botão Excluir
        javax.swing.JButton btnRemover = new javax.swing.JButton("Remover Modelo");
        btnRemover.setBackground(new Color(200, 50, 50));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.addActionListener(e -> removerModelo3D());
        
        panelBotoes.add(btnCarregar);
        panelBotoes.add(btnRemover);
        jPanel4.add(panelBotoes, java.awt.BorderLayout.SOUTH);
    }
    
    private void selecionarESalvarModelo3D() {
        if (this.projetoAtual == null || this.projetoAtual.getId() == null) {
            JOptionPane.showMessageDialog(this, "Salve o projeto antes de adicionar o modelo 3D.");
            return;
        }

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Selecione o modelo 3D (.obj)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos OBJ", "obj"));
        
        int ret = fileChooser.showOpenDialog(this);
        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            String caminho = arquivo.getAbsolutePath();
            
            // 1. Atualiza visualmente na hora
            visualizador3D.carregarModelo(caminho);
            
            // 2. Salva no banco de dados
            try {
                this.projetoAtual.setCaminhoModelo3D(caminho);
                new ProjetoDAO().salvar(this.projetoAtual); // Persiste a alteração
                JOptionPane.showMessageDialog(this, "Modelo 3D vinculado ao projeto com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar vínculo no banco: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void removerModelo3D() {
        if (this.projetoAtual == null) return;
        
        if (this.projetoAtual.getCaminhoModelo3D() == null || this.projetoAtual.getCaminhoModelo3D().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há modelo 3D vinculado para remover.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja desvincular o modelo 3D deste projeto?", 
                "Remover Modelo", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // 1. Limpa no Objeto e no Banco
                this.projetoAtual.setCaminhoModelo3D(null);
                new ProjetoDAO().salvar(this.projetoAtual);
                
                // 2. Limpa visualmente (Carrega simulação)
                visualizador3D.carregarModelo(null);
                
                JOptionPane.showMessageDialog(this, "Modelo removido.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover do banco: " + ex.getMessage());
            }
        }
    }

    private void carregarArquivo3D() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Selecione um arquivo 3D (.obj, .fxml)");
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            // Manda o visualizador carregar
            visualizador3D.carregarModelo(fileToOpen.getAbsolutePath());
        }
    }

    private void controlarVisibilidadeCamposTerreno(boolean visivel) {
        // Esconde labels quando não tem terreno para não ficar "vazio"
        ref.setVisible(visivel);
        lblTopgrafia.setVisible(visivel);
        lblTipoSolo.setVisible(visivel);
        lblCA.setVisible(visivel);
        lblAreaTotal.setVisible(visivel);
        lblValor.setVisible(visivel);
        lbldescricao.setVisible(visivel);
        
        lblReferenciaTerreno.setVisible(visivel);
        lblCidadeBairroRuaNumero.setVisible(visivel);
        lblTopografiaTerreno.setVisible(visivel);
        lblTipoSoloTerreno.setVisible(visivel);
        lblCATerreno.setVisible(visivel);
        lblAreaTotalTerreno.setVisible(visivel);
        lblValorTerreno.setVisible(visivel);
        jScrollPane2.setVisible(visivel);
    }
    

    private void estilizarAbasModernas() {
        jTabbedPane2.setBackground(Color.WHITE);
        jTabbedPane2.setForeground(corAzulHeader);
        jTabbedPane2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jTabbedPane2.setOpaque(true);
        
        jTabbedPane2.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new Insets(10, 50, 10, 50);
                selectedTabPadInsets = new Insets(0, 0, 0, 0);
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? corFundoSelecao : Color.WHITE);
                g.fillRect(x, y, w, h);
            }
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(corAzulHeader);
                    g.fillRect(x, h - 3, w, 3); // Linha azul embaixo da aba ativa
                }
            }
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });
    }

    private void padronizarTabela(JTable table, JScrollPane scroll) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(corFundoSelecao);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        if (scroll != null) {
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getViewport().setBackground(Color.WHITE);
        }

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(corAzulHeader);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(100, 35));
                return label;
            }
        });
    }

    private void configurarTabelaDocs() {
        padronizarTabela(jTDocs, jScrollPane3);
        
        // Renderizador com margem esquerda (padding)
        jTDocs.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(bordaInferior, BorderFactory.createEmptyBorder(0, 10, 0, 0)));
                return this;
            }
        });
        
        if (jTDocs.getColumnCount() > 1) {
             jTDocs.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
             jTDocs.getColumnModel().getColumn(1).setPreferredWidth(300); // Nome
        }
    }
    
    private void configurarTabelaFinanceiro() {
        padronizarTabela(jTable1, JTDespesa); 
        
        // Ajuste de colunas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(90);  // Data
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150); // Descrição
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120); // Fornecedor
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100); // Categoria
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100); // Valor
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(300); // Observação
        
        // Renderer Centralizado com Condicional (Status Pendente = Vermelho)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(bordaInferior);
                
                if (!isSelected) {
                    if (column == 4 && "Pendente".equals(value)) {
                        setForeground(new Color(200, 0, 0)); 
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        setForeground(new Color(50, 50, 50));
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                return this;
            }
        };
        
        // Aplica nas colunas normais
        for (int i = 0; i <= 5; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderer Especial para Observação (Quebra de linha automática)
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new TextAreaRenderer());
    }

    private void configurarTabelaFeedback() {
        padronizarTabela(jTable2, jTFeedback); // Reutiliza estilo base
        
        // Larguras
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(80);  // Autor
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(400); // Mensagem
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(100); // Tipo
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(100); // Data
        
        // Renderer para Autor (Colorido)
        DefaultTableCellRenderer autorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(bordaInferior);
                
                if (column == 0 && !isSelected) {
                    String autor = (value != null) ? value.toString() : "";
                    if ("Gestor".equals(autor)) {
                        setForeground(new Color(30, 60, 160)); // Azul
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if ("Cliente".equals(autor)) {
                        setForeground(new Color(0, 100, 0)); // Verde
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    }
                }
                return this;
            }
        };
        
        jTable2.getColumnModel().getColumn(0).setCellRenderer(autorRenderer);
        jTable2.getColumnModel().getColumn(2).setCellRenderer(autorRenderer); // Tipo
        jTable2.getColumnModel().getColumn(3).setCellRenderer(autorRenderer); // Data
        
        // Mensagem com quebra de linha
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
    }
    
    // Classe interna auxiliar para renderizar células com quebra de linha (JTextArea dentro da JTable)
    private class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createCompoundBorder(bordaInferior, BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? corFundoSelecao : Color.WHITE);
            setText((value != null) ? value.toString() : "");
            
            // Ajuste automático de altura da linha
            int largura = table.getColumnModel().getColumn(column).getWidth();
            setSize(largura, Short.MAX_VALUE);
            int alturaIdeal = getPreferredSize().height;
            
            if (table.getRowHeight(row) != Math.max(alturaIdeal, 40)) {
                table.setRowHeight(row, Math.max(alturaIdeal, 40));
            }
            return this;
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

        jFileChooser1 = new javax.swing.JFileChooser();
        cadastroButton2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblNomeCliente = new javax.swing.JLabel();
        lblNomeProjeto = new javax.swing.JLabel();
        HomeButton = new javax.swing.JLabel();
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
        btnLancarDespesa = new javax.swing.JToggleButton();
        btnExcluirDespesa = new javax.swing.JToggleButton();
        lblResumoSaldo = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblResumoGasto = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblResumoOrcamento = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        barProgressoFinanceiro = new javax.swing.JProgressBar();
        JTDespesa = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTDocs = new javax.swing.JTable();
        btnExcluirDoc = new javax.swing.JButton();
        btnAdicionarDoc = new javax.swing.JButton();
        btnAbrirDoc = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jTFeedback = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnAdicionarFeedback = new javax.swing.JButton();
        btnExcluirFeedback = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        edtComentario = new javax.swing.JTextArea();
        HomeButton2 = new javax.swing.JButton();

        cadastroButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/BotaoVoltar.png"))); // NOI18N
        cadastroButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cadastroButton2MouseClicked(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1370, 760));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(1300, 470));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomeCliente.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        lblNomeCliente.setText("Victor Emmanuel");
        jPanel1.add(lblNomeCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 230, -1));

        lblNomeProjeto.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblNomeProjeto.setText("Casas Duplas");
        jPanel1.add(lblNomeProjeto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 870, 50));

        HomeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/BotaoVoltar.png"))); // NOI18N
        HomeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HomeButtonMouseClicked(evt);
            }
        });
        jPanel1.add(HomeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 20, 30, -1));
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
        jPanel5.add(btnAdicionarTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 360, -1, -1));

        btnExcluirTerreno.setText("Excluir Terreno");
        btnExcluirTerreno.setToolTipText("");
        btnExcluirTerreno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirTerrenoActionPerformed(evt);
            }
        });
        jPanel5.add(btnExcluirTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, 130, -1));

        lblNomeTerreno.setText("Terreno Casas Nobres");
        jPanel5.add(lblNomeTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        lblReferenciaTerreno.setText("Perto do Hospital");
        jPanel5.add(lblReferenciaTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, -1, -1));

        lblCidadeBairroRuaNumero.setText("Rio Pomba, Centro, Rua alves de Castro, 98");
        jPanel5.add(lblCidadeBairroRuaNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, -1, -1));

        lblTopografiaTerreno.setText("Plano");
        jPanel5.add(lblTopografiaTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, -1, -1));

        ref.setText("Referência:");
        jPanel5.add(ref, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        lblTopgrafia.setText("Topografia: ");
        jPanel5.add(lblTopgrafia, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, -1, -1));

        lblTipoSoloTerreno.setText("Arenoso");
        jPanel5.add(lblTipoSoloTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 110, -1, -1));

        lblTipoSolo.setText("Tipo de Solo: ");
        jPanel5.add(lblTipoSolo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 110, -1, -1));

        lblCA.setText("C.A.: ");
        jPanel5.add(lblCA, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        lblCATerreno.setText("1,5");
        jPanel5.add(lblCATerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, -1, -1));

        lblAreaTotal.setText("Área Total: ");
        jPanel5.add(lblAreaTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, -1, -1));

        lblAreaTotalTerreno.setText("245 m²");
        jPanel5.add(lblAreaTotalTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, 80, -1));

        lblValor.setText("Valor do Terreno:");
        jPanel5.add(lblValor, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 140, -1, -1));

        lblValorTerreno.setText("234,00");
        jPanel5.add(lblValorTerreno, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 140, 80, -1));

        lbldescricao.setText("Descrição:");
        jPanel5.add(lbldescricao, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, -1, -1));

        jTDescricaoTerreno.setColumns(20);
        jTDescricaoTerreno.setRows(5);
        jScrollPane2.setViewportView(jTDescricaoTerreno);

        jPanel5.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 380, 110));

        jTabbedPane2.addTab("Terreno", jPanel5);

        jPanel6.setBackground(new java.awt.Color(249, 250, 251));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnLancarDespesa.setText("Lançar Despesa");
        btnLancarDespesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLancarDespesaActionPerformed(evt);
            }
        });
        jPanel6.add(btnLancarDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 90, -1, -1));

        btnExcluirDespesa.setText("Excluir Despesa");
        btnExcluirDespesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirDespesaActionPerformed(evt);
            }
        });
        jPanel6.add(btnExcluirDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 80, -1, -1));

        lblResumoSaldo.setText("$R$ 0,00");
        jPanel6.add(lblResumoSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 30, 150, -1));

        jLabel11.setText("Saldo DIsponivel:");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 30, -1, -1));

        lblResumoGasto.setText("$R$ 0,00");
        jPanel6.add(lblResumoGasto, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 150, -1));

        jLabel12.setText("Total Gasto:");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        lblResumoOrcamento.setText("$R$ 0,00");
        jPanel6.add(lblResumoOrcamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 100, -1));

        jLabel5.setText("Orçamento Total:");
        jPanel6.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        barProgressoFinanceiro.setStringPainted(true);
        jPanel6.add(barProgressoFinanceiro, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 1340, -1));

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

        jPanel6.add(JTDespesa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 1350, 430));

        jTabbedPane2.addTab("Controle de Gastos", jPanel6);

        jPanel2.setBackground(new java.awt.Color(249, 250, 251));
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

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 1140, 560));

        btnExcluirDoc.setText("Excluir");
        btnExcluirDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnExcluirDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 140, 120, -1));

        btnAdicionarDoc.setText("Novo Documento");
        btnAdicionarDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdicionarDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 40, -1, -1));

        btnAbrirDoc.setText("Abrir");
        btnAbrirDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirDocActionPerformed(evt);
            }
        });
        jPanel2.add(btnAbrirDoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 90, 120, -1));

        jTabbedPane2.addTab("Documentos", jPanel2);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1390, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 625, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Visualizador 3D", jPanel4);

        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jTFeedback.setViewportView(jTable2);

        jPanel7.add(jTFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 730, 280));

        btnAdicionarFeedback.setText("Adicionar");
        btnAdicionarFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarFeedbackActionPerformed(evt);
            }
        });
        jPanel7.add(btnAdicionarFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 340, 100, 30));

        btnExcluirFeedback.setText("Excluir");
        btnExcluirFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirFeedbackActionPerformed(evt);
            }
        });
        jPanel7.add(btnExcluirFeedback, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 40, 90, 30));

        jLabel2.setText("Comentário:");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 320, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Diário", "problema", "Elogio", "Alteração", "Outro" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel7.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 120, 30));

        jLabel4.setText("Tipo de Apontamento");
        jPanel7.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, -1, -1));

        edtComentario.setColumns(20);
        edtComentario.setLineWrap(true);
        edtComentario.setRows(5);
        jScrollPane4.setViewportView(edtComentario);

        jPanel7.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 340, 450, 30));

        jTabbedPane2.addTab("Feedback", jPanel7);

        jPanel1.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 1390, 660));
        jTabbedPane2.getAccessibleContext().setAccessibleName("Visão Geral");

        HomeButton2.setForeground(new java.awt.Color(82, 149, 205));
        HomeButton2.setText("Voltar para Home");
        HomeButton2.setBorder(null);
        HomeButton2.setBorderPainted(false);
        HomeButton2.setContentAreaFilled(false);
        HomeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(HomeButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1310, 70, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1430, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEditarActionPerformed
        DlgCadastroProjetos dlg = new DlgCadastroProjetos(null, true);
        dlg.setProjetoParaEdicao(this.projetoAtual);
        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
        this.setProjeto(this.projetoAtual);
    }//GEN-LAST:event_bntEditarActionPerformed

    private void btnExcluirProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirProjetoActionPerformed
        controller.excluirProjeto(this.projetoAtual);
    }//GEN-LAST:event_btnExcluirProjetoActionPerformed

    private void btnAdicionarTerrenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarTerrenoActionPerformed
        DlgCadastroTerreno dlg = new DlgCadastroTerreno(null, true);
        dlg.setProjetoVinculado(this.projetoAtual);

        if (this.projetoAtual.getTerreno() != null) {
            dlg.carregarTerreno(this.projetoAtual.getTerreno());
        } 

        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true); 

        // Deixa o controller buscar os dados novos e atualizar a tela
        if (this.projetoAtual.getId() != null) {
            Projeto atualizado = controller.buscarPorId(this.projetoAtual.getId());
            this.setProjeto(atualizado);
        }
    }//GEN-LAST:event_btnAdicionarTerrenoActionPerformed

    private void btnExcluirTerrenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirTerrenoActionPerformed
    controller.excluirTerreno(this.projetoAtual);
    }//GEN-LAST:event_btnExcluirTerrenoActionPerformed

    private void btnExcluirDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirDocActionPerformed
        int linha = jTDocs.getSelectedRow();
    if (linha != -1) {
        Documento doc = docModel.getDocumento(linha);
        docController.excluirDocumento(doc, this); // O Controller cuida do resto
    } else {
        JOptionPane.showMessageDialog(this, "Selecione um documento.");
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
        
        atualizarTabelaDocumentos();
    }//GEN-LAST:event_btnAdicionarDocActionPerformed

    private void btnExcluirDespesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirDespesaActionPerformed
        int linha = jTable1.getSelectedRow();
        if (linha != -1) {
            Despesa d = despesaModel.getDespesa(linha);
            despesaController.excluirDespesa(d, this); // Chama o controller
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir.");
        }
    }//GEN-LAST:event_btnExcluirDespesaActionPerformed

    private void btnLancarDespesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLancarDespesaActionPerformed
        DlgCadastroDespesa dlg = new DlgCadastroDespesa(null, true);
        dlg.setProjetoVinculado(this.projetoAtual);
        dlg.setVisible(true);
       
        atualizarAbaFinanceiro();
    }//GEN-LAST:event_btnLancarDespesaActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btnAdicionarFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarFeedbackActionPerformed
        String tipo = (String) jComboBox1.getSelectedItem();
        String comentario = edtComentario.getText();
        // ENVIA COMO GESTOR (Padrão do Sistema Desktop)
        boolean sucesso = feedbackController.salvarFeedback(tipo, comentario, this.projetoAtual, "Gestor");
        if (sucesso) {
            atualizarAbaFeedback();
            JOptionPane.showMessageDialog(this, "Feedback registrado!");
        }
    }//GEN-LAST:event_btnAdicionarFeedbackActionPerformed

    private void btnExcluirFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirFeedbackActionPerformed
        int linha = jTable2.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um comentário.");
            return;
        }
        Feedback f = feedbackModel.getFeedback(linha);
        int confirm = JOptionPane.showConfirmDialog(this, "Excluir este comentário?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            feedbackController.excluir(f.getId());
            atualizarAbaFeedback();
        }
    }//GEN-LAST:event_btnExcluirFeedbackActionPerformed

    private void cadastroButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cadastroButton2MouseClicked
        controller.navegarParaLogin();
    }//GEN-LAST:event_cadastroButton2MouseClicked

    private void HomeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeButtonMouseClicked
      //  controller.navegarParaHome();
    }//GEN-LAST:event_HomeButtonMouseClicked

    private void HomeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeButton2ActionPerformed
        controller.navegarParaLogin();
    }//GEN-LAST:event_HomeButton2ActionPerformed

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
    private javax.swing.JLabel HomeButton;
    private javax.swing.JButton HomeButton2;
    private javax.swing.JScrollPane JTDespesa;
    private javax.swing.JProgressBar barProgressoFinanceiro;
    private javax.swing.JButton bntEditar;
    private javax.swing.JButton btnAbrirDoc;
    private javax.swing.JButton btnAdicionarDoc;
    private javax.swing.JButton btnAdicionarFeedback;
    private javax.swing.JButton btnAdicionarTerreno;
    private javax.swing.JToggleButton btnExcluirDespesa;
    private javax.swing.JButton btnExcluirDoc;
    private javax.swing.JButton btnExcluirFeedback;
    private javax.swing.JButton btnExcluirProjeto;
    private javax.swing.JButton btnExcluirTerreno;
    private javax.swing.JToggleButton btnLancarDespesa;
    private javax.swing.JLabel cadastroButton2;
    private javax.swing.JTextArea edtComentario;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTDescricao;
    private javax.swing.JTextArea jTDescricaoTerreno;
    private javax.swing.JTable jTDocs;
    private javax.swing.JScrollPane jTFeedback;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
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
