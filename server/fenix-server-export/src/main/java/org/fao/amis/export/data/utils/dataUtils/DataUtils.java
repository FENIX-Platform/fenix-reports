package org.fao.amis.export.data.utils.dataUtils;


import org.apache.log4j.Logger;
import org.fao.amis.export.data.daoValue.DaoForecastValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Created by fabrizio on 11/7/14.
 */
public class DataUtils {

    private static final Logger LOGGER = Logger.getLogger(DataUtils.class);


    public void fillMap(HashMap<Integer, HashMap<Integer, String>> elementMap, int[] commodities,
                        int[] maizeAndWheatList,
                        int[] riceList, int[] soybeanList, Properties codeList) {


        LOGGER.debug("***********************");
        LOGGER.debug("Start fill map");

        for (int commodity : commodities) {

            HashMap<Integer, String> temporaryMap = new HashMap<Integer, String>();

            switch (commodity) {
                case 1:
                case 5:
                    LOGGER.info("5");

                    fillTempMap(temporaryMap, maizeAndWheatList, codeList);
                    break;
                case 4:
                    LOGGER.info("4");

                    fillTempMap(temporaryMap, riceList, codeList);
                    break;
                case 6:
                    LOGGER.info("6");

                    fillTempMap(temporaryMap, soybeanList, codeList);
                    break;
            }

            elementMap.put(commodity, temporaryMap);
        }

    }

    public void fillTempMap(HashMap<Integer, String> tmp, int[] listCodes, Properties codeList) {

        for (int code : listCodes) {
            LOGGER.info("VALUE");


            String value = codeList.getProperty(Integer.toString(code));
            LOGGER.info(value);
            if (value != null) {
                LOGGER.info("VALUE != null");
                if(value.equals("Yield - Tonnes/Ha")){
                    LOGGER.info("QISDA");
                }
                tmp.put(code, value);
            }
        }
    }


    public void fillElementsMap(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> toFillMap, HashMap<Integer, HashMap<Integer, String>> toFollow,
                                LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> unorderedMap) {

        Set<Integer> commodities = toFollow.keySet();

        for (int commodity : commodities) {

            HashMap<Integer, String> semiFollow = toFollow.get(commodity);

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> semiUnorderedMap = unorderedMap.get("" + commodity);

            Set<String> dates = semiUnorderedMap.keySet();

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> resultWithoutCommodity = new  LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>();

            for (String date : dates) {


                LinkedHashMap<String, DaoForecastValue> codesForecastsMap= semiUnorderedMap.get(date);

                Set<String> codes = semiUnorderedMap.get(date).keySet();

                resultWithoutCommodity.put(date, fillElementsByCodes(codes, semiFollow, codesForecastsMap));

            }

            toFillMap.put(""+commodity,resultWithoutCommodity);
        }
    }

    private  LinkedHashMap<String, DaoForecastValue>fillElementsByCodes(Set<String> codes,
                                                                          HashMap<Integer, String> semiFollow,
                                                                          LinkedHashMap<String, DaoForecastValue> codesForecastsMap) {


        LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> result = new LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>();
        LinkedHashMap<String, DaoForecastValue> semiResult = new LinkedHashMap<String, DaoForecastValue>();

        for(String code: codes){
            if(semiFollow.containsKey(Integer.parseInt(code))){
                semiResult.put(code, codesForecastsMap.get(code));
            }
        }

        return semiResult;
    }

    public LinkedHashMap<String, DaoForecastValue> compareAndCreateSemiMap (HashMap<Integer, String> listValues, LinkedHashMap<String, DaoForecastValue> elementValues ){

        LinkedHashMap<String, DaoForecastValue> result = new LinkedHashMap<String, DaoForecastValue>();

        Set<Integer> elementCodes = listValues.keySet();

        for(int elementCode: elementCodes ){

            if(elementValues.containsKey(""+elementCode))
                result.put(""+elementCode,elementValues.get(""+elementCode));

        }

        return result;
    }

}


