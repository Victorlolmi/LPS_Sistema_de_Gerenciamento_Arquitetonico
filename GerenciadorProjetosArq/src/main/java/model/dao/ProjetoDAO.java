/*
 * License Header omitted.
 */
package model.dao;
import factory.JPAUtil;
import model.entities.Projeto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * @author Viktin
 */
public class ProjetoDAO extends GenericDAO<Projeto> {

    public ProjetoDAO() {
        super(Projeto.class);
    }

    public List<Projeto> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Performance: LEFT JOIN FETCH carrega o cliente na mesma query (evita problema de N+1).
            String jpql = "SELECT p FROM Projeto p LEFT JOIN FETCH p.cliente ORDER BY p.nome";
            
            return em.createQuery(jpql, Projeto.class).getResultList();
            
        } finally {
            em.close();
        }
    }

    public List<Projeto> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Busca parcial (LIKE) e case-insensitive
            String jpql = "SELECT p FROM Projeto p WHERE lower(p.nome) LIKE lower(:nome) ORDER BY p.nome";
            
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            query.setParameter("nome", "%" + nome + "%");
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    public List<Projeto> buscarDinamica(String termo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Busca global: tenta dar match no nome do Projeto OU no Cliente associado
            String jpql = "SELECT p FROM Projeto p JOIN FETCH p.cliente " +
                          "WHERE lower(p.nome) LIKE lower(:termo) " +
                          "OR lower(p.cliente.nome) LIKE lower(:termo) " +
                          "ORDER BY p.nome";
            
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            query.setParameter("termo", "%" + termo + "%");
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    public Projeto buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Projeto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Projeto> buscarPorStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Ordena por previsão para destacar prazos apertados na UI
            String jpql = "SELECT p FROM Projeto p WHERE p.status = :status ORDER BY p.dataPrevisao";
            
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            query.setParameter("status", status);
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    public List<Projeto> buscarPorClienteId(Long clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Projeto p WHERE p.cliente.id = :id ORDER BY p.nome";
            
            return em.createQuery(jpql, Projeto.class)
                      .setParameter("id", clienteId)
                      .getResultList();
            
        } finally {
            em.close();
        }
    }
}