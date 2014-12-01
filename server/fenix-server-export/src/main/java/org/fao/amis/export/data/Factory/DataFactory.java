package org.fao.amis.export.data.Factory;



import org.fao.amis.export.data.configurations.dataCreator.DataCreator;
import org.fao.amis.export.data.forecast.Forecast;
import org.fao.amis.export.data.query.AMISQuery;

import java.io.IOException;

/**
 * Created by fabrizio on 11/10/14.
 */
public class DataFactory {

    private DataCreator fakeConstructor;

    private AMISQuery qvo;

    private Forecast forecasts;

    public DataFactory(Object[][] data, String season, String dataSource, String region){
        try {

            this.fakeConstructor = new DataCreator(data, season, dataSource, region);

            qvo = new AMISQuery();
            qvo.init();

            forecasts = new Forecast();
            forecasts.initData(this.fakeConstructor.getData());

            forecasts.createOrderedMaps(qvo.getFoodBalanceElements(), "national");
            forecasts.createOrderedMaps(qvo.getItyElements(), "international");
            forecasts.createOrderedMaps(qvo.getOtherElements(), "others");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public DataCreator getDataCreatorIstance(){
        return  this.fakeConstructor;

    }


    public AMISQuery getAMISQueryIstance() {
        return this.qvo;
    }


    public Forecast getForecastIstance() {
        return this.forecasts;
    }


}
