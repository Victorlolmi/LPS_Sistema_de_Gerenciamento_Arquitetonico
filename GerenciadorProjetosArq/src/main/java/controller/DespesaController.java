/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import model.dao.DespesaDAO;
import model.entities.Despesa;
import model.entities.Projeto;
/**
 *
 * @author Viktin
 */
public class DespesaController {

    private final DespesaDAO dao;

    public DespesaController() {
        this.dao = new DespesaDAO();
    }

    /**
     * Método atualizado para receber TODOS os campos do financeiro
     */
    public boolean salvarDespesa(String descricao, String valorTexto, String categoria, 
                                 String dataTexto, String fornecedor, String status, 
                                 String formaPagamento, String observacoes, Projeto projeto) {
        
        // 1. Validações Básicas
        if (projeto == null) {
            JOptionPane.showMessageDialog(null, "Erro Crítico: Nenhum projeto vinculado a esta despesa.");
            return false;
        }
        if (descricao.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "A descrição é obrigatória.");
            return false;
        }
        // Fornecedor é importante, vamos obrigar? Por enquanto deixo opcional, mas recomendo obrigar.
        if (valorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Informe o valor.");
            return false;
        }

        try {
            Despesa despesa = new Despesa();
            
            // --- Preenchendo os dados ---
            despesa.setDescricao(descricao);
            despesa.setCategoria(categoria);
            despesa.setFornecedor(fornecedor);
            despesa.setStatus(status); // "Pago" ou "Pendente"
            despesa.setFormaPagamento(formaPagamento);
            despesa.setObservacoes(observacoes);
            despesa.setProjeto(projeto);

            // 2. Tratamento do Valor (Dinheiro)
            // Aceita "1.200,50" ou "1200.50"
            String valorLimpo = valorTexto.replace("R$", "").replace(".", "").replace(",", ".").trim();
            despesa.setValor(Double.parseDouble(valorLimpo));

            // 3. Tratamento da Data
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (dataTexto != null && !dataTexto.equals("__/__/____") && !dataTexto.trim().isEmpty()) {
                despesa.setDataDespesa(LocalDate.parse(dataTexto, dtf));
            } else {
                despesa.setDataDespesa(LocalDate.now()); // Se não preencher, usa hoje
            }

            // 4. Salvar
            dao.salvar(despesa);
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Valor inválido. Digite apenas números (ex: 150,00).");
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar despesa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirDespesa(Long id) {
        try {
            dao.remover(id);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + e.getMessage());
            return false;
        }
    }

    public List<Despesa> listarDespesasDoProjeto(Long idProjeto) {
        return dao.listarPorProjeto(idProjeto);
    }
    
    public Double buscarTotalGasto(Long idProjeto) {
        return dao.somarTotalPorProjeto(idProjeto);
    }
}
