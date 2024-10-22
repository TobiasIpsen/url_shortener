package app.daos;

import app.dtos.UrlDTO;
import app.entities.Url;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UrlDAO implements IDao {

    private static UrlDAO instance;
    private static EntityManagerFactory emf;

    public static UrlDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UrlDAO();
        }
        return instance;
    }

    @Override
    public List<UrlDTO> getALl() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UrlDTO> query = em.createQuery("SELECT c FROM Url c", UrlDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public UrlDTO getLongUrl(String shortUrl) {
        Url url;
        try (EntityManager em = emf.createEntityManager()) {
            url = em.find(Url.class, shortUrl);
            if (url == null) {
                throw new EntityNotFoundException();
            }
        }
        return new UrlDTO(url);
    }

    @Override
    public UrlDTO create(UrlDTO urlDTO) {
        Url url;
        try (EntityManager em = emf.createEntityManager()) {
            url = new Url(urlDTO);
            em.getTransaction().begin();
            em.persist(url);
            em.getTransaction().commit();
        }
        return new UrlDTO(url);
    }

    @Override
    public void delete(String shortUrl) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Url url = em.find(Url.class, shortUrl);
            em.remove(url);
            em.getTransaction().commit();
        }
    }

    @Override
    public UrlDTO update() {

    }
}
