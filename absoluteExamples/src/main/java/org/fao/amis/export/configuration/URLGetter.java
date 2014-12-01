package org.fao.amis.export.configuration;

/**
 * Created by fabrizio on 11/13/14.
 */
public class URLGetter {

    // CodeList
    private static final String commodityProperties = "export/amisLists/commodityList/commodity.properties";
    private static final String codeListProperties = "export/amisLists/elementsList/codelist.properties";
    private static final String forecastProperties = "export/component/forecastDSD/forecast.properties";

    // Formulas
    private static final String formulaNational = "export/component/formulas/formulaNational.properties";
    private static final String formulaOthers = "export/component/formulas/formulaOthers.properties";
    private static final String[] operandsFormulaNational = {"27","19","20","999"};
    private static final String[] operandsFormulaOthers = {"4"};

    // Semantic division
    private static final String internationalProperties = "export/component/semanticDivision/international.properties";
    private static final String nationalProperties = "export/component/semanticDivision/national.properties";
    private static final String othersProperties = "export/component/semanticDivision/other.properties";

    // Commodity Division
    private static final int[] maizeAndWheatCodes ={19,18,5,7,27,999,35,20,13,14,15,34,28,29,30,31,32,33,8 ,10, 16 ,1,2,4};
    private static final int[] riceCodes ={19,18,998,5,7,27,999,35,20,13,14,15,34,28,8,10, 16,1,2,4,3 };
    private static final int[] soybeanCodes ={19,18,5,7,27,999,35,20,36,31,13,14, 15, 10, 8,16, 21,34,28,1,2,4 };
    private static final int[] commodityCodes = {5,4,6,1};


    public static String getCommodityProperties() {
        return commodityProperties;
    }

    public static String getCodeListProperties() {
        return codeListProperties;
    }

    public static String getForecastProperties() {
        return forecastProperties;
    }

    public static String getFormulaNational() {
        return formulaNational;
    }

    public static String getFormulaOthers() {
        return formulaOthers;
    }

    public static String getInternationalProperties() {
        return internationalProperties;
    }

    public static String getNationalProperties() {
        return nationalProperties;
    }

    public static String getOthersProperties() {
        return othersProperties;
    }

    public static String[] getOperandsFormulaNational() {
        return operandsFormulaNational;
    }

    public static String[] getOperandsFormulaOthers() {
        return operandsFormulaOthers;
    }

    public static int[] getMaizeAndWheatCodes() {
        return maizeAndWheatCodes;
    }

    public static int[] getRiceCodes() {
        return riceCodes;
    }

    public static int[] getSoybeanCodes() {
        return soybeanCodes;
    }

    public static int[] getCommodityCodes() {
        return commodityCodes;
    }
}
