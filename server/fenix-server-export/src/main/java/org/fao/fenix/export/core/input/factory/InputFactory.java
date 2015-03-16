package org.fao.fenix.export.core.input.factory;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.dataset.Resource;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
import org.fao.fenix.export.core.utils.reader.PropertiesReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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


    private Map<String, Class<Input>> pluginsClass = new HashMap<>();


    private InputFactory() throws Exception {
        String inputPluginsURL = ConfiguratorURL.getInstance().getInputProperties();
        Properties pluginsClassName = PropertiesReader.getInstance().getProperties(inputPluginsURL);

        for (Map.Entry<Object, Object> entry : pluginsClassName.entrySet())
            pluginsClass.put((String)entry.getKey(), (Class<Input>)Class.forName((String)entry.getValue()));
    }


    //logic
    public Input getPlugin(PluginConfig config, Resource resource) throws Exception {
        LOGGER.warn("start");
        Input plugin = pluginsClass.get(config.getPlugin()).newInstance();
        LOGGER.warn("plugin created");

        plugin.init(config.getConfig(), resource);
        LOGGER.warn("plugin initialized");

        return plugin;
    }


}
