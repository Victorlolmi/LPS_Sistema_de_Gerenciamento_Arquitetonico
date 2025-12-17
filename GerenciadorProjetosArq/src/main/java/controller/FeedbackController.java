/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.dao.FeedbackDAO;
import model.entities.Feedback;
import model.entities.Projeto;

/**
 * @author Viktin
 */
public class FeedbackController {

    private final FeedbackDAO dao;

    public FeedbackController() {
        this.dao = new FeedbackDAO();
    }

    public boolean salvarFeedback(String tipo, String comentario, Projeto projeto, String autor) {
        
        // Bloqueia registro órfão (integridade referencial)
        if (projeto == null) {
            JOptionPane.showMessageDialog(null, "Erro: Nenhum projeto vinculado.");
            return false;
        }
        
        if (comentario == null || comentario.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escreva um comentário.");
            return false;
        }

        try {
            // TODO: Validar se 'autor' precisa ser normalizado (trim/uppercase) ou buscado do AuthContext
            Feedback f = new Feedback(tipo, comentario, projeto, autor);
            dao.salvar(f);
            return true;
            
        } catch (Exception e) {
            // Em prod, substituir JOptionPane por Logger (SLF4J)
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            return false;
        }
    }

    public List<Feedback> listar(Long idProjeto) {
        return dao.listarPorProjeto(idProjeto);
    }
    
    public boolean excluir(Long id) {
        try {
            dao.remover(id);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + e.getMessage());
            return false;
        }
    }
}