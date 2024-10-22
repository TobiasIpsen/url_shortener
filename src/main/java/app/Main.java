package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig.startServer(7070);
    }
}