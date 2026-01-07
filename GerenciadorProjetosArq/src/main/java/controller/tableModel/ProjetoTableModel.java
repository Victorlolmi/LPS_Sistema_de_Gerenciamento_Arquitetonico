/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.entities.Cliente;
import model.entities.Projeto;
import model.entities.Usuario;
/**
 *
 * @author Viktin
 */
public class ProjetoTableModel extends AbstractTableModel {

    private List<Projeto> dados = new ArrayList<>();
    private final boolean isClienteLogado; // Flag para saber se é visão de cliente

    // Construtor padrão (assume que NÃO é cliente se não passar nada, ou pode adaptar)
    public ProjetoTableModel() {
        this.isClienteLogado = false;
    }

    // NOVO CONSTRUTOR: Recebe o usuário logado para decidir as colunas
    public ProjetoTableModel(Usuario usuarioLogado) {
        this.isClienteLogado = (usuarioLogado instanceof Cliente);
    }

    @Override
    public String getColumnName(int column) {
        // Se for cliente logado, a segunda coluna (índice 1) vira "Gestor"
        if (column == 1) {
            return isClienteLogado ? "Gestor" : "Cliente";
        }
        
        // As outras colunas permanecem iguais
        String[] colunasPadrao = {"Projeto", "Cliente", "Status", "Visualizar"};
        return colunasPadrao[column];
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return 4; // "Projeto", "Cliente/Gestor", "Status", "Visualizar"
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Projeto p = dados.get(linha);
        
        switch (coluna) {
            case 0: // Projeto
                return p.getNome();
            case 1: // Cliente OU Gestor
                if (isClienteLogado) {
                    // Se sou cliente, mostro o nome do Gestor
                    return (p.getGestor() != null) ? p.getGestor().getNome() : "---";
                } else {
                    // Se sou Gestor (ou outro), mostro o nome do Cliente
                    return (p.getCliente() != null) ? p.getCliente().getNome() : "---";
                }
            case 2: // Status
                return (p.getStatus() != null) ? p.getStatus() : "-";
            case 3: // Ações
                return "Abrir";
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