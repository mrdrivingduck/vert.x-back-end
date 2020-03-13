package iot.zjt.backend.component;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * The logger component.
 * 
 * @author Mr Dk.
 * @since 2020/03/11
 * @see <a href="https://vertx.io/docs/vertx-core/java/#_logging">Vert.x Logger</a>
 * @see <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">Log4J Configuration</a>
 * @see <a href="http://logging.apache.org/log4j/2.x/manual/layouts.html">Log4J Layouts</a>
 */
public class Logger {

    public static void init() throws IOException {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                            "io.vertx.core.logging.Log4j2LogDelegateFactory");
        System.setProperty("log4j2.skipJansi", "false");
        ConfigurationSource source = new ConfigurationSource(
            new FileInputStream(Config.getConfig().get("logger", "config")));
        Configurator.initialize(null, source);
    }
}