package controller.tableModel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Gestor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author juans
 */
public class GestorTableModel extends AbstractTableModel {

   private List<Gestor> dados = new ArrayList<>();
    private final String[] colunas = {"Nome", "CPF", "E-mail", "Visualizar"};

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
        Gestor g = dados.get(linha);
        
        switch (coluna) {
            case 0: return g.getNome();
            case 1: return g.getCpf();
            case 2: return g.getEmail();
            case 3: return "Abrir"; // Botão
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setDados(List<Gestor> lista) {
        this.dados = lista;
        fireTableDataChanged();
    }
    
    public Gestor getGestor(int linha) {
        return dados.get(linha);
    }
}