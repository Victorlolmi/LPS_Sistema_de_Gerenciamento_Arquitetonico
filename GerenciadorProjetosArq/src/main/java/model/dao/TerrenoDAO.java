/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import model.entities.Terreno;

/**
 * @author Viktin
 */
public class TerrenoDAO extends GenericDAO<Terreno> {

    public TerrenoDAO() {
        super(Terreno.class);
    }

    public List<Terreno> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Busca parcial (LIKE) e case-insensitive
            String jpql = "SELECT t FROM Terreno t WHERE lower(t.nome) LIKE lower(:nome)";
            
            TypedQuery<Terreno> query = em.createQuery(jpql, Terreno.class);
            query.setParameter("nome", "%" + nome + "%");
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Terreno> buscarPorCidade(String cidade) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Query via navegação de objeto (Terreno -> Endereco -> Cidade)
            String jpql = "SELECT t FROM Terreno t WHERE lower(t.endereco.cidade) LIKE lower(:cidade)";
            
            TypedQuery<Terreno> query = em.createQuery(jpql, Terreno.class);
            query.setParameter("cidade", "%" + cidade + "%");
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}