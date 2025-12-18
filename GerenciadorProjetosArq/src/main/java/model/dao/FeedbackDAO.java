/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import model.entities.Feedback;

/**
 * @author Viktin
 */
public class FeedbackDAO extends GenericDAO<Feedback> {

    public FeedbackDAO() {
        super(Feedback.class);
    }

    public List<Feedback> listarPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Ordenação DESC para priorizar feedbacks recentes na UI
            String jpql = "SELECT f FROM Feedback f WHERE f.projeto.id = :idProjeto ORDER BY f.dataRegistro DESC";
            
            return em.createQuery(jpql, Feedback.class)
                     .setParameter("idProjeto", idProjeto)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}