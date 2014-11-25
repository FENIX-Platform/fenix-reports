package org.fao.amis.export.data.forecast;


import org.apache.log4j.Logger;
import org.fao.amis.export.configuration.URLGetter;
import org.fao.amis.export.data.daoValue.DaoForecastValue;
import org.fao.amis.export.data.utils.dataUtils.DataUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Created by fabrizio on 11/8/14.
 */
public class Forecast {


    private static  String URL_FORECASTS ;

    private static final Logger LOGGER = Logger.getLogger(Forecast.class);

    private static int[] COMMODITY_LIST;

    private DataUtils dataUtils;

    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> unorderedMap;

    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> foodBalanceResults;

    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> ityResults;

    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> otherResults;

    private Properties prop;

    private URLGetter urlGetter;


    public Forecast() {

        this.urlGetter = new URLGetter();
        this.URL_FORECASTS = this.urlGetter.getForecastProperties();
        this.COMMODITY_LIST = this.urlGetter.getCommodityCodes();

        initProperties();
        this.foodBalanceResults = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>>();
        this.ityResults  = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>>();
        this.otherResults = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>>();
    }


    private void initProperties() {


        this.prop = new Properties();
        String propFileName = null;
        propFileName = URL_FORECASTS;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        try {
            this.prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void initData(Object[][] data) {

        this.unorderedMap =
                new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>>();

        int commodityIndex = Integer.parseInt(this.prop.getProperty("commodity"));
        int dateIndex = Integer.parseInt(this.prop.getProperty("date"));
        int codeIndex = Integer.parseInt(this.prop.getProperty("code"));


        for (int commodity : COMMODITY_LIST) {

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> semiMap =
                    new LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>();

            String commodityCode = "" + commodity;
            for (Object[] row : data) {


                if (commodityCode.equals("" + row[commodityIndex])) {
                    String date = "" + row[dateIndex];


                    if (!this.unorderedMap.containsKey(commodityCode)
                        || (this.unorderedMap.containsKey(commodityCode) &&
                        !this.unorderedMap.get(commodityCode).containsKey(date)) ||
                        (this.unorderedMap.containsKey(commodityCode) && this.unorderedMap.get(commodityCode).containsKey(date)
                        && !this.unorderedMap.get(commodityCode).get(date).containsKey(row[codeIndex]))) {


                        LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> temporary = createTempMap(data, commodityCode, date, commodityIndex, dateIndex);
                        semiMap.put(date, temporary.get(date));

                    }
                }
            }
            unorderedMap.put(commodityCode, semiMap);
        }


        LOGGER.debug("***************************************");
        LOGGER.debug("UNORDERED MAP");
        LOGGER.debug(unorderedMap);

    }

    // OK
    private LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> createTempMap(
            Object[][] data, String commodityCode, String date, int commodityIndex, int dateIndex) {

        LinkedHashMap<String, DaoForecastValue> tempMap = new LinkedHashMap<String, DaoForecastValue>();
        int codeIndex = Integer.parseInt(this.prop.getProperty("code"));

        int valueIndex = Integer.parseInt(this.prop.getProperty("value"));
        int flagsIndex = Integer.parseInt(this.prop.getProperty("flags"));
        int notesIndex = Integer.parseInt(this.prop.getProperty("notes"));

        int i=0;
        for (Object[] row : data) {
            i++;

            double value;


            if (((Integer.parseInt((String)row[commodityIndex] ))  == Integer.parseInt(commodityCode)) && (((String) row[dateIndex]).equals(date))){


                if(row[valueIndex] .equals("")){
                    value = -1.0;
                }else{
                    value = Double.parseDouble((String)row[valueIndex]);
                }

                DaoForecastValue valueModel = new DaoForecastValue("" + row[notesIndex], "" + row[flagsIndex], value);
                tempMap.put("" + row[codeIndex], valueModel);


            }
        }


        LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> result = new LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>();

        result.put(date, tempMap);

        return result;

    }


    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> getUnorderedMap() {
        return unorderedMap;
    }


    public void createOrderedMaps( HashMap< Integer, HashMap<Integer, String>> listElements, String type){
        dataUtils = new DataUtils();

        if(type.equals("national")){
          dataUtils.fillElementsMap(this.foodBalanceResults,listElements,this.unorderedMap);
        }
        else if(type.equals("international")){
            dataUtils.fillElementsMap(this.ityResults,listElements,this.unorderedMap);
        }
        else{
            dataUtils.fillElementsMap(this.otherResults,listElements,this.unorderedMap);

        }

    }



    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> getFoodBalanceResults() {
        return foodBalanceResults;
    }


    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> getItyResults() {
        return ityResults;
    }


    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> getOtherResults() {
        return otherResults;
    }


    public int[] getCommodityList(){

        return this.COMMODITY_LIST;
    }


}
