package app.config;

import app.entities.Ip;
import app.entities.Url;
import app.entities.UrlTracking;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.PrintStream;
import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HibernateConfigV2 {

    static PrintStream originalErr = System.err;
    private static EntityManagerFactory entityManagerFactory;



    // TODO: IMPORTANT: Add Entity classes here for them to be registered with Hibernate
    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Url.class);
        configuration.addAnnotatedClass(UrlTracking.class);
    }



    public static EntityManagerFactory getEntityManagerFactoryConfig(boolean isTest) {
        if (isTest) {
            entityManagerFactory = buildEntityFactoryConfigTest();
        }
        if (entityManagerFactory == null) entityManagerFactory = buildEntityFactoryConfigDev();
        return entityManagerFactory;
    }

    private static EntityManagerFactory buildEntityFactoryConfigDev() {
        try {
            System.setErr(System.out);
            Configuration configuration = new Configuration();

            Properties props = new Properties();

            // Base Properties
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
            props.put("hibernate.hbm2ddl.auto", "create-drop"); // hibernate creates tables based on entities
            props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
            props.put("hibernate.show_sql", "true"); // show sql in console
            props.put("hibernate.format_sql", "true"); // format sql in console
            props.put("hibernate.use_sql_comments", "true"); // show sql comments in console
            props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes

            if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                setDevProperties(props);
            }

            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        } finally {
            System.setErr(originalErr);
        }
    }

    private static Properties setDeployedProperties(Properties props) {
        String DBName = System.getenv("DB_NAME");
        props.put("hibernate.connection.url", System.getenv("CONNECTION_STR") + DBName);
        props.put("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.put("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props) {
        String DBName = Utils.getPropertyValue("DB_NAME","config.properties");
        props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/" + DBName);
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        return props;
    }

    private static EntityManagerFactory buildEntityFactoryConfigTest() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
            props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test-db");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.show_sql", "true");
            props.put("hibernate.hbm2ddl.auto", "create-drop");
            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);

        getAnnotationConfiguration(configuration);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        System.out.println("Hibernate Java Config serviceRegistry created");

        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }
}
