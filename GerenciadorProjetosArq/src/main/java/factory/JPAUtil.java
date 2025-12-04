/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
/**
 *
 * @author Viktin
 */
public class JPAUtil {
    private static final EntityManagerFactory FACTORY = Persistence
            .createEntityManagerFactory("gestor-projetos-pu");

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
    public static void closeFactory() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }

}
