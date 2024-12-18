package app.routes;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.dtos.UserDTO;
import app.entities.Url;
import app.security.controllers.SecurityController;
import app.security.daos.SecurityDAO;
import app.security.exceptions.ValidationException;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UrlRouteTest {

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(true);
    private static final SecurityController securityController = SecurityController.getInstance();
    private static final SecurityDAO securityDAO = new SecurityDAO(emf);
    private static Javalin app;
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static List<Url> urlList = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost:7070/api/v1";
        app = ApplicationConfig.startServer(7070);
    }

    @BeforeEach
    void beforeEach() {
        UserDTO[] users = Populate.populateUsers(emf);
        userDTO = users[0];
        adminDTO = users[1];
        urlList = Populate.populateUrls(emf);

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO);
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO);
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        urlList = new ArrayList<>();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE Role").executeUpdate();
            em.createQuery("DELETE UrlTracking").executeUpdate();
            em.createQuery("DELETE Url").executeUpdate();
            em.createQuery("DELETE User").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void afterAll() {
        ApplicationConfig.stopServer(app);
    }

//    @Test
//    @DisplayName("Get All Urls")
//    void getAllUrls() {
//        given()
//                .when()
//                .header("Authorization", adminToken)
//                .get("/urls")
//                .then()
//                .log().all()
//                .statusCode(200)
//                .body("size()", is(3));
//    }

    @Test
    @DisplayName("Get Single Url")
    void getSingleUrl() {
        given()
                .when()
                .header("Authorization", userToken)
                .log().all()
                .get("/urls/" + urlList.get(0).getShortUrl())
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("Create URL")
    void creatUrl() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", userToken)
                .accept("application/json")
                .body(Map.of("longUrl", "https://www.reddit.com/"))
                .when()
                .post("/urls")
                .then()
                .statusCode(201)
                .body("longUrl", is("https://www.reddit.com/"))
                .assertThat()
                .body("shortUrl.length()", equalTo(6));
    }

    @Test
    @DisplayName("Create URL with Empty body")
    void creatUrlEmptyBody() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", userToken)
                .accept("application/json")
                .body("{}")
                .when()
                .post("/urls")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create URL with Empty longUrl")
    void creatUrlEmptyLongUrl() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", userToken)
                .accept("application/json")
                .body(Map.of("longUrl", ""))
                .when()
                .post("/urls")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Delete URL")
    void deleteUrl() {
        System.out.println(urlList.get(0).getShortUrl());
        given()
                .header("Authorization", userToken)
                .when()
                .delete("/urls/" + urlList.get(0).getShortUrl())
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Update url")
    void updateUrl() {
        given()
                .contentType("application/json")
                .accept("application/json")
                .header("Authorization", userToken)
                .body(Map.of("longUrl", "https://www.github.com/"))
                .when()
                .put("/urls/" + urlList.get(0).getShortUrl())
                .then()
                .statusCode(200)
                .assertThat()
                .body("longUrl", is("https://www.github.com/"));
    }

}