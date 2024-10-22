package app.controllers;

import app.config.HibernateConfig;
import app.daos.UrlDAO;
import app.dtos.UrlDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UrlController implements IController {

    private final UrlDAO dao;

    public UrlController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
        this.dao = UrlDAO.getInstance(emf);
    }

    @Override
    public void getAll(Context ctx) {
        // DTO
        List<UrlDTO> urlDTO = dao.getAll();
        // response
        ctx.res().setStatus(200);
        ctx.json(urlDTO, UrlDTO.class);
    }

    @Override
    public void get(Context ctx) {
        String url = ctx.pathParam("url");
        // DTO
        // Respoonse/Redirect
    }

    @Override
    public void create(Context ctx) {

    }

    @Override
    public void delete(Context ctx) {

    }

    @Override
    public void update(Context ctx) {

    }
}
