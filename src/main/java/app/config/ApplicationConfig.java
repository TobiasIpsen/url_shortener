package app.config;

import app.routes.Routes;
import app.security.controllers.AccessController;
import app.security.enums.Role;
import app.security.exceptions.ApiException;
import app.security.routes.SecurityRoutes;
import app.utils.Utils;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationConfig {

    private static Routes routes = new Routes();
//    private static SecurityController securityController = SecurityController.getInstance();
    private static AccessController accessController = new AccessController();
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE);
        config.router.contextPath = "/api/v1"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        app.beforeMatched(accessController::accessHandler);

        app.exception(ApiException.class, ApplicationConfig::apiExceptionHandler);
        app.exception(EntityNotFoundException.class, ApplicationConfig::entityNotFound);
        app.exception(Exception.class, ApplicationConfig::generalExceptionHandler);

        app.before(ApplicationConfig::corsHeaders);
        app.options("/*", ApplicationConfig::corsHeadersOptions);

        app.start(port);
        return app;
    }

    private static void corsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    private static void corsHeadersOptions(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.status(204);
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
