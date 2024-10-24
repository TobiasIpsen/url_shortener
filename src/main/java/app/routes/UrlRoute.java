package app.routes;

import app.controllers.UrlController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UrlRoute {

    private final UrlController urlController = new UrlController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", urlController::getAll, Role.ADMIN);
            get("/{shortUrl}", urlController::get, Role.USER);
            post("/", urlController::create, Role.USER);
            delete("/{url}", urlController::delete, Role.USER);
            put("/{url}", urlController::update, Role.USER);
        };
    }
}
