package org.fao.fenix.export.core.input.factory;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.input.plugin.Input;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class InputFactory {
    private static final Logger LOGGER = Logger.getLogger(InputFactory.class);
    @Inject private Instance<Input> inputInstance;

    public InputFactory() {}

    //logic
    public Input getPlugin(PluginConfig config, Resource resource) throws Exception {
        Input plugin = getPlugin(config.getPlugin());
        if(plugin==null)
            throw new Exception("Input plugin not found");
        plugin.init(config.getConfig(), resource);
        LOGGER.warn("plugin input initialized");
        return plugin;
    }

    public Input getPlugin(String inputName) {
        if(inputName!= null)
            for (Iterator<Input> i = inputInstance.select().iterator(); i.hasNext(); ) {
                Input instance = i.next();
                if (instance.getClass().getAnnotation(org.fao.fenix.commons.utils.annotations.export.Input.class).value().equals(inputName))
                    return instance;
            }
        return null;
    }

}
