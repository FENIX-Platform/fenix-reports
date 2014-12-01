package org.fao.fenix.export.core.utils.configuration;

/**
 * Created by fabrizio on 12/1/14.
 */
public class ConfiguratorURL {

    // Map between configuration and class names
    private static final String INPUT_PROPERTIES = "plugins/input/inputPlugins.properties";
    private static final String OUTPUT_PROPERTIES = "plugins/output/outputPlugins.properties";

    private static ConfiguratorURL configuratorURL;

    private ConfiguratorURL(){}

    public static ConfiguratorURL getInstance() {

        if (configuratorURL == null)
        {
            configuratorURL = new ConfiguratorURL();
        }

        return configuratorURL;
    }

    public static String getInputProperties() {
        return INPUT_PROPERTIES;
    }

    public static String getOutputProperties() {
        return OUTPUT_PROPERTIES;
    }
}
