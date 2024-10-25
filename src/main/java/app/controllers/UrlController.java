package app.controllers;

import app.config.HibernateConfigV2;
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

public class UrlController implements IController {

    UrlDAO urlDAO;
    TrackingDAO trackingDAO;
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    public UrlController() {
        EntityManagerFactory emf = HibernateConfigV2.getEntityManagerFactoryConfig(false);
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
        if (clientIp == null) {
            clientIp = ctx.ip();
        }
        // DTO
        UrlDTO urlDTO = urlDAO.getLongUrl(shortUrl);
        trackingDAO.count(urlDTO, "185.107.176.36");
        // Respoonse/Redirect
        ctx.redirect(urlDTO.getLongUrl());
    }

    @Override
    public void create(Context ctx) {
        if (ctx.body().isEmpty()) {
            ctx.json("Request is empty");
        } else {
            UrlDTO urlDTO = ctx.bodyAsClass(UrlDTO.class);
            UserDTO user = ctx.attribute("user");
            UrlDTO newShortUrl = urlDAO.create(urlDTO, user);
            ctx.status(HttpStatus.CREATED);
            ctx.json(newShortUrl);
        }
    }

    @Override
    public void delete(Context ctx) {
        String shortUrl = ctx.pathParam("shortUrl");
        urlDAO.delete(shortUrl);
        ctx.status(HttpStatus.OK);
    }

    @Override
    public void update(Context ctx) {

        String shortUrl = ctx.pathParam("shortUrl");

//        UrlDTO urlDTO = ctx.bodyAsClass(UrlDTO.class);
        UrlDTO urlDTO = ctx.bodyValidator(UrlDTO.class)
                .check(u -> u.getLongUrl() != null, "Long url is empty")
//                .check(u -> u.getShortUrl() != null,"Short url is empty")
                .get();

        UrlDTO changedURL = urlDAO.updateLong(shortUrl, urlDTO);

        ctx.status(HttpStatus.OK);
        ctx.json(changedURL);
    }
}
