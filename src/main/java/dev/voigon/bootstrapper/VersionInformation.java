package dev.voigon.bootstrapper;

import java.io.IOException;
import java.util.Properties;

/**
 * Stores information about bootstrapper from version.properties
 */
/* package-private */ class VersionInformation {

    static {
        String version;

        Properties properties = new Properties();
        try {
            properties.load(VersionInformation.class.getClassLoader().getResourceAsStream("version.properties"));
        } catch (IOException e) {
            version = null;
        }

        version = properties.getProperty("version");
        VERSION = version;
    }

    /* package-private */ static final String
            VERSION;

}
