package iot.zjt.backend;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import iot.zjt.backend.component.Config;
import iot.zjt.backend.component.Database;
import iot.zjt.backend.component.HttpRouter;
import iot.zjt.backend.component.Server;
import iot.zjt.backend.component.VertxInstance;

/**
 * The entry point of server.
 * 
 * @author Mr Dk.
 * @version 2020/03/09
 */
public class StartUp {

    private static Logger logger = null;

    private static Future<Void> dbCompleteFuture = null;

    public static void main(String[] args) {
        try {
            initLogger(); // Init the logger
            initConfig(); // Init the configuration
            initPath(); // Init some path as system properties
            initVertx(); // Init the Vert.x instance
            initRouter(); // Init the HTTP router
            initDatabase(); // Init the database connection pool
            initServer(); // Init the HTTP server

        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("Server start up failed");
        }
    }

    /**
     * Initialize the logger configuration.
     * 
     * @throws IOException
     */
    private static void initLogger() throws IOException {
        iot.zjt.backend.component.Logger.init();
        logger = LogManager.getLogger(StartUp.class);
        logger.info("Logger init ok.");
    }

    /**
     * Initialize the configuration file.
     * 
     * @throws IOException
     * @throws InvalidFileFormatException
     */
    private static void initConfig() throws IOException, InvalidFileFormatException {
        Config.init();
    }

    /**
     * The Vert.x instance.
     */
    private static void initVertx() {
        // VertxInstance.init();
    }

    /**
     * Initialize the path of some directories.
     */
    private static void initPath() {
        Section pathSection = Config.getConfig().get("path");
        for (String key : pathSection.keySet()) {
            System.setProperty(key, pathSection.get(key));
        }
    }

    /**
     * Initialize database connections.
     */
    private static void initDatabase() {
        Database.init(VertxInstance.getInstance());
        dbCompleteFuture = Future.future(promise -> {
            Database.test(promise);
        });
    }

    /**
     * Initialize the router to handlers.
     */
    private static void initRouter() {
        HttpRouter.getInstance().init(VertxInstance.getInstance());
    }

    /**
     * Initialize the web server and start listening.
     */
    private static void initServer() {
        Server.getInstance().init(VertxInstance.getInstance());
        CompositeFuture.all(Arrays.asList(dbCompleteFuture)).setHandler(res -> {
            if (res.succeeded()) {
                Server.getInstance().run(HttpRouter.getInstance().getRouter());
            } else {
               logger.error(res.cause().getMessage());
            }
        });
    }
}
