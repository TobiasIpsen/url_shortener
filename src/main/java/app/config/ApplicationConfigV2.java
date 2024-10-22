package app.config;

import app.routes.Routes;
import app.security.controllers.AccessController;
import app.security.controllers.SecurityController;
import app.security.enums.Role;
import app.security.exceptions.ApiException;
import app.security.routes.SecurityRoutes;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfigV2 {

    private static Routes routes = new Routes();
//    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
//    private static SecurityController securityController = SecurityController.getInstance();
    private static AccessController accessController = new AccessController();
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfigV2.class);

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE);
        config.router.contextPath = "/api/v1"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfigV2::configuration);

//        app.beforeMatched(accessController::accessHandler);

        app.exception(Exception.class, ApplicationConfigV2::generalExceptionHandler);
        app.exception(ApiException.class, ApplicationConfigV2::apiExceptionHandler);
        app.exception(EntityNotFoundException.class, ApplicationConfigV2::entityNotFound);

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    //--------------Exceptions--------------//

    private static void generalExceptionHandler(Exception e, Context ctx) {
        logger.error("An unhandled exception occurred", e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }

    public static void apiExceptionHandler(ApiException e, Context ctx) {
        ctx.status(e.getCode());
        logger.warn("An API exception occurred: Code: {}, Message: {}", e.getCode(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }

    public static void entityNotFound(Exception e, Context ctx) {
        ctx.status(HttpStatus.NOT_FOUND);
        logger.error("Entity Not Found:", e.getMessage());
        ctx.json(Map.of("Entity Not Found:", e.getMessage()));
    }
}
