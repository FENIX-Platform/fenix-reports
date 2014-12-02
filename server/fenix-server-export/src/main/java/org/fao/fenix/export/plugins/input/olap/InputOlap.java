package org.fao.fenix.export.plugins.input.olap;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.plugin.Input;

import java.util.TreeMap;

/**
 * Created by fabrizio on 12/1/14.
 */
public class InputOlap implements Input {

    private JsonNode dataNode;

    private String inputName;


    public void init(JsonNode dsdNode, JsonNode dataNode) {
        this.dataNode = dataNode;

    }

    @Override
    public void setConfigParameters(JsonNode inputNode, JsonNode dataNode, JsonNode metaDataNode, String key){

        this.inputName = key;
        // body blank for now



    }

    @Override
    public String getInputName() {
        return this.inputName;
    }

    @Override
    public TreeMap<?, ?> getDataObject() {
        return null;
    }


    public JsonNode getDataNode(){
        return this.dataNode;
    }
}
