/*
 * License Header omitted.
 */
package model.dao;
import factory.JPAUtil;
import java.util.ArrayList;
import model.entities.Cliente;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * @author Viktin
 */
public class ClienteDAO extends GenericDAO<Cliente> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public List<Cliente> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Performance: LEFT JOIN FETCH carrega o endereço na mesma query, evitando problema de N+1.
            // Ordenação por nome para facilitar a renderização em ComboBoxes/Tabelas.
            String jpql = "SELECT c FROM Cliente c LEFT JOIN FETCH c.endereco ORDER BY c.nome";
            return em.createQuery(jpql, Cliente.class).getResultList();
            
        } finally {
            em.close();
        }
    }

    public List<Cliente> buscarPorNome(String nomeBusca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // A MÁGICA ESTÁ AQUI: "JOIN FETCH c.endereco"
            // Isso diz: "Traga o cliente E JÁ TRAGA o endereço dele junto agora."
            String jpql = "SELECT c FROM Cliente c JOIN FETCH c.endereco WHERE lower(c.nome) LIKE lower(:nome)";
            
            return em.createQuery(jpql, Cliente.class)
                     .setParameter("nome", "%" + nomeBusca + "%")
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    public Cliente buscarPorDocumento(String documento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Varredura em ambas colunas para garantir unicidade (Pessoa Física ou Jurídica)
            String jpql = "SELECT c FROM Cliente c WHERE c.cpf = :doc OR c.cnpj = :doc";
            
            return em.createQuery(jpql, Cliente.class)
                    .setParameter("doc", documento)
                    .getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public Cliente buscarPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c WHERE c.email = :email";
            
            return em.createQuery(jpql, Cliente.class)
                    .setParameter("email", email)
                    .getSingleResult();
                    
        } catch (NoResultException e) {
            // Retorna null silenciosamente para validação de cadastro (check de duplicidade)
            return null; 
        } finally {
            em.close();
        }
    }

    public Cliente buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c WHERE c.cpf = :cpf";

            return em.createQuery(jpql, Cliente.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
    
    public List<Cliente> buscarPorGestor(Long gestorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
 
            String jpql = "SELECT DISTINCT c FROM Projeto p " +
                          "JOIN p.cliente c " +
                          "LEFT JOIN FETCH c.endereco " + 
                          "WHERE p.gestor.id = :gestorId " +
                          "ORDER BY c.nome";
            
            TypedQuery<Cliente> query = em.createQuery(jpql, Cliente.class);
            query.setParameter("gestorId", gestorId);
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
}