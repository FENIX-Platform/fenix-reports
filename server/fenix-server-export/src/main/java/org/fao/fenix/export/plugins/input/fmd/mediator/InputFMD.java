package org.fao.fenix.export.plugins.input.fmd.mediator;

import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreGenericData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.plugins.input.fmd.dataModel.FMDDataCreator;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class InputFMD extends Input {

    private static FMDClientMediator mediator;
    private Resource resource;
    private JsonNode questionBean;
    private JsonNode metadata;
    Map<String, Object> configInput ;

    @Override
    public void init(Map<String, Object> config, Resource resource) {

        this.configInput = config;
        this.resource = resource;
        configInput = config;
        mediator = new FMDClientMediator();
        try {
            questionBean = mediator.getParsedData(config.get("uid").toString());
            metadata = mediator.getParsedMetadata(String.valueOf(config.get("urlSchema")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CoreData getResource() {
        FMDDataCreator dataModelCreator  =new FMDDataCreator();
        try {
            dataModelCreator.initDataFromMDSD(metadata, questionBean,"EN");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return new CoreGenericData(dataModelCreator.getMetaDataCleaned());
    }

    public JsonNode getQuestionBean() {
        return questionBean;
    }
}
