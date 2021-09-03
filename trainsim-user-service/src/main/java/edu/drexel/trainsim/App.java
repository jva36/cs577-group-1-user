package edu.drexel.trainsim;

import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.zaxxer.hikari.HikariConfig;
import org.sql2o.Sql2o;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;

import edu.drexel.trainsim.user.DatabaseModule;
import edu.drexel.trainsim.web.*;

public class App {
    public static void main(String[] args) throws Exception {
        // Wait until OTP is fully initilized
        // TOOD: There are certainly better ways to do this.
        Thread.sleep(2000);

        // Database
        var hikari = new HikariConfig();
        hikari.setJdbcUrl(getEnv("DB_URL"));
        hikari.setUsername(getEnv("DB_USER"));
        hikari.setPassword(getEnv("DB_PASSWORD"));

        // Dependency injection
        var injector = Guice.createInjector(
            new DatabaseModule(hikari)
        );

        // Prepopulate routes and stops
        var db = injector.getInstance(Sql2o.class);

        // Web server
        var gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        var app = Javalin.create(config -> {
            config.enableDevLogging();
            config.enableCorsForAllOrigins();
        });

        // Setup controllers
        injector.getInstance(UserController.class).bindRoutes(app);

        // Start the web server
        app.start(80);
    }

    private static String getEnv(String name) {
        var value = System.getenv(name);

        if (value == null) {
            final String message = "Environment variable `%s` is required.";
            throw new RuntimeException(String.format(message, name));
        }

        return value;
    }
}
