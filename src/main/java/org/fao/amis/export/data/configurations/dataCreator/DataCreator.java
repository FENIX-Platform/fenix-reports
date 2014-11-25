package org.fao.amis.export.data.configurations.dataCreator;

/**
 * Created by fabrizio on 11/5/14.
 */


public class DataCreator {

    private Object[][] data;

    private String datasource, season, country;

    public DataCreator(Object[][] dataTrue, String season, String dataSource, String region) {
        this.data = dataTrue;
        this.datasource = dataSource;
        this.season = season;
        this.country = region;
    }


    public Object[][] getData() {
        return this.data;
    }


    public String getDatasource() {
        return datasource;
    }

    public String getSeason() {
        return season;
    }

    public String getCountry() {
        return country;
    }
}