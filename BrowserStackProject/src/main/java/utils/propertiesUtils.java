package utils; 

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class propertiesUtils {
    private static Properties prop;

    // Load properties only once
    static {
        prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/details.properties");
            prop.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get property values
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
