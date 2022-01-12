package cn.iot.zjt.backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.iot.zjt.backend.component.Config;

/**
 * Reading configuration and start main verticle.
 *
 * @version 2021/04/13
 */
public class LauncherVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(LauncherVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    cn.iot.zjt.backend.component.LoggerInitializer.init();
    logger.info("Logger initialized.");

    String configPath = Config.CONFIG_BASE + Config.CONFIG_FILE;

    Config
      .initConfig(getVertx(), configPath)
      .compose(config -> {
        logger.info("Found configuration file.");
        getVertx().deployVerticle(new MainVerticle(),
                                  new DeploymentOptions().setConfig(config));
        startPromise.complete();
        return Future.succeededFuture();
      })
      .onFailure(error -> {
        logger.error(error.getMessage(), error);
        startPromise.fail(error);
        vertx.close(ar -> {
          logger.warn("Server is shut down gracefully.");
        });
      });
  }
}
