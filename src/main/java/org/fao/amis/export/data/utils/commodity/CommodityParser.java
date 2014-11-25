package org.fao.amis.export.data.utils.commodity;


import org.fao.amis.export.configuration.URLGetter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fabrizio on 11/10/14.
 */
public class CommodityParser {

    private static String COMMODITY_URL;

    private Properties prop;

    private URLGetter urlGetter;

    public CommodityParser(){
        this.urlGetter = new URLGetter();
        this.COMMODITY_URL = this.urlGetter.getCommodityProperties();
        this.prop=  new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(COMMODITY_URL);

        try {
            this.prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getCommodityLabel(String code){
        return this.prop.getProperty(code);
    }
}
