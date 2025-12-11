/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.tableModel;

import java.awt.List;
import model.entities.Projeto;

/**
 *
 * @author juans
 */
public class TMProjeto {
    private List<Projeto> lista;
    
    // Constantes para as colunas baseadas na imagem
    private final int COL_PROJETO = 0;   
    private final int COL_CLIENTE = 1;    
    private final int COL_STATUS = 2;
    // A coluna de botão "Detalhes" é visual, mas se quiser mapear:
    // private final int COL_DETALHES = 3; 

    public TMProjeto(List<Projeto> lstProjetos) {        
        lista = lstProjetos;        
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        // Quantidade de colunas (Projeto, Cliente, Status)
        return 3; 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {                
        if (lista.isEmpty()) {
            return null;
        } 
        
        Projeto aux = lista.get(rowIndex);

        switch (columnIndex) {
            case COL_PROJETO:
                return aux.getNome();
            case COL_CLIENTE:
                // Aqui pegamos o nome de dentro do objeto Cliente
                if(aux.getCliente() != null) {
                    return aux.getCliente().getNome(); 
                }
                return "";
            case COL_STATUS:
                return aux.getStatus();
            default: 
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Se você implementar o botão na tabela futuramente, 
        // a coluna do botão precisará retornar true aqui.
        return false;
    }

    @Override
    public String getColumnName(int column) {
        
        switch (column) {
            case COL_PROJETO:
                return "Projeto";
            case COL_CLIENTE:
                return "Cliente";
            case COL_STATUS:
                return "Status"; 
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    // Método utilitário para pegar o objeto inteiro ao clicar na linha
    public Projeto getObjeto(int rowIndex) {
        return lista.get(rowIndex);
    }
}
