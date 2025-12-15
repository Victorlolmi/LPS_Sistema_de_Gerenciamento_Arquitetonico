/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Feedback;
/**
 *
 * @author Viktin
 */
public class FeedbackTableModel extends AbstractTableModel {

    private List<Feedback> dados = new ArrayList<>();
    
    // NOVA ORDEM: Autor | Mensagem | Tipo | Data
    private final String[] colunas = {"Autor", "Mensagem", "Tipo", "Data"};
    
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @Override
    public String getColumnName(int column) { return colunas[column]; }
    
    @Override
    public int getRowCount() { return dados.size(); }
    
    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Feedback f = dados.get(linha);
        switch (coluna) {
            case 0: return f.getAutor(); // Autor primeiro
            case 1: return f.getComentario(); // Mensagem (Texto Longo)
            case 2: return f.getTipo();
            case 3: return (f.getDataRegistro() != null) ? f.getDataRegistro().format(dtf) : "-";
            default: return null;
        }
    }
    
    public void setDados(List<Feedback> lista) {
        this.dados = lista;
        fireTableDataChanged();
    }
    
    public Feedback getFeedback(int linha) { return dados.get(linha); }
}
