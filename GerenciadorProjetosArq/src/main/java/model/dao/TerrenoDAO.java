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
 *
 * @author Viktin
 */
public class TerrenoDAO extends GenericDAO<Terreno> {

    // O construtor passa a classe Terreno para o GenericDAO saber o que está manipulando
    public TerrenoDAO() {
        super(Terreno.class);
    }

    // --- MÉTODOS ESPECÍFICOS ALÉM DO CRUD PADRÃO ---

    /**
     * Busca terrenos pelo nome (parecido com o operador LIKE do SQL)
     * Útil para barra de pesquisa na tela.
     */
    public List<Terreno> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Seleciona t da tabela Terreno onde t.nome contem o texto passado
            String jpql = "SELECT t FROM Terreno t WHERE lower(t.nome) LIKE lower(:nome)";
            
            TypedQuery<Terreno> query = em.createQuery(jpql, Terreno.class);
            query.setParameter("nome", "%" + nome + "%"); // O % faz a busca parcial
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca terrenos por cidade (acessando o objeto Endereco dentro dele)
     */
    public List<Terreno> buscarPorCidade(String cidade) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Note como navegamos pelo objeto: t.endereco.cidade
            String jpql = "SELECT t FROM Terreno t WHERE lower(t.endereco.cidade) LIKE lower(:cidade)";
            
            TypedQuery<Terreno> query = em.createQuery(jpql, Terreno.class);
            query.setParameter("cidade", "%" + cidade + "%");
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}