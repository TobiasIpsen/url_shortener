package app.routes;

import app.config.ApplicationConfigV2;
import app.config.HibernateConfigV2;
import app.config.Populate;
import app.daos.UrlDAO;
import app.entities.Url;
import app.security.enums.Role;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class UrlRouteTest {


    private static EntityManagerFactory emf = HibernateConfigV2.getEntityManagerFactoryConfig(true);
    private static List<Url> urlList = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "localhost:7070/api/v1";
        ApplicationConfigV2.startServer(7070);
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

    /*
    get("/",urlController::getAll, Role.ADMIN);
    get("/{shortUrl}", urlController::get, Role.USER);
    post("/", urlController::create, Role.USER);
    delete("/{url}", urlController::delete, Role.USER);
    put("/{shortUrl}", urlController::update, Role.USER);
    */

    @Test
    @DisplayName("Get All Urls")
    void getAllUrls() {
        given()
                .when()
                .get("/url")
                .then()
                .log().all()
                .statusCode(200);
    }





}