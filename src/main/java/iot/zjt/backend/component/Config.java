/**
 * @author Mr Dk.
 * @version 2019/09/06
 * 
 * The configuration component.
 */

package iot.zjt.backend.component;

import java.io.File;
import java.io.IOException;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Config {

    private static Wini ini = null;

    public static void init() throws IOException, InvalidFileFormatException {
        ini = new Wini(new File("config/config.ini"));
    }

    public static String getConfig(String section, String key) {
        return ini.get(section, key);
    }
}