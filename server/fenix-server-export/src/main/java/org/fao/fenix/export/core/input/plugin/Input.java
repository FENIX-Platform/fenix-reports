package org.fao.fenix.export.core.input.plugin;

import org.codehaus.jackson.JsonNode;

import java.util.TreeMap;

/**
 * Created by fabrizio on 12/1/14.
 */
public abstract class Input {

    private JsonNode inputNode, dataNode, metadataNode;

    private String name;

    public void setConfigParameters(JsonNode inputNode, JsonNode dataNode, JsonNode metaDataNode, String name){
        this.name = name;
        this.inputNode = inputNode;
        this.dataNode  =dataNode;
        this.metadataNode = metaDataNode;
    }

    public String getInputName(){
        return this.name;
    }

    public JsonNode getInputNode(){
        return this.inputNode;
    }

    public JsonNode getDataNode(){
        return this.dataNode;
    }

    public JsonNode getMetadataNode(){
        return  this.metadataNode;
    }

    public  abstract TreeMap<?, ?> getDataObject() ;

}
