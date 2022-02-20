package cn.iot.zjt.backend.component;

import cn.iot.zjt.backend.handler.restful.RestfulDeleteHandler;
import cn.iot.zjt.backend.handler.restful.RestfulGetHandler;
import cn.iot.zjt.backend.handler.restful.RestfulPostHandler;
import cn.iot.zjt.backend.handler.restful.RestfulPutHandler;
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
 * @version 2022/02/20
 */
public class WebServer {

  private static Router initRouter(final Vertx vertx, final JsonObject config) {
    Router router = Router.router(vertx);

    new StatusHandler().register(router);

    new LoginHandler().register(router);

    new RestfulPostHandler(vertx, config).register(router);
    new RestfulGetHandler(vertx, config).register(router);
    new RestfulPutHandler(vertx, config).register(router);
    new RestfulDeleteHandler(vertx, config).register(router);

    return router;
  }

  public static Future<HttpServer> initServer(final Vertx vertx, final JsonObject config) {
    String  privateKey   = Config.CONFIG_BASE + config.getString("server.tls.key");
    String  certificate  = Config.CONFIG_BASE + config.getString("server.tls.cert");
    boolean httpsEnabled = config.getBoolean("server.tls.enabled", false);
    int port = config.getInteger("server.web.bind");

    HttpServerOptions options = new HttpServerOptions()
      .setSsl(httpsEnabled)  // use HTTPS
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
