package app.daos;

import app.dtos.UrlDTO;
import app.dtos.UserDTO;
import app.entities.Url;
import app.entities.UrlTracking;
import app.security.entities.User;
import app.utils.Conversion;
import jakarta.persistence.*;

import java.util.ArrayList;
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
    public List<UrlDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<UrlDTO> query = em.createQuery("SELECT c FROM Url c", UrlDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public UrlDTO getLongUrl(String shortUrl) {
        Url url;
        try (EntityManager em = emf.createEntityManager()) {
//            TypedQuery<Url> query = em.createQuery("SELECT u FROM Url u WHERE u.shortUrl = :url", Url.class);
//            query.setParameter("url", shortUrl);
//            url = query.getSingleResult();
            url = em.find(Url.class, shortUrl);
            if (url == null) {
                throw new EntityNotFoundException(shortUrl);
            }

        } catch (EntityNotFoundException e) {
            System.err.println("ShortUrl Entity not found: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            System.err.println("An error occurred during the transaction: " + e.getMessage());
            throw new IllegalStateException(e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred in getLongUrl method", e);
        }
        return new UrlDTO(url);
    }

    @Override
    public UrlDTO create(UrlDTO urlDTO, UserDTO userDTO) {
        Url url;
        try (EntityManager em = emf.createEntityManager()) {

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class);
            query.setParameter("name", userDTO.getUsername());
            User user = query.getSingleResult();

            url = new Url(urlDTO);
            url.addUser(user);
            url.setShortUrl(Conversion.shortCode());

            user.addUrl(url);

            em.getTransaction().begin();
            em.persist(url);
            em.merge(user);
            em.getTransaction().commit();


        } /*catch (NoResultException e) { //Not needed, because the user will never get here, because they are not logged in.
            throw new EntityNotFoundException(e.getMessage());
        }*/ catch (IllegalStateException e) {
            System.err.println("An error occurred during the transaction: " + e.getMessage());
            throw new IllegalStateException(e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred in create method", e);
        }
        return new UrlDTO(url);
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
            System.err.println("ShortUrl Entity not found: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            System.err.println("An error occurred during the transaction: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred in delete method", e);
        }
    }

    @Override
    public UrlDTO updateLong(String shortUrl, UrlDTO urlDTO) {
        Url urlFound;
        try (EntityManager em = emf.createEntityManager()) {

            urlFound = em.find(Url.class, shortUrl);

            if (urlFound == null) {
                throw new EntityNotFoundException(shortUrl);
            }

            urlFound.setLongUrl(urlDTO.getLongUrl());

            em.getTransaction().begin();
            em.merge(urlFound);
            em.getTransaction().commit();

        } catch (EntityNotFoundException e) {
            System.err.println("Entity not found: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            System.err.println("An error occurred during the transaction: " + e.getMessage());
            throw new IllegalStateException(e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Unexpected error occurred in updateLong method", e);
        }
        return new UrlDTO(urlFound);
    }



}
