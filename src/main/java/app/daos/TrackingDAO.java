package app.daos;

import app.dtos.IpDTO;
import app.dtos.UrlDTO;
import app.entities.UrlTracking;
import app.service.IPAPI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Synchronized;

public class TrackingDAO {

    private static TrackingDAO instance;
    private static EntityManagerFactory emf;

    @Synchronized
    public static TrackingDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TrackingDAO();
        }
        return instance;
    }

    @Synchronized
    public void count(UrlDTO url, String clientIp) {
        IpDTO ip = IPAPI.getIP(clientIp);

        try (EntityManager em = emf.createEntityManager()) {
            UrlTracking urlTracking;
            UrlTracking found = em.find(UrlTracking.class, url.getShortUrl());
            em.getTransaction().begin();
            if (found == null) {
                urlTracking = new UrlTracking(url.getShortUrl(), ip.getRegionName(), 1);
                em.persist(urlTracking);
            } else {
                found.setClicks(found.getClicks() + 1);
                em.merge(found);
            }
            em.getTransaction().commit();
        }
    }
}
