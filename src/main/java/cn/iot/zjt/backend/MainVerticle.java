package cn.iot.zjt.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.iot.zjt.backend.component.Database;
import cn.iot.zjt.backend.component.Token;
import cn.iot.zjt.backend.component.WebServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Initialization work before server listening.
 *
 * @version 2021/10/18
 */
public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Token
      .initTokenProvider(getVertx(), config())
      .compose(nop -> {
        logger.info("JWT token provider ready.");

        return Database.initMysqlPool(getVertx(), config());
      })
      .compose(mysql -> {
        logger.info("MySQL server ready.");

        return WebServer.initServer(getVertx(), config());
      })
      .compose(webServer -> {
        logger.info("Succeed to initialize web server.");
        logger.info("Web server listening at: " + webServer.actualPort() + ".");
        
        return Future.succeededFuture();
      })
      .onFailure(error -> {
        logger.error("Failed to initialize server.");
        logger.error(error.getMessage(), error);
      });
  }
}
