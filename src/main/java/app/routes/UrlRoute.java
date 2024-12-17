package app.routes;

import app.controllers.UrlController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UrlRoute {

    private final UrlController urlController = new UrlController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", urlController::getAll, Role.ANYONE);
            get("/{shortUrl}", urlController::get, Role.ANYONE);
            post("/", urlController::create, Role.USER);
            put("/{shortUrl}", urlController::update, Role.USER);
            delete("/{shortUrl}", urlController::delete, Role.USER);
        };
    }
}
