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
import model.entities.Projeto;

/**
 *
 * @author Viktin
 */
public class HistoricoTableModel extends AbstractTableModel {
    private List<Projeto> dados = new ArrayList<>();
    private final String[] colunas = {"Projeto", "Status", "Início", "Orçamento", "Detalhes"};
    
    // Formatadores
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        Projeto projeto = dados.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return projeto.getNome();
            case 1:
                return projeto.getStatus();
            case 2:
                return (projeto.getDataInicio() != null) ? dtf.format(projeto.getDataInicio()) : "-";
            case 3:
                return (projeto.getOrcamento() != null) ? nf.format(projeto.getOrcamento()) : "R$ 0,00";
            case 4:
                return "Visualizar"; // Texto para o botão
            default:
                return null;
        }
    }
    
    // Método para retornar o objeto Projeto da linha selecionada
    public Projeto getProjeto(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < dados.size()) {
            return dados.get(rowIndex);
        }
        return null;
    }

    public void setDados(List<Projeto> dados) {
        this.dados = dados;
        fireTableDataChanged();
    }
    
    public void limpar() {
        this.dados.clear();
        fireTableDataChanged();
    }
}
