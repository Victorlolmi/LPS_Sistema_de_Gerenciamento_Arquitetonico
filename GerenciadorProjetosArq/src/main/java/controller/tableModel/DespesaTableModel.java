/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import model.entities.Despesa;
/**
 *
 * @author Viktin
 */
public class DespesaTableModel extends AbstractTableModel {

    private List<Despesa> dados = new ArrayList<>();
    
    // As colunas que vão aparecer na tabela
    private final String[] colunas = {"Data", "Descrição", "Fornecedor", "Categoria", "Valor", "Status"};

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Despesa d = dados.get(linha);
        
        switch (coluna) {
            case 0: // Data
                if (d.getDataDespesa() != null) {
                    return d.getDataDespesa().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                return "-";
                
            case 1: // Descrição
                return d.getDescricao();
                
            case 2: // Fornecedor
                return (d.getFornecedor() != null && !d.getFornecedor().isEmpty()) ? d.getFornecedor() : "-";
                
            case 3: // Categoria
                return d.getCategoria();
                
            case 4: // Valor (Formatado em Reais)
                if (d.getValor() != null) {
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    return nf.format(d.getValor());
                }
                return "R$ 0,00";
                
            case 5: // Status
                return d.getStatus(); // "Pago" ou "Pendente"
                
            default:
                return null;
        }
    }

    // Método para atualizar a lista inteira de uma vez
    public void setDados(List<Despesa> lista) {
        this.dados = lista;
        fireTableDataChanged(); // Avisa a JTable para se redesenhar
    }
    
    // Recupera o objeto da linha clicada (útil para excluir)
    public Despesa getDespesa(int linha) {
        return dados.get(linha);
    }
    
    public void limpar() {
        this.dados.clear();
        fireTableDataChanged();
    }
}
