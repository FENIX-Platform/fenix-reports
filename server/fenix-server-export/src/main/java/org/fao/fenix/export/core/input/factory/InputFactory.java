package org.fao.fenix.export.core.input.factory;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.commons.msd.dto.data.dataset.Resource;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.input.plugin.Input2;
import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
import org.fao.fenix.export.core.utils.reader.PropertiesReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fabrizio on 12/1/14.
 */
public class InputFactory {

    private static final Logger LOGGER = Logger.getLogger(InputFactory.class);



    private static InputFactory inputFactory;


    public static InputFactory getInstance() throws Exception {

        if (inputFactory == null)
        {
            inputFactory = new InputFactory();
        }

        return inputFactory;
    }

    private Map<String, Class<Input2>> pluginsClass = new HashMap<>();

    private InputFactory() throws Exception {
        String inputPluginsURL = ConfiguratorURL.getInstance().getInputProperties();
        Properties pluginsClassName = PropertiesReader.getInstance().getProperties(inputPluginsURL);

        for (Map.Entry<Object, Object> entry : pluginsClassName.entrySet())
            pluginsClass.put((String)entry.getKey(), (Class<Input2>)Class.forName((String)entry.getValue()));
    }




    //logic
    public Input2 getPlugin(PluginConfig config, Resource resource) throws Exception {
        LOGGER.warn("start");
        Input2 plugin = pluginsClass.get(config.getPlugin()).newInstance();
        LOGGER.warn("plugin created");

        plugin.init(config.getConfig(), resource);
        LOGGER.warn("plugin initialized");

        return plugin;
    }




















    private Input inputChosen;

    public Input init(JsonNode jsonNodeInput, JsonNode jsonNodeData, JsonNode jsonNodeMetadata){

        try {

            String key = jsonNodeInput.path("plugin").asText();

            String inputPluginsURL = ConfiguratorURL.getInstance().getInputProperties();
            String classInputPlugins = PropertiesReader.getInstance().getPropertyValue(inputPluginsURL, key);

            Class inputClass = Class.forName(classInputPlugins);
            inputChosen = (Input)inputClass.newInstance();
            inputChosen.setConfigParameters(jsonNodeInput, jsonNodeData, jsonNodeMetadata, key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return inputChosen;


    }

    public Input getInputChosen(){
        return this.inputChosen;
    }

}
