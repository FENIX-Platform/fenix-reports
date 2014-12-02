package org.fao.fenix.export.core.input.plugin;

import org.codehaus.jackson.JsonNode;

import java.util.TreeMap;

/**
 * Created by fabrizio on 12/1/14.
 */
public interface Input {

    public void setConfigParameters(JsonNode inputNode, JsonNode dataNode, JsonNode metaDataNode, String name);

    public String getInputName();

    public TreeMap<?,?> getDataObject();



}
