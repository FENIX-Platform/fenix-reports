package org.fao.fenix.export.plugins.handlers.table.utils;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fabrizio on 12/4/14.
 */
public class DatatypeFormatter {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(DatatypeFormatter.class);

    public Double getRightNumberFormat(ObjectNode column, String data, ObjectNode format) {

        Double result = null;

        if (!data.equals(null)) {
            String formatNumber = format.get("formatValue").asText();

            DecimalFormat df = new DecimalFormat(formatNumber);
            result = Double.parseDouble(df.format(Double.parseDouble(data)));
        }
        return result;

    }

    public String getRightStringFormat(ObjectNode column, String data, ObjectNode format) {
        return data;
    }

    public String getRightCodeFormat(ObjectNode column, String data, ObjectNode format) {

        String result = null;
        if (!data.equals(null)) {
            JsonNode codes = column.get("domain").get("codes");

            boolean notFound = true;

            for (int i = 0; i < codes.size() && notFound; i++) {

                if (data.equals(codes.get(i).get("code").get("code").asText())) {

                    // for now only in English
                    result = codes.get(i).get("code").get("title").get("EN").asText();
                    notFound = false;

                }

            }
        }


        return result;
    }

    public String getRightLabelFormat(ObjectNode column, String data, ObjectNode format) {

        // TODO
        return null;
    }

    public Boolean getRightBooleanFormat(ObjectNode column, String data, ObjectNode format) {
        Boolean result = null;

        if (!data.equals(null)) {
            result = Boolean.parseBoolean(data);
        }

        return result;
    }

    public Double getRightPercentageFormat(ObjectNode column, String data, ObjectNode format) {
        return getRightNumberFormat(column, data, format);
    }

    public String getRightDateFormat(ObjectNode column, String data, ObjectNode format) {

        String result = null;

        if (!data.equals(null)) {

            String formatDateRequested = format.get("formatDate").asText();


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyyMMdd").parse(String.valueOf(data));
                String dateFormat;

                dateFormat = (!formatDateRequested.equals(null) && !formatDateRequested.equals(""))?
                       formatDateRequested: "dd/MM/yyyy";

                DateFormat rightDateFormat = new SimpleDateFormat(dateFormat);
                result = rightDateFormat.format(dateParsed);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    public String getRightYearFormat(ObjectNode column, String data, ObjectNode format) {
        String result = null;

        if (!data.equals(null)) {

            String formatDateRequested = format.get("formatDate").asText();


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyy").parse(String.valueOf(data));
                String dateFormat;

                dateFormat = (!formatDateRequested.equals(null) && !formatDateRequested.equals(""))?
                        formatDateRequested: "dd/MM/yyyy";

                DateFormat rightDateFormat = new SimpleDateFormat(dateFormat);
                result = rightDateFormat.format(dateParsed);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    public String getRightMonthFormat(ObjectNode column, String data, ObjectNode format) {
        String result = null;

        if (!data.equals(null)) {

            String formatDateRequested = format.get("formatDate").asText();


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyyMM").parse(String.valueOf(data));
                String dateFormat;

                dateFormat = (!formatDateRequested.equals(null) && !formatDateRequested.equals(""))?
                        formatDateRequested: "dd/MM/yyyy";

                DateFormat rightDateFormat = new SimpleDateFormat(dateFormat);
                result = rightDateFormat.format(dateParsed);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    public String getRightTimeFormat(ObjectNode column, String data, ObjectNode format) {
        String result = null;

        if (!data.equals(null)) {

            String formatDateRequested = format.get("formatDate").asText();


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(String.valueOf(data));
                String dateFormat;

                dateFormat = (!formatDateRequested.equals(null) && !formatDateRequested.equals(""))?
                        formatDateRequested: "dd/MM/yyyy";

                DateFormat rightDateFormat = new SimpleDateFormat(dateFormat);
                result = rightDateFormat.format(dateParsed);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }


}
