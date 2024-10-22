package app.daos;

import app.dtos.IpDTO;
import app.dtos.UrlDTO;
import app.entities.Url;
import app.entities.UrlTracking;
import app.service.IPAPI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
        IpDTO ip = IPAPI.getIP(clientIp);

        try (EntityManager em = emf.createEntityManager()) {
            UrlTracking urlTracking;
//            UrlTracking found = em.find(UrlTracking.class, url.getShortUrl());
            TypedQuery<UrlTracking> query = em.createQuery("SELECT u FROM UrlTracking u WHERE u.url = :shorturl", UrlTracking.class);
            query.setParameter("shorturl",url.getShortUrl());
            List<UrlTracking> found = query.getResultList();
            if (found.isEmpty()) {
                urlTracking = new UrlTracking(url.getShortUrl(), ip.getRegionName(), 1);
            } else {
                urlTracking = new UrlTracking(url.getShortUrl(), ip.getRegionName(), found.get(0).getCount()+1);
            }
            em.getTransaction().begin();
            em.persist(urlTracking);
            em.getTransaction().commit();
        }
    }
}
