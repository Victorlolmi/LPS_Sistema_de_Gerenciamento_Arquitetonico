/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import model.entities.Despesa;

/**
 * @author Viktin
 */
public class DespesaDAO extends GenericDAO<Despesa> {

    public DespesaDAO() {
        super(Despesa.class);
    }

    public List<Despesa> listarPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Ordenação DESC para priorizar lançamentos recentes na UI
            String jpql = "SELECT d FROM Despesa d WHERE d.projeto.id = :idProjeto ORDER BY d.dataDespesa DESC";
            
            return em.createQuery(jpql, Despesa.class)
                     .setParameter("idProjeto", idProjeto)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Double somarTotalPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Aggregation direto no banco (mais performático que trazer a lista e somar no Java)
            String jpql = "SELECT SUM(d.valor) FROM Despesa d WHERE d.projeto.id = :idProjeto";
            
            TypedQuery<Double> query = em.createQuery(jpql, Double.class);
            query.setParameter("idProjeto", idProjeto);
            
            Double total = query.getSingleResult();
            
            // Null safety: JPQL retorna NULL se não houver registros matchando o WHERE
            return (total != null) ? total : 0.0;
            
        } finally {
            em.close();
        }
    }
}