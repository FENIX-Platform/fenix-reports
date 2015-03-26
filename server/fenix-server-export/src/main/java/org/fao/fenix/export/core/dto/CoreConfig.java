package org.fao.fenix.export.core.dto;


import org.fao.fenix.commons.msd.dto.data.Resource;

public class CoreConfig {


    private Resource resource;
    private PluginConfig input;
    private PluginConfig output;


 /*   public CoreConfig(PluginConfig input, PluginConfig output, Resource resource) {
        this.input = input;
        this.output = output;
        this.resource = resource;
    }*/

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public PluginConfig getInput() {
        return input;
    }

    public void setInput(PluginConfig input) {
        this.input = input;
    }

    public PluginConfig getOutput() {
        return output;
    }

    public void setOutput(PluginConfig output) {
        this.output = output;
    }
}
