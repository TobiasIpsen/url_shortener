package app.routes;

import app.dtos.UserDTO;
import app.entities.Url;
import app.security.entities.Role;
import app.security.entities.User;
import app.service.Conversion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

public class Populate {
    private static User user, admin;

    public static UserDTO[] populateUsers(EntityManagerFactory emf) {
        Role userRole, adminRole;

        user = new User("usertest", "password");
        admin = new User("admintest", "password");

        userRole =  new Role("USER");
        adminRole =  new Role("ADMIN");

        user.addRole(userRole);
        admin.addRole(userRole);
        admin.addRole(adminRole);

        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

        UserDTO userDTO = new UserDTO(user.getUsername(), "password");
        UserDTO adminDTO = new UserDTO(admin.getUsername(), "password");
        return new UserDTO[]{userDTO, adminDTO};
    }

    public static List<Url> populateUrls(EntityManagerFactory emf) {

        Url url1 = new Url(Conversion.shortCode(), "http://youtube.com/", user);
        Url url2 = new Url(Conversion.shortCode(), "http://dr.dk/", user);
        Url url3 = new Url(Conversion.shortCode(), "http://bt.dk/", admin);

        List<Url> urls = new ArrayList<>();
        urls.add(url1);
        urls.add(url2);
        urls.add(url3);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(url1);
            em.persist(url2);
            em.persist(url3);

            em.getTransaction().commit();
            return urls;
        }
    }
}
