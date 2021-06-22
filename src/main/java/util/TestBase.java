package util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class TestBase {
      public static Properties prop;
      public static Logger log;

      public static void init()
    {

        PropertyConfigurator.configure("src/main/java/configuration/log4j.properties");
        prop=new Properties();
        try {
            FileInputStream file = new FileInputStream("src/main/java/configuration/config.properties");
            prop.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
