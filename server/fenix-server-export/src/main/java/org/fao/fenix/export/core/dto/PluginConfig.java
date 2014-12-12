package org.fao.fenix.export.core.dto;

import java.util.Map;

public class PluginConfig {

    private String plugin;
    private Map<String,Object> config;


    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
