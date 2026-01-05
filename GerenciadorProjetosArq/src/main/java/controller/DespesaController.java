package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import model.dao.DespesaDAO;
import model.entities.Despesa;
import model.entities.Projeto;
import view.screens.dialogs.Gestor.DlgVisualizarProjeto;

/**
 * @author Viktin
 */
public class DespesaController {

    private final DespesaDAO dao;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DespesaController() {
        this.dao = new DespesaDAO();
    }

    public boolean salvarDespesa(String descricao, String valorTexto, String categoria, 
                                 String dataTexto, String fornecedor, String status, 
                                 String formaPagamento, String observacoes, Projeto projeto) {
        
        try {
            if (projeto == null) {
                JOptionPane.showMessageDialog(null, "Erro: A despesa deve estar vinculada a um projeto.");
                return false;
            }
            
            if (descricao == null || descricao.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "A descrição da despesa é obrigatória.");
                return false;
            }

            Despesa despesa = new Despesa();
            despesa.setDescricao(descricao.trim());
            despesa.setCategoria(categoria);
            despesa.setFornecedor(fornecedor != null ? fornecedor.trim() : "");
            despesa.setStatus(status);
            despesa.setFormaPagamento(formaPagamento);
            despesa.setObservacoes(observacoes);
            despesa.setProjeto(projeto);

            String valorLimpo = valorTexto.replace("R$", "").replace(".", "").replace(",", ".").trim();
            despesa.setValor(Double.valueOf(valorLimpo));

            if (dataTexto == null || dataTexto.contains("_") || dataTexto.trim().isEmpty()) {
                despesa.setDataDespesa(LocalDate.now());
            } else {
                despesa.setDataDespesa(LocalDate.parse(dataTexto, DATE_FORMATTER));
            }

            dao.salvar(despesa);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui uma despesa e solicita que a View se atualize.
     */
    public void excluirDespesa(Despesa d, DlgVisualizarProjeto view) {
        if (d == null) return;

        int confirm = JOptionPane.showConfirmDialog(view, 
                "Deseja excluir a despesa: " + d.getDescricao() + "?", 
                "Excluir Gasto", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.remover(d.getId());
                
                // Agora funciona porque tornamos o método public na View
                view.atualizarAbaFinanceiro(); 
                
                JOptionPane.showMessageDialog(view, "Despesa excluída com sucesso.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erro ao excluir: " + e.getMessage());
            }
        }
    }

    public List<Despesa> listarDespesasDoProjeto(Long idProjeto) {
        return dao.listarPorProjeto(idProjeto);
    }
    
    public Double buscarTotalGasto(Long idProjeto) {
        Double total = dao.somarTotalPorProjeto(idProjeto);
        return total != null ? total : 0.0;
    }
}