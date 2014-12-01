package org.fao.fenix.export.plugins.input.olap;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.plugin.Input;

import java.util.TreeMap;

/**
 * Created by fabrizio on 12/1/14.
 */
public class InputOlap implements Input {

    JsonNode dataNode;

    @Override
    public void init(JsonNode dsdNode, JsonNode dataNode) {
        this.dataNode = dataNode;

    }

    @Override
    public void setConfigParameters(JsonNode input) {

        // body blank for now

    }

    @Override
    public TreeMap<?, ?> getDataObject() {
        return null;
    }


    public JsonNode getDataNode(){
        return this.dataNode;
    }
}
