package org.fao.fenix.export.core.utils.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


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
        try {
            return getProperties(urlProperties).getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Properties getProperties(String urlProperties) throws IOException {

        Properties prop =  new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(urlProperties);
        prop.load(inputStream);
        return prop;
    }
}
