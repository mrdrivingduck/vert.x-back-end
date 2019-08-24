/**
 * @author Mr Dk.
 * @version 2019/08/24
 * 
 * The entry point of server.
 */

package iot.zjt;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class StartUp {

    private static Logger logger = null;

    public static void main(String[] args) {
    
        try {
            // Logger init
            loggerInit();

        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("Server start up failed");
        }
    }

    private static void loggerInit() throws IOException {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
        ConfigurationSource source = new ConfigurationSource(new FileInputStream("config/log4j2.xml"));
        Configurator.initialize(null, source);
        logger = LogManager.getLogger(StartUp.class.getName());
        logger.info("Logger init ok.");
    }
}