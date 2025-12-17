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
 * @author Viktin
 */
public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario findByEmailOrCpf(String ident) {
        // Sanitiza input caso seja um CPF formatado (123.456...), mas mantém original para validação de email
        String identLimpo = ident.replaceAll("[.\\-]", "");
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Smart Login: Tenta match no email (literal) ou CPF (apenas números)
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :identificador OR u.cpf = :identificadorLimpo";
            
            return em.createQuery(jpql, Usuario.class)
                    .setParameter("identificador", ident) 
                    .setParameter("identificadorLimpo", identLimpo) 
                    .getSingleResult();
                    
        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
}