/**
 * @author Mr Dk.
 * @version 2019/09/06
 * 
 * The logger component.
 */

package iot.zjt.backend.component;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class Logger {

    public static void init() throws IOException {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
        ConfigurationSource source = new ConfigurationSource(new FileInputStream("config/log4j2.xml"));
        Configurator.initialize(null, source);
    }
}