package cn.iot.zjt.backend.component;

import cn.iot.zjt.backend.handler.web.LoginHandler;
import cn.iot.zjt.backend.handler.web.StatusHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;

/**
 * Initialize the HTTPS server for web.
 *
 * @version 2021/11/02
 */
public class WebServer {

  private static Router initRouter(final Vertx vertx, final JsonObject config) {
    Router router = Router.router(vertx);

    new StatusHandler().register(router);

    new LoginHandler().register(router);

    return router;
  }

  public static Future<HttpServer> initServer(final Vertx vertx, final JsonObject config) {
    String privateKey = Config.CONFIG_BASE + config.getString("server.tls.key");
    String certificate = Config.CONFIG_BASE + config.getString("server.tls.cert");
    int port = config.getInteger("server.web.bind");

    HttpServerOptions options = new HttpServerOptions()
      .setSsl(false)  // use HTTPS
      .setPemKeyCertOptions(new PemKeyCertOptions()
        .setKeyPath(privateKey)
        .setCertPath(certificate)
      );

    Router router = initRouter(vertx, config);

    return vertx
      .createHttpServer(options)
      .requestHandler(router)
      .listen(port);
  }
}
