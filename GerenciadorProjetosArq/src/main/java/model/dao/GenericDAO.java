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
            
            // --- AQUI ESTÁ A MÁGICA ---
            // Trocamos 'persist' por 'merge'.
            // O 'merge' verifica: 
            // 1. O objeto tem ID? Não -> Faz INSERT (Cria novo)
            // 2. O objeto tem ID? Sim -> Faz UPDATE (Atualiza o existente)
            // Isso resolve seu problema de duplicação e erro de edição.
            em.merge(entity);
            
            // Confirma a transação
            em.getTransaction().commit();
        } catch (Exception e) {
            // Se ocorrer um erro, desfaz a transação
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Propaga a exceção para que a camada superior possa tratá-la
            throw new RuntimeException("Erro ao salvar/atualizar a entidade: " + e.getMessage(), e);
        } finally {
            // Fecha o EntityManager para liberar os recursos
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    // Você pode manter esse método se quiser ser específico em algum lugar,
    // mas o 'salvar' ali de cima agora já faz o trabalho desse aqui também.
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
    
    // Método para deletar (caso precise no futuro)
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

