/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;
import factory.JPAUtil;
import model.entities.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
/**
 *
 * @author Viktin
 */
public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        // Passa a classe da entidade para o construtor do GenericDAO
        super(Usuario.class);
    }
    
    // O método 'salvar(Usuario usuario)' já foi herdado! Não é preciso reescrevê-lo.

    public Usuario findByEmailOrCpf(String ident) {
        String identLimpo = ident.replaceAll("[.\\-]", "");
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL para buscar em duas colunas diferentes
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :identificador OR u.cpf = :identificadorLimpo";
            
            return em.createQuery(jpql, Usuario.class)
                    .setParameter("identificador", ident) // Busca o email com o valor original
                    .setParameter("identificadorLimpo", identLimpo) // Busca o CPF com o valor limpo
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Nenhum usuário encontrado com esse email ou CPF
        } finally {
            em.close();
        }
    }
    
}
