package app.service;

import app.config.HibernateConfig;
import app.entities.Url;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class Conversion {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final Random random = new Random();
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
    private static final Logger logger = LoggerFactory.getLogger(Conversion.class);

    public static String shortCode() {
        String shortUrl;
        boolean isUnique;

        do {
            StringBuilder builder = new StringBuilder(SHORT_CODE_LENGTH);
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }

            shortUrl = builder.toString();
            isUnique = checkShortCodeUnique(shortUrl);
        } while (isUnique == false);

        return shortUrl;
    }

    public static boolean checkShortCodeUnique(String shortUrl) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Url> query = em.createQuery("SELECT u.shortUrl FROM Url u where u.shortUrl = :code", Url.class);
            query.setParameter("code", shortUrl);
            List<Url> list = query.getResultList();
            if (list.isEmpty()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking if shortcode is unique", e);
            throw e;
        }
    }
}
