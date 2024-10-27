package app.controllers;

import app.config.HibernateConfig;
import app.daos.TrackingDAO;
import app.daos.UrlDAO;
import app.dtos.UrlDTO;
import app.dtos.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UrlController implements IController {

    UrlDAO urlDAO;
    TrackingDAO trackingDAO;
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    public UrlController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
        this.urlDAO = UrlDAO.getInstance(emf);
        this.trackingDAO = TrackingDAO.getInstance(emf);
        logger.info("UrlController initialized");
    }

    @Override
    public void getAll(Context ctx) {
        // DTO
        List<UrlDTO> urlDTO = urlDAO.getAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(urlDTO, UrlDTO.class);
    }

    @Override
    public void get(Context ctx) {
        String shortUrl = ctx.pathParam("shortUrl");
        String clientIp = ctx.header("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = ctx.ip();
        }
        // DTO
        UrlDTO urlDTO = urlDAO.getLongUrl(shortUrl);
        trackingDAO.count(urlDTO, clientIp);
        // Response/Redirect
        ctx.redirect(urlDTO.getLongUrl());
    }

    @Override
    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");

        UrlDTO urlDTO = ctx.bodyValidator(UrlDTO.class)
                .check(u -> u.getLongUrl() != null, "Long url is empty")
                .get();

        if (urlDTO.getLongUrl() == null || urlDTO.getLongUrl().trim().isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(Map.of("Error", "longUrl cannot be empty"));
            return;
        }

        UrlDTO newShortUrl = urlDAO.create(urlDTO, user);
        ctx.status(HttpStatus.CREATED);
        ctx.json(newShortUrl);
    }

    @Override
    public void update(Context ctx) {

        String shortUrl = ctx.pathParam("shortUrl");

        UrlDTO urlDTO = ctx.bodyValidator(UrlDTO.class)
                .check(u -> u.getLongUrl() != null, "Long url is empty")
                .get();

        if (urlDTO.getLongUrl() == null || urlDTO.getLongUrl().trim().isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(Map.of("Error", "longUrl cannot be empty"));
            return;
        }

        UrlDTO changedURL = urlDAO.updateLong(shortUrl, urlDTO);

        ctx.status(HttpStatus.OK);
        ctx.json(changedURL);
    }

    @Override
    public void delete(Context ctx) {
        String shortUrl = ctx.pathParam("shortUrl");
        urlDAO.delete(shortUrl);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
