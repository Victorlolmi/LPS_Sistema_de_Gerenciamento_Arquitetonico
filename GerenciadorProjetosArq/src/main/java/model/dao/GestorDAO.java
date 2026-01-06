/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import factory.JPAUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import model.entities.Gestor;

/**
 *
 * @author juans
 */
public class GestorDAO extends GenericDAO<Gestor>{

    public GestorDAO() {
        super(Gestor.class);
    }

    public List<Gestor> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {

            String jpql = "SELECT g FROM Gestor g LEFT JOIN FETCH g.endereco ORDER BY g.nome";
            return em.createQuery(jpql, Gestor.class).getResultList();
            
        } finally {
            em.close();
        }
    }

    public List<Gestor> buscarPorNome(String nomeBusca) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT g FROM Gestor g JOIN FETCH g.endereco WHERE lower(g.nome) LIKE lower(:nome)";
            
            return em.createQuery(jpql, Gestor.class)
                     .setParameter("nome", "%" + nomeBusca + "%")
                     .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
    public Gestor buscarPorEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT g FROM Gestor g WHERE g.email = :email";
            
            return em.createQuery(jpql, Gestor.class)
                    .setParameter("email", email)
                    .getSingleResult();
                    
        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }

    public Gestor buscarPorCpf(String cpf) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT g FROM Gestor g WHERE g.cpf = :cpf";

            return em.createQuery(jpql, Gestor.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
    public List<Gestor> buscarPorCliente(Long clienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Seleciona Gestores que o ID esteja na lista de gestores dos projetos desse cliente
            String jpql = "SELECT DISTINCT g FROM Gestor g " +
                          "WHERE g.id IN (" +
                          "   SELECT p.gestor.id FROM Projeto p " +
                          "   WHERE p.cliente.id = :clienteId " +
                          "   AND p.gestor IS NOT NULL" +
                          ")";
            
            TypedQuery<Gestor> query = em.createQuery(jpql, Gestor.class);
            query.setParameter("clienteId", clienteId);
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
}
