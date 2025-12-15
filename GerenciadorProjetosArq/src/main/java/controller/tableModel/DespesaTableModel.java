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
    private final String[] colunas = {"Data", "Descrição", "Fornecedor", "Categoria", "Status", "Valor", "Observação"};

    // Formatadores (Instanciados aqui para não criar um novo a cada célula)
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

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
                return (d.getDataDespesa() != null) ? d.getDataDespesa().format(dtf) : "-";
                
            case 1: // Descrição
                return d.getDescricao();
                
            case 2: // Fornecedor
                return (d.getFornecedor() != null && !d.getFornecedor().isEmpty()) ? d.getFornecedor() : "-";
                
            case 3: // Categoria
                return d.getCategoria();
                
            case 4: // Status
                return d.getStatus(); // "Pago" ou "Pendente"
                
            case 5: // Valor
                return (d.getValor() != null) ? nf.format(d.getValor()) : "R$ 0,00";
                
            case 6: // Observação (NOVA COLUNA NO FINAL)
                return (d.getObservacoes() != null) ? d.getObservacoes() : "";
                
            default:
                return null;
        }
    }

    public void setDados(List<Despesa> lista) {
        this.dados = lista;
        fireTableDataChanged(); 
    }
    
    public Despesa getDespesa(int linha) {
        return dados.get(linha);
    }
    
    public void limpar() {
        this.dados.clear();
        fireTableDataChanged();
    }
}
