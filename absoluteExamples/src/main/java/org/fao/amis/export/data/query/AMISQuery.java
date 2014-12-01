package org.fao.amis.export.data.query;

import org.apache.log4j.Logger;
import org.fao.amis.export.configuration.URLGetter;
import org.fao.amis.export.data.utils.dataUtils.DataUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by fabrizio on 11/7/14.
 */
public class AMISQuery {

    private static final Logger LOGGER = Logger.getLogger(AMISQuery.class);

    private static  int[] MAIZE_AND_WHEAT_FOOD,  RICE_FOOD,  SOYBEANS_FOOD, COMMODITIES ;

    private static  String URL_NATIONAL, URL_INTERNATION, URL_OTHERS;

    private HashMap< Integer, HashMap<Integer, String>> foodBalanceElements;

    private HashMap< Integer, HashMap<Integer, String>> ityElements;

    private HashMap< Integer, HashMap<Integer, String>> otherElements;

    private DataUtils dataUtils;

    private URLGetter urlGetter;

    public AMISQuery(){
        this.urlGetter = new URLGetter();
        this.MAIZE_AND_WHEAT_FOOD = this.urlGetter.getMaizeAndWheatCodes();
        this.RICE_FOOD = this.urlGetter.getRiceCodes();
        this.SOYBEANS_FOOD = this.urlGetter.getSoybeanCodes();
        this.COMMODITIES = this.urlGetter.getCommodityCodes();

        this.URL_NATIONAL = this.urlGetter.getNationalProperties();
        this.URL_INTERNATION = this.urlGetter.getInternationalProperties();
        this.URL_OTHERS = this.urlGetter.getOthersProperties();
    }

    public  void init() throws IOException {
        try {

            initMaps( "national");
            initMaps("international");
            initMaps("others");

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void initMaps(String type) throws IOException {

       // InputStream inputStream = null;

         if(type.equals("national")){
             dataUtils = new DataUtils();

             Properties prop = new Properties();
             String propFileName = null;
             this.foodBalanceElements = new HashMap<Integer, HashMap<Integer, String>>();
             propFileName = URL_NATIONAL;
             InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
             prop.load(inputStream);

             System.out.println("INPUTTT");
             System.out.println(inputStream);

             this.foodBalanceElements = new HashMap<Integer, HashMap<Integer, String>>();


             dataUtils.fillMap(this.foodBalanceElements, COMMODITIES, MAIZE_AND_WHEAT_FOOD, RICE_FOOD, SOYBEANS_FOOD, prop);
             LOGGER.warn("*****************************************");
             LOGGER.warn("finished fill map");

             LOGGER.debug( this.foodBalanceElements.toString());
         }



        else if(type.equals("international")){
             dataUtils = new DataUtils();

             Properties prop = new Properties();
             String propFileName = null;
             this.ityElements = new HashMap<Integer, HashMap<Integer, String>>();
             propFileName = URL_INTERNATION;
             InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
             prop.load(inputStream);

             dataUtils.fillMap(this.ityElements,COMMODITIES,MAIZE_AND_WHEAT_FOOD,RICE_FOOD,SOYBEANS_FOOD, prop);
             LOGGER.debug("*****************************************");
             LOGGER.debug("ITY ELEMENTS");

            LOGGER.debug(this.ityElements.toString());

         }

        else if(type.equals("others")){
             dataUtils = new DataUtils();

             Properties prop = new Properties();
             String propFileName = null;
             this.otherElements = new HashMap<Integer, HashMap<Integer, String>>();
             propFileName = URL_OTHERS;
             InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

             prop.load(inputStream);
             LOGGER.debug("*****************************************");
             LOGGER.debug("OTHERS ELEMENTS PROPERTIES");
             LOGGER.debug(prop.toString());
             dataUtils.fillMap(this.otherElements,COMMODITIES,MAIZE_AND_WHEAT_FOOD,RICE_FOOD,SOYBEANS_FOOD, prop);
         }
    }

    public HashMap<Integer, HashMap<Integer, String>> getFoodBalanceElements() {
        return foodBalanceElements;
    }

    public HashMap<Integer, HashMap<Integer, String>> getItyElements() {
        return ityElements;
    }

    public HashMap<Integer, HashMap<Integer, String>> getOtherElements() {
        return otherElements;
    }

}
