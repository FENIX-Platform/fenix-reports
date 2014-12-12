package org.fao.fenix.export.core.output.factory;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.core.output.plugin.Output2;
import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
import org.fao.fenix.export.core.utils.reader.PropertiesReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class OutputFactory {

    private static OutputFactory outputFactory;


    public static OutputFactory getInstance() throws Exception {

        if (outputFactory == null)
        {
            outputFactory = new OutputFactory();
        }

        return outputFactory;
    }


    private Map<String, Class<Output2>> pluginsClass = new HashMap<>();

    private OutputFactory() throws Exception {
        String inputPluginsURL = ConfiguratorURL.getInstance().getOutputProperties();
        Properties pluginsClassName = PropertiesReader.getInstance().getProperties(inputPluginsURL);

        for (Map.Entry<Object, Object> entry : pluginsClassName.entrySet())
            pluginsClass.put((String)entry.getKey(), (Class<Output2>)Class.forName((String)entry.getValue()));
    }




    //logic
    public Output2 getPlugin(PluginConfig config) throws Exception {
        Output2 plugin = pluginsClass.get(config.getPlugin()).newInstance();
        plugin.init(config.getConfig());
        return plugin;
    }


    // OLD--------------------

    private Output outputChosen;

    public Output init(JsonNode jsonNodeOutput){

        try {

            String key = jsonNodeOutput.path("plugin").asText();
            String outputPluginsURL = ConfiguratorURL.getInstance().getOutputProperties();
            String classOutputPlugin = PropertiesReader.getInstance().getPropertyValue(outputPluginsURL, key);

            Class outputClass = Class.forName(classOutputPlugin);
            outputClass.toString();
            outputChosen = (Output)outputClass.newInstance();
            outputChosen.init(jsonNodeOutput);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return outputChosen;


    }

    public Output getOutputChosen(){
        return this.outputChosen;
    }
}
