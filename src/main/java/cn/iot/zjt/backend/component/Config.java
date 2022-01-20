package cn.iot.zjt.backend.component;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Configuration initialization.
 *
 * @version 2022/01/20
 */
public class Config {

  public static final String CONFIG_BASE = "conf/";
  public static final String CONFIG_FILE = "config.json";

  public static final String API_VERSION = "0.0.1";

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
