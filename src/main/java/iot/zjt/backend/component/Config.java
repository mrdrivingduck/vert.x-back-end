package iot.zjt.backend.component;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

/**
 * The configuration component.
 * 
 * @author Mr Dk.
 * @version 2020/03/08
 */
public class Config {

    private static Wini ini = null;

    /**
     * Initialize the configuration from file.
     * 
     * @throws IOException
     * @throws InvalidFileFormatException
     */
    public static void init() throws IOException, InvalidFileFormatException {
        ini = new Wini(new File("config/config.ini"));
    }

    /**
     * Get the config content through section and key.
     * 
     * @param section Config section.
     * @param key Config key.
     * @return The config content.
     */
    public static String getConfig(String section, String key) {
        return ini.get(section, key);
    }

    /**
     * To judge whether a key is absent in config.
     * 
     * @param key Config key.
     * @return Whether the key exists.
     */
    public static boolean contains(String key) {
        return ini.containsKey(key);
    }
}