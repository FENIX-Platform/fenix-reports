package org.fao.fenix.export.core.input.plugin;

import org.codehaus.jackson.JsonNode;

import java.util.TreeMap;

/**
 * Created by fabrizio on 12/1/14.
 */
public interface Input {

    public void init(JsonNode dsdNode, JsonNode dataNode);

    public void setConfigParameters(JsonNode input);

    public TreeMap<?,?> getDataObject();



}
