/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Projeto;
/**
 *
 * @author Viktin
 */
public class ProjetoTableModel extends AbstractTableModel {

    private List<Projeto> dados = new ArrayList<>();
    
    // 1. ADICIONAMOS A COLUNA "AÇÕES" NO FINAL
    private final String[] colunas = {"Projeto", "Cliente", "Status", "Visualizar"};

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
        Projeto p = dados.get(linha);
        
        switch (coluna) {
            case 0: // Projeto
                return p.getNome();
            case 1: // Cliente
                return (p.getCliente() != null) ? p.getCliente().getNome() : "---";
            case 2: // Status
                return (p.getStatus() != null) ? p.getStatus() : "-";
            case 3: // Ações (Nova Coluna)
                return "Abrir"; // O texto que vai aparecer no botão (opcional)
            default:
                return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; 
    }

    public void setDados(List<Projeto> lista) {
        this.dados = lista;
        fireTableDataChanged();
    }
    
    public Projeto getProjeto(int linha) {
        return dados.get(linha);
    }
    
    public void limpar() {
        this.dados.clear();
        fireTableDataChanged();
    }
}
