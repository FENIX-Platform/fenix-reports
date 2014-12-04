package org.fao.fenix.export.core.output.plugin;

import org.codehaus.jackson.JsonNode;

/**
 * Created by fabrizio on 12/1/14.
 */
public abstract class Output {

    private JsonNode output;

    public void init(JsonNode output){
        this.output = output;
    }

    public JsonNode getOutput() {
        return this.output;
    }


    public abstract String getExportFormat();
}
