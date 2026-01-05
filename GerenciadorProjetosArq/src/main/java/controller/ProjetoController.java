package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import model.dao.ProjetoDAO;
import model.dao.ClienteDAO;
import model.dao.TerrenoDAO;
import model.entities.Cliente;
import model.entities.Projeto;
import view.screens.dialogs.Gestor.DlgCadastroProjetos;
import view.screens.dialogs.Gestor.DlgVisualizarProjeto;

/**
 * @author Viktin 
 */
public class ProjetoController {

    private DlgCadastroProjetos cadastroView;
    private DlgVisualizarProjeto visualizarView;
    private final ProjetoDAO dao;
    private final ClienteDAO clienteDAO;
    private final TerrenoDAO terrenoDAO;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProjetoController(DlgCadastroProjetos view) {
        this.cadastroView = view;
        this.dao = new ProjetoDAO();
        this.clienteDAO = new ClienteDAO();
        this.terrenoDAO = new TerrenoDAO();
        inicializarCadastro();
    }

    public ProjetoController(DlgVisualizarProjeto view) {
        this.visualizarView = view;
        this.dao = new ProjetoDAO();
        this.clienteDAO = new ClienteDAO();
        this.terrenoDAO = new TerrenoDAO();
    }

    private void inicializarCadastro() {
        if (cadastroView != null) {
            List<Cliente> clientes = clienteDAO.listarTodos();
            cadastroView.atualizarComboClientes(clientes);
        }
    }

    public void preencherCamposEdicao(Projeto p) {
        if (cadastroView == null || p == null) return;

        cadastroView.setTitle("Editar Projeto: " + p.getNome());
        
        // NOTE: Acesso direto aos setters da View (Swing style).
        cadastroView.setProjetoCampos(
            p.getNome(),
            p.getDescricao(),
            p.getStatus(),
            p.getOrcamento() != null ? String.format("%.2f", p.getOrcamento()) : "0,00",
            p.getDataInicio() != null ? p.getDataInicio().format(dtf) : "",
            p.getDataPrevisao() != null ? p.getDataPrevisao().format(dtf) : "",
            p.getCliente()
        );
    }

    public Projeto buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public void excluirProjeto(Projeto projeto) {
        if (projeto == null || projeto.getId() == null) {
            JOptionPane.showMessageDialog(visualizarView, "Erro: Nenhum projeto selecionado.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(visualizarView, 
            "Tem certeza que deseja EXCLUIR o projeto '" + projeto.getNome() + "'?\nEssa ação não pode ser desfeita.", 
            "Excluir Projeto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                dao.remover(projeto.getId());
                JOptionPane.showMessageDialog(visualizarView, "Projeto excluído com sucesso!");
                visualizarView.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(visualizarView, "Erro ao excluir: " + e.getMessage());
            }
        }
    }

    public void excluirTerreno(Projeto projeto) {
        if (projeto == null || projeto.getTerreno() == null) return;

        int confirmacao = JOptionPane.showConfirmDialog(visualizarView, 
                "Tem certeza que deseja remover o terreno deste projeto?", "Excluir Terreno",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                Long idTerreno = projeto.getTerreno().getId();
                projeto.setTerreno(null);
                dao.salvar(projeto);
                terrenoDAO.remover(idTerreno);
                JOptionPane.showMessageDialog(visualizarView, "Terreno removido!");
                visualizarView.setProjeto(dao.buscarPorId(projeto.getId()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(visualizarView, "Erro: " + e.getMessage());
            }
        }
    }

    private LocalDate converterData(String data) {
        if (data == null || data.trim().isEmpty()) return null;
        
        try {
            return LocalDate.parse(data.trim(), dtf);
        } catch (Exception e) {
            System.err.println("Erro ao converter data: " + data + " | " + e.getMessage());
            return null; 
        }
    }

    private boolean isDataValida(String data) {
        if (data == null) return false;
        String limpa = data.replace("/", "").trim();
        return !limpa.isEmpty() && limpa.length() == 8;
    }

    public void salvarProjeto() {
        if (cadastroView.getNome().trim().isEmpty()) {
            cadastroView.exibeMensagem("O nome do projeto é obrigatório!");
            return;
        }

        Projeto projeto = (cadastroView.getProjetoEmEdicao() != null) 
                          ? cadastroView.getProjetoEmEdicao() 
                          : new Projeto();

        try {
            projeto.setNome(cadastroView.getNome());
            projeto.setDescricao(cadastroView.getDescricao());
            projeto.setStatus(cadastroView.getStatus());
            projeto.setCliente(cadastroView.getClienteSelecionado());

            String strDataIn = cadastroView.getDataInicio();
            if (isDataValida(strDataIn)) {
                LocalDate dtIn = converterData(strDataIn);
                if (dtIn != null) {
                    projeto.setDataInicio(dtIn);
                } else {
                    cadastroView.exibeMensagem("Data de Início inválida!");
                    return;
                }
            } else {
                cadastroView.exibeMensagem("Data de Início é obrigatória!");
                return;
            }

            String strDataPrev = cadastroView.getDataPrevisao();
            if (isDataValida(strDataPrev)) {
                projeto.setDataPrevisao(converterData(strDataPrev));
            }

            projeto.setOrcamento(converterMoeda(cadastroView.getOrcamento()));

            dao.salvar(projeto);
            cadastroView.exibeMensagem("Projeto salvo com sucesso!");
            cadastroView.dispose();

        } catch (Exception e) {
            cadastroView.exibeMensagem("Erro ao processar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Double converterMoeda(String valor) {
        if (valor == null || valor.trim().isEmpty()) return 0.0;
        try {
            // Normaliza formato pt-BR (1.234,56) para o parse do Double
            return Double.parseDouble(valor.replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}