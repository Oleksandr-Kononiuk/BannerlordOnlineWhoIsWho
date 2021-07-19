package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.function.Consumer;
import java.util.function.Function;

public class JpaUtil {
    private static EntityManagerFactory emf;

    //BannerlordOnlinePlayersMySQL
    public static void init(String persistenceUnitName) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        emf.close();
    }

    public static void performWithinPersistenceContext(Consumer<EntityManager> operation) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            operation.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Error performing JPA operation. Transaction is rolled back");
            throw e;
            //e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public static <T> T performReturningWithinPersistenceContext(Function<EntityManager, T> entityManagerFunction) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            T result = entityManagerFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.out.println("Error performing JPA operation. Transaction is rolled back");
            //e.printStackTrace();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
