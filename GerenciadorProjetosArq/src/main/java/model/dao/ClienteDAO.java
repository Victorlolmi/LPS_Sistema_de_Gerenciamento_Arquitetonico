/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import model.entities.Cliente;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
/**
 *
 * @author Viktin
 */
public class ClienteDAO extends GenericDAO<Cliente> {

    public ClienteDAO() {
        // Passa a classe da entidade para o construtor do GenericDAO
        super(Cliente.class);
    }

    // O método 'salvar', 'atualizar', 'remover' e 'findById' já foram herdados.

    /**
     * Retorna todos os clientes ordenados pelo nome.
     * Ideal para preencher JComboBox.
     */
    public List<Cliente> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // O "ORDER BY c.nome" é importante para a lista aparecer em ordem alfabética
            String jpql = "SELECT c FROM Cliente c LEFT JOIN FETCH c.endereco ORDER BY c.nome";
            return em.createQuery(jpql, Cliente.class).getResultList();
            
        } finally {
            em.close();
        }
    }

    /**
     * Busca clientes por parte do nome (case insensitive).
     * Ex: buscar "jo" traz "João", "José", "Marujo".
     */
    public List<Cliente> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Cliente c WHERE lower(c.nome) LIKE lower(:nome) ORDER BY c.nome";
            
            return em.createQuery(jpql, Cliente.class)
                    .setParameter("nome", "%" + nome + "%") // O % permite buscar partes do texto
                    .getResultList();
            
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca exata por CPF ou CNPJ (opcional, caso precise validar duplicidade)
     */
    public Cliente buscarPorDocumento(String documento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
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
        // Busca um cliente onde o email seja igual ao informado
        String jpql = "SELECT c FROM Cliente c WHERE c.email = :email";
        
        return em.createQuery(jpql, Cliente.class)
                .setParameter("email", email)
                .getSingleResult();
                
    } catch (NoResultException e) {
        return null; // Não achou ninguém, retorna null (o que é bom para cadastro!)
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
}
