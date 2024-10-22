package app.controllers;

import io.javalin.http.Context;

public interface IController {

    void getAll(Context ctx);
    void get(Context ctx);
    void create(Context ctx);
    void delete(Context ctx);
    void update(Context ctx);

}
