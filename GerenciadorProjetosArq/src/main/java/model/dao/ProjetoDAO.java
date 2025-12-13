/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import model.entities.Projeto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
/**
 *
 * @author Viktin
 */
public class ProjetoDAO extends GenericDAO<Projeto> {

    public ProjetoDAO() {
        // Informa ao GenericDAO que esta classe cuida de 'Projeto'
        super(Projeto.class);
    }

    // OBS: Os métodos salvar, atualizar, remover e findById já existem no GenericDAO.

    /**
     * Retorna todos os projetos, ordenados pela Data de Início (mais recentes primeiro).
     */
    public List<Projeto> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // ORDER BY p.dataInicio DESC -> Mostra os projetos mais novos no topo da lista
           String jpql = "SELECT p FROM Projeto p LEFT JOIN FETCH p.cliente ORDER BY p.nome";
            
            return em.createQuery(jpql, Projeto.class).getResultList();
            
        } finally {
            em.close();
        }
    }

    /**
     * Busca projetos pelo nome (busca parcial e ignora maiúsculas/minúsculas).
     * Ex: buscar "site" encontra "Novo Site", "Website Institucional", etc.
     */
    public List<Projeto> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Projeto p WHERE lower(p.nome) LIKE lower(:nome) ORDER BY p.nome";
            
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            query.setParameter("nome", "%" + nome + "%");
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    public Projeto buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // O find já resolve tudo
            return em.find(Projeto.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Filtra projetos por Status.
     * Útil para abas como "Concluídos" ou "Em Andamento".
     */
    public List<Projeto> buscarPorStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Projeto p WHERE p.status = :status ORDER BY p.dataPrevisao";
            
            TypedQuery<Projeto> query = em.createQuery(jpql, Projeto.class);
            query.setParameter("status", status);
            
            return query.getResultList();
            
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca todos os projetos de um Cliente específico.
     */
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
