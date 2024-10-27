package app.daos;

import app.config.HibernateConfig;
import app.config.Populate;
import app.dtos.UrlDTO;
import app.dtos.UserDTO;
import app.entities.Url;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlDAOTest {

    private static EntityManagerFactory emf;
    private static UrlDAO dao;
    private static List<Url> urlList = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryConfig(true);
        dao = UrlDAO.getInstance(emf);
    }

    @BeforeEach
    void beforeEach() {
        urlList = new ArrayList<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE Role").executeUpdate();
            em.createQuery("DELETE UrlTracking").executeUpdate();
            em.createQuery("DELETE Url").executeUpdate();
            em.createQuery("DELETE User").executeUpdate();
            em.getTransaction().commit();
        }
        urlList = Populate.populate(emf);
    }

    @Test
    @DisplayName("Get All Urls")
    void getAll() {
        int urlListSize = dao.getAll().size();
        int expected = 5;
        assertEquals(expected,urlListSize);
    }

    @Test
    @DisplayName("Get Long Url from Short Url")
    void getLongUrl() {
        String daoLongUrl = dao.getLongUrl(urlList.get(0).getShortUrl()).getLongUrl();
        String longUrl = urlList.get(0).getLongUrl();
        assertEquals(daoLongUrl,longUrl);
    }

    @Test
    @DisplayName("Create new Url")
    void create() {
        UserDTO tobias = new UserDTO("Tobias", "password");

        UrlDTO urlDTO = new UrlDTO("longUrl");
        UrlDTO createdUrl = dao.create(urlDTO, tobias);

        assertEquals(urlDTO.getLongUrl(),createdUrl.getLongUrl());
    }

    @Test
    void delete() {
        dao.delete(urlList.get(0).getShortUrl());
        int size;

        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UrlDTO> query = em.createQuery("SELECT c FROM Url c", UrlDTO.class);
            size = query.getResultList().size();
        }

        assertEquals(4,size);
    }

    @Test
    void update() {
        UrlDTO urlDTO = new UrlDTO("longUrl");
        UrlDTO updatedUrl = dao.updateLong(urlList.get(0).getShortUrl(),urlDTO);

        assertEquals(urlDTO.getLongUrl(),updatedUrl.getLongUrl());
    }
}