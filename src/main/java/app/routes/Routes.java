package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final UrlRoute urlRoute = new UrlRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/class", urlRoute.getRoutes());
        };
    }

}
