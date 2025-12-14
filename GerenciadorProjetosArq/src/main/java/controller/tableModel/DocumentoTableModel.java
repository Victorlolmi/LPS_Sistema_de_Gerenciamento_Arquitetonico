/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Documento;
/**
 *
 * @author Viktin
 */
public class DocumentoTableModel extends AbstractTableModel {

    private List<Documento> dados = new ArrayList<>();
    private String[] colunas = {"Nome", "Categoria", "Tipo", "Data Upload"};

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
        Documento d = dados.get(linha);
        switch (coluna) {
            case 0: return d.getNome();
            case 1: return d.getCategoria();
            case 2: return d.getTipo();
            case 3: 
                return (d.getDataUpload() != null) 
                    ? d.getDataUpload().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                    : "-";
            default: return null;
        }
    }

    public void setDados(List<Documento> lista) {
        this.dados = lista;
        fireTableDataChanged();
    }
    
    public Documento getDocumento(int linha) {
        return dados.get(linha);
    }
    
    public void limpar() {
        this.dados.clear();
        fireTableDataChanged();
    }
}