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
 * @author Viktin
 */
public class DespesaController {

    private final DespesaDAO dao;

    public DespesaController() {
        this.dao = new DespesaDAO();
    }

    public boolean salvarDespesa(String descricao, String valorTexto, String categoria, 
                                 String dataTexto, String fornecedor, String status, 
                                 String formaPagamento, String observacoes, Projeto projeto) {
        
        // Guard clause: Despesa órfã quebra integridade referencial
        if (projeto == null) {
            JOptionPane.showMessageDialog(null, "Erro Crítico: Nenhum projeto vinculado a esta despesa.");
            return false;
        }
        
        if (descricao.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "A descrição é obrigatória.");
            return false;
        }
        
        // TODO: Avaliar tornar fornecedor obrigatório para melhorar os relatórios
        if (valorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Informe o valor.");
            return false;
        }

        try {
            Despesa despesa = new Despesa();
            
            despesa.setDescricao(descricao);
            despesa.setCategoria(categoria);
            despesa.setFornecedor(fornecedor);
            despesa.setStatus(status);
            despesa.setFormaPagamento(formaPagamento);
            despesa.setObservacoes(observacoes);
            despesa.setProjeto(projeto);

            // Sanitização manual de moeda (BRL -> Double)
            // Remove R$ e pontos de milhar, troca vírgula por ponto.
            // Cuidado: "1.200.50" (US) quebraria aqui ao remover os pontos.
            String valorLimpo = valorTexto.replace("R$", "").replace(".", "").replace(",", ".").trim();
            despesa.setValor(Double.parseDouble(valorLimpo));

            // Parsing manual com fallback para "Hoje" se vier vazio ou com a máscara padrão
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (dataTexto != null && !dataTexto.equals("__/__/____") && !dataTexto.trim().isEmpty()) {
                despesa.setDataDespesa(LocalDate.parse(dataTexto, dtf));
            } else {
                despesa.setDataDespesa(LocalDate.now()); 
            }

            dao.salvar(despesa);
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Valor inválido. Digite apenas números (ex: 150,00).");
            return false;
        } catch (Exception e) {
            // Em prod, trocar printStackTrace por logger
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