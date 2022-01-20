package cn.iot.zjt.backend;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.iot.zjt.backend.component.Config;
import cn.iot.zjt.backend.component.LoggerInitializer;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Reading configuration and start main verticle.
 *
 * @version 2022/01/20
 */
public class LauncherVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(LauncherVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LoggerInitializer.init();
    logger.info("Logger initialized.");

    String configPath = Config.CONFIG_BASE + Config.CONFIG_FILE;
    ConfigRetriever configRetriever = Config.initConfig(getVertx(), configPath);

    // listening for configuration file modification.
    configRetriever.listen(change -> {
      logger.warn("Configuration file '" + configPath + "' modification detected.");

      @SuppressWarnings("rawtypes")
      List<Future> undeployFutures = new ArrayList<>();
      for (String verticleId : getVertx().deploymentIDs()) {
        if (verticleId.equals(deploymentID())) {
          continue;  // skip for current verticle (LauncherVerticle)
        }
        undeployFutures.add(getVertx().undeploy(verticleId));
        logger.warn("Stoping deployed verticle: " + verticleId + ".");
      }

      CompositeFuture
        .all(undeployFutures)
        .compose(v -> {
          // all verticles (except LauncherVerticle) have been undeployed
          // deploy a new verticle with new configuration object now
          return getVertx().deployVerticle(
            new MainVerticle(),
            new DeploymentOptions().setConfig(change.getNewConfiguration())
          );
        })
        .compose(deployId -> {
          logger.warn("Verticle deployed: " + deployId + ".");
          return Future.succeededFuture();
        })
        .onFailure(error -> {
          logger.error(error.getMessage(), error);
          vertx.close(ar -> {
            logger.warn("Server is shutting down gracefully.");
          });
        });
    });

    // get configuration for the first time
    configRetriever
      .getConfig()
      .compose(config -> {
        logger.info("Configuration file found.");
        return getVertx().deployVerticle(new MainVerticle(),
                                         new DeploymentOptions().setConfig(config));
      })
      .compose(deployId -> {
        startPromise.complete();
        logger.warn("Verticle deployed: " + deployId + ".");
        return Future.succeededFuture();
      })
      .onFailure(error -> {
        logger.error(error.getMessage(), error);
        startPromise.fail(error);
        vertx.close(ar -> {
          logger.warn("Server is shutting down gracefully.");
        });
      });
  }
}
