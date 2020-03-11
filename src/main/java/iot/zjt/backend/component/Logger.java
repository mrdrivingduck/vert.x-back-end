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
 */
public class Logger {

    public static void init() throws IOException {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                            "io.vertx.core.logging.Log4j2LogDelegateFactory");
        ConfigurationSource source = new ConfigurationSource(
            new FileInputStream((String) CliParameters.getCli()
                                    .getArgumentValue("logger config")));
        Configurator.initialize(null, source);
    }
}