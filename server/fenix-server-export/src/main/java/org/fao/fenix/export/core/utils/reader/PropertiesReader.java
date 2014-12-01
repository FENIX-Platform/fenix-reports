package org.fao.fenix.export.core.utils.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fabrizio on 12/1/14.
 */
public class PropertiesReader {

    private static PropertiesReader readerInstance;

    private PropertiesReader(){}

    public static PropertiesReader getInstance() {

        if (readerInstance == null)
        {
            readerInstance = new PropertiesReader();
        }

        return readerInstance;
    }

    public String getPropertyValue(String urlProperties, String key){

        Properties prop =  new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(urlProperties);
        try {
           prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }
}
