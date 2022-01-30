package cn.iot.zjt.backend.component;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Configuration initialization.
 *
 * @version 2022/01/30
 */
public class Config {

  private static final Logger logger = LogManager.getLogger(Config.class);

  public static final String CONFIG_BASE = "conf/";
  public static final String CONFIG_FILE = "config.json";

  private static Properties properties;
  public static final String API_VERSION() {
    String version = "";
    try {
      if (properties == null) {
        properties = new Properties();
        properties.load(
          Config.class
            .getClassLoader()
            .getResourceAsStream("project.properties")
        );
      }
      version = properties.getProperty("api.version");
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

    return version;
  }

  public static ConfigRetriever initConfig(final Vertx vertx, final String path) {
    ConfigStoreOptions configStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", path));
    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
      .addStore(configStore);
    return ConfigRetriever
      .create(vertx, options);
  }
}
