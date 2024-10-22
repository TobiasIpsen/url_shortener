package app.daos;

import app.dtos.IpDTO;
import app.dtos.UrlDTO;
import app.entities.Url;
import app.entities.UrlTracking;
import app.service.IPAPI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class TrackingDAO {

    private static TrackingDAO instance;
    private static EntityManagerFactory emf;

    public static TrackingDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TrackingDAO();
        }
        return instance;
    }

    public void count(UrlDTO url, String clientIp) {
        IpDTO region = IPAPI.getIP(clientIp);

        try (EntityManager em = emf.createEntityManager()) {
            UrlTracking urlTracking;
            UrlTracking found = em.find(UrlTracking.class,url.getShortUrl());
            if (found == null) {
                urlTracking = new UrlTracking(url.getShortUrl(), region.getRegion(), 1);
            } else {
                urlTracking = new UrlTracking(url.getShortUrl(), region.getRegion(), found.getCount()+1);
            }
            em.getTransaction().begin();
            em.persist(urlTracking);
            em.getTransaction().commit();
        }
    }
}
