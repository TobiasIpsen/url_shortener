package app.daos;

import app.dtos.UrlDTO;
import app.dtos.UserDTO;
import app.entities.Url;
import app.security.entities.User;
import app.service.Conversion;
import jakarta.persistence.*;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UrlDAO implements IDao {

    private static UrlDAO instance;
    private static EntityManagerFactory emf;
    private static final Logger logger = LoggerFactory.getLogger(UrlDAO.class);

    @Synchronized //Allow only one thread to access it at once
    public static UrlDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UrlDAO();
            logger.info("UrlDAO instance created");
        }
        return instance;
    }

    @Override
    public List<UrlDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            logger.info("Fetching all URLs.");
            TypedQuery<Url> query = em.createQuery("SELECT c FROM Url c", Url.class);
            List<Url> urls = query.getResultList();
            return urls.stream()
                    .map(UrlDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch all URLs", e);
            throw e;
        }
    }

    @Override
    public UrlDTO getLongUrl(String shortUrl) {
        try (EntityManager em = emf.createEntityManager()) {

            logger.info("Fetching long URL from shortUrl: " + shortUrl);
            Url url = em.find(Url.class, shortUrl);
            if (url == null) {
                throw new EntityNotFoundException(shortUrl);
            }
            return new UrlDTO(url);

        } catch (EntityNotFoundException e) {
            logger.error("ShortUrl Entity not found: " + shortUrl, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in getLongUrl", e);
            throw new RuntimeException("Unexpected error occurred in getLongUrl method: ", e);
        }
    }

    @Override
    public UrlDTO create(UrlDTO urlDTO, UserDTO userDTO) {
        try (EntityManager em = emf.createEntityManager()) {

            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class)
                    .setParameter("name", userDTO.getUsername())
                    .getSingleResult();

            Url url = new Url(urlDTO);
            url.addUser(user);
            url.setShortUrl(Conversion.shortCode());

            user.addUrl(url);

            em.getTransaction().begin();
            em.persist(url);
            em.merge(user);
            em.getTransaction().commit();

            return new UrlDTO(url);
        } /*catch (NoResultException e) { //Not needed, because the user will never get here, because they are not logged in.
            throw new EntityNotFoundException(e.getMessage());
        }*/ catch (Exception e) {
            logger.error("Unexpected error occurred during URL creation", e);
            throw new RuntimeException("Unexpected error occurred in create method: ", e);
        }
    }

    @Override
    public void delete(String shortUrl) {
        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();
            Url url = em.find(Url.class, shortUrl);

            if (url == null) {
                throw new EntityNotFoundException(shortUrl);
            }

            em.remove(url);
            em.getTransaction().commit();

        } catch (EntityNotFoundException e) {
            logger.error("ShortUrl Entity not found: " + shortUrl, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            throw new RuntimeException("Unexpected error occurred in delete method", e);
        }
    }

    @Override
    public UrlDTO updateLong(String shortUrl, UrlDTO urlDTO) {
        try (EntityManager em = emf.createEntityManager()) {

            Url urlFound = em.find(Url.class, shortUrl);

            if (urlFound == null) {
                throw new EntityNotFoundException(shortUrl);
            }

            urlFound.setLongUrl(urlDTO.getLongUrl());

            em.getTransaction().begin();
            em.merge(urlFound);
            em.getTransaction().commit();

            return new UrlDTO(urlFound);

        } catch (EntityNotFoundException e) {
            logger.error("ShortUrl Entity not found: " + shortUrl, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            throw new RuntimeException("Unexpected error occurred in updateLong method", e);
        }
    }

}