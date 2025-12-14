/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import factory.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import model.entities.Documento;
/**
 *
 * @author Viktin
 */
public class DocumentoDAO {

    public void salvar(Documento d) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (d.getId() == null) {
                em.persist(d); // Novo
            } else {
                em.merge(d); // Edição
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void remover(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Documento d = em.find(Documento.class, id);
            if (d != null) {
                em.remove(d);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Busca apenas os documentos daquele projeto específico
    public List<Documento> listarPorProjeto(Long idProjeto) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Documento> lista = null;
        try {
            // JPQL: Seleciona Documento onde o ID do Projeto seja igual ao parâmetro
            String jpql = "SELECT d FROM Documento d WHERE d.projeto.id = :idProjeto";
            lista = em.createQuery(jpql, Documento.class)
                      .setParameter("idProjeto", idProjeto)
                      .getResultList();
        } finally {
            em.close();
        }
        return lista;
    }
}
