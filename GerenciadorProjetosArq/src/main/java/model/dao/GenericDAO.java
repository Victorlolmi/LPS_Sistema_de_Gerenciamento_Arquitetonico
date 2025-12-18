/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import javax.persistence.EntityManager;

/**
 * @author Viktin
 */
public class GenericDAO<T> {
    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void salvar(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // Strategy: "Upsert" via Merge. 
            // Se ID null -> Insert. Se ID existe -> Update.
            // Simplifica a lógica no Controller evitando check manual de ID.
            em.merge(entity);
            
            em.getTransaction().commit();

        } catch (Exception e) {
            // Rollback mandatório para garantir atomicidade em caso de falha
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Wrap em RuntimeException para não sujar assinatura do método
            throw new RuntimeException("Erro ao salvar/atualizar a entidade: " + e.getMessage(), e);

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    // Wrapper semântico.
    // Como o 'salvar' usa merge, este método é tecnicamente redundante, 
    // mas mantido para clareza de leitura (dao.update vs dao.salvar).
    public void update(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity); 
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    public void remover(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            T entidade = em.find(entityClass, id);
            if (entidade != null) {
                em.remove(entidade);
            }
            
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}