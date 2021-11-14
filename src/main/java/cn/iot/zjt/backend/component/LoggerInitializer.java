package cn.iot.zjt.backend.component;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoggerInitializer {

  public static void init() throws IOException {
    System.setProperty(
      "vertx.logger-delegate-factory-class-name",
      "io.vertx.core.logging.Log4j2LogDelegateFactory"
    );
    System.setProperty("log4j2.skipJansi", "false");  // VM options
    System.setProperty("sun.stdout.encoding", "UTF-8");
    ConfigurationSource source = new ConfigurationSource(
      Logger.class.getResourceAsStream("/log4j2.xml")
    );
    Configurator.initialize(null, source);
  }
}
