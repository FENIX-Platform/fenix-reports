package org.fao.fenix.export.core.output.factory;
import org.apache.log4j.Logger;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.output.plugin.Output;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class OutputFactory {
    private static final Logger LOGGER = Logger.getLogger(OutputFactory.class);
    @Inject private Instance<Output> outputInstance;

    public OutputFactory() {}

    //logic
    public Output getPlugin(PluginConfig config) throws Exception {
        Output plugin = getPlugin(config.getPlugin());
        if (plugin == null)
            throw new Exception("Output plugin not found");
        plugin.init(config.getConfig());
        LOGGER.warn("plugin output initialized");
        return plugin;
    }

    public Output getPlugin(String outputName) {
        if(outputName!= null)
            for (Iterator<Output> i = outputInstance.select().iterator(); i.hasNext(); ) {
                Output instance = i.next();
                if (instance.getClass().getAnnotation(org.fao.fenix.commons.utils.annotations.export.Output.class).value().equals(outputName))
                    return instance;
            }
        return null;
    }

}
