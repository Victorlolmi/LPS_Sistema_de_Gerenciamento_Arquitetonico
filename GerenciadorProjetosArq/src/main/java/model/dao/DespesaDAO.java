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
 *
 * @author Viktin
 */
public class DespesaDAO extends GenericDAO<Despesa> {

    public DespesaDAO() {
        // Informa ao GenericDAO que esta classe cuida de 'Despesa'
        super(Despesa.class);
    }

    // OBS: Os métodos salvar, atualizar, remover e buscarPorId já são herdados do GenericDAO.

    /**
     * Lista apenas as despesas de um projeto específico.
     * Ordenado pela data da despesa (mais recentes primeiro).
     */
    public List<Despesa> listarPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT d FROM Despesa d WHERE d.projeto.id = :idProjeto ORDER BY d.dataDespesa DESC";
            
            return em.createQuery(jpql, Despesa.class)
                     .setParameter("idProjeto", idProjeto)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Soma o total gasto pelo projeto diretamente no banco.
     * Retorna 0.0 se não houver gastos, evitando NullPointerException.
     */
    public Double somarTotalPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL de Soma (SUM)
            String jpql = "SELECT SUM(d.valor) FROM Despesa d WHERE d.projeto.id = :idProjeto";
            
            TypedQuery<Double> query = em.createQuery(jpql, Double.class);
            query.setParameter("idProjeto", idProjeto);
            
            Double total = query.getSingleResult();
            
            return (total != null) ? total : 0.0;
            
        } finally {
            em.close();
        }
    }
}
