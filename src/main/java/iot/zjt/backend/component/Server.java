package iot.zjt.backend.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * The HTTP server component.
 * 
 * @author Mr Dk.
 * @since 2020/03/09
 */
public class Server {
    
    private static Server instance = null;
    private Server() {}

    private HttpServer server;
    private Logger logger = LogManager.getLogger(Server.class);

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void init(final Vertx vertx) {
        // options
        server = vertx.createHttpServer();
    }

    public void run(final Router router) {
        int port = Integer.parseInt(Config.getConfig().get("server", "port"));
        server.requestHandler(router);
        server.listen(port, res -> {
            if (res.succeeded()) {
                StringBuilder sb = new StringBuilder("Server listening at: ");
                sb.append(port);
                logger.warn(sb.toString());
            } else {
                logger.error(res.cause().getMessage());
            }
        });
    }
}