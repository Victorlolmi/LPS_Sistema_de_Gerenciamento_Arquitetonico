/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import javax.persistence.EntityManager;
/**
 *
 * @author Viktin
 */
public class GenericDAO<T> {
    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void salvar(T entity) {
        // Obtém uma instância do EntityManager
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            // Inicia a transação
            em.getTransaction().begin();
            
            // Persiste a entidade. Se a entidade já existir (tiver um ID), 
            // um merge seria mais apropriado, mas para um "salvar" inicial, persist é o correto.
            em.persist(entity);
            
            // Confirma a transação
            em.getTransaction().commit();
        } catch (Exception e) {
            // Se ocorrer um erro, desfaz a transação
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Propaga a exceção para que a camada superior possa tratá-la
            throw new RuntimeException("Erro ao salvar a entidade: " + e.getMessage(), e);
        } finally {
            // Fecha o EntityManager para liberar os recursos
            if (em.isOpen()) {
                em.close();
            }
        }
    }
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
}
