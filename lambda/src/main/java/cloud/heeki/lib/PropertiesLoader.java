package cloud.heeki.lib;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties loadProperties(String file) {
        Properties props = new Properties();
        try {
            InputStream input = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(file);
            props.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}