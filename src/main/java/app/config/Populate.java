package app.config;

import app.entities.Url;
import app.security.entities.Role;
import app.security.entities.User;
import app.service.Conversion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Populate {

    public static void main(String[] args) {
//        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
//        populate(emf);
    }

    public static List<Url> populate(EntityManagerFactory emf) {
        User tobias = new User("Tobias", "password");
        User emil = new User("Emil", "password");
        User peter = new User("Peter", "password");
        User oliver = new User("Oliver", "password");

        Role userRole = new Role("user");

        tobias.addRole(userRole);
        emil.addRole(userRole);
        peter.addRole(userRole);
        oliver.addRole(userRole);

        Url url1 = new Url("http://youtube.com/", Conversion.shortCode(), tobias);
        Url url2 = new Url("http://dr.dk/", Conversion.shortCode(), tobias);
        Url url3 = new Url("http://bt.dk/", Conversion.shortCode(), emil);
        Url url4 = new Url("http://twitch.tv/", Conversion.shortCode(), peter);
        Url url5 = new Url("http://github.com/", Conversion.shortCode(), oliver);

        List<Url> urls = new ArrayList<>();
        urls.add(url1);
        urls.add(url2);
        urls.add(url3);
        urls.add(url4);
        urls.add(url5);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(userRole);

            em.persist(tobias);
            em.persist(emil);
            em.persist(peter);
            em.persist(oliver);

            em.persist(url1);
            em.persist(url2);
            em.persist(url3);
            em.persist(url4);
            em.persist(url5);

            em.getTransaction().commit();
            return urls;
        }
    }
}
