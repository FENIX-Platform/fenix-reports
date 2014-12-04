package org.fao.fenix.export.plugins.handlers.table.utils;

import org.codehaus.jackson.JsonNode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by fabrizio on 12/4/14.
 */
public class DatatypeFormatter {

    public double getRightNumberFormat(JsonNode column, String data, JsonNode format){

        String formatNumber = format.get("formatValue").asText();

        DecimalFormat df = new DecimalFormat(formatNumber);
        return Double.parseDouble(df.format(Double.parseDouble(data)));


    }

    public String getRightStringFormat(JsonNode column, String data, JsonNode format){
        return data;
    }

    public String getRightCodeFormat(JsonNode column, String data, JsonNode format){

        String result = null;
        JsonNode codes = column.get("domain").get("codes");

        boolean notFound  =true;

        for(int i=0; i<codes.size() && notFound; i++){

            if(data.equals(codes.get(i).get("code").get("code"))){

                // for now only in English
                result = codes.get(i).get("code").get("title").get("EN").asText();
                notFound = false;

            }

        }


        return result;
    }

    public String getRightLabelFormat(JsonNode column, String data, JsonNode format){
        return null;
    }

    public boolean getRightBooleanFormat(JsonNode column, String data, JsonNode format){
        return Boolean.parseBoolean(data);
    }

    public double getRightPercentageFormat(JsonNode column, String data, JsonNode format){
        return Double.parseDouble(data);
    }

    public SimpleDateFormat getRightDateFormat(JsonNode column, String data, JsonNode format){
        return null;
    }

    public SimpleDateFormat getRightYearFormat(JsonNode column, String data, JsonNode format){
        return null;
    }

    public SimpleDateFormat getRightMonthFormat(JsonNode column, String data, JsonNode format){
        return null;
    }

    public SimpleDateFormat getRightTimeFormat(JsonNode column, String data, JsonNode format){
        return null;
    }


}
