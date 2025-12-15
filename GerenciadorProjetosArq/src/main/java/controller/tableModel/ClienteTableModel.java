/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Cliente;
/**
 *
 * @author Viktin
 */
public class ClienteTableModel extends AbstractTableModel {

    private List<Cliente> dados = new ArrayList<>();
    // Colunas: Nome, CPF, Email, Cidade, Botão
    private final String[] colunas = {"Nome", "CPF", "E-mail", "Cidade", "Visualizar"};

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
        Cliente c = dados.get(linha);
        
        switch (coluna) {
            case 0: return c.getNome();
            case 1: return c.getCpf();
            case 2: return c.getEmail();
            case 3: return (c.getEndereco() != null) ? c.getEndereco().getCidade() : "-";
            case 4: return "Abrir"; // Botão
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setDados(List<Cliente> lista) {
        this.dados = lista;
        fireTableDataChanged();
    }
    
    public Cliente getCliente(int linha) {
        return dados.get(linha);
    }
}