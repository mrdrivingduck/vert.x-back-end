/**
 * @author Mr Dk.
 * @version 2019/09/25
 * 
 * The HTTP server component.
 */

package iot.zjt.backend.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

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
        int port = Integer.parseInt(Config.getConfig("server", "port"));
        server.requestHandler(router);
        server.listen(port, "localhost", res -> {
            if (res.succeeded()) {
                StringBuilder sb = new StringBuilder("Server listening at: ");
                sb.append("localhost");
                sb.append(":");
                sb.append(port);
                logger.info(sb.toString());
            } else {
                logger.error(res.cause().getMessage());
            }
        });
    }
}