package org.fao.fenix.export.core.output.factory;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
import org.fao.fenix.export.core.utils.reader.PropertiesReader;

/**
 * Created by fabrizio on 12/1/14.
 */
public class OutputFactory {

    private static OutputFactory outputFactory;

    private OutputFactory(){}

    public static OutputFactory getInstance() {

        if (outputFactory == null)
        {
            outputFactory = new OutputFactory();
        }

        return outputFactory;
    }

    private Output outputChosen;

    public Output init(JsonNode jsonNodeOutput){

        try {

            String key = jsonNodeOutput.path("plugin").asText();
            String outputPluginsURL = ConfiguratorURL.getInstance().getInputProperties();
            String classOutputPlugin = PropertiesReader.getInstance().getPropertyValue(outputPluginsURL, key);

            Class outputClass = Class.forName(classOutputPlugin);
            outputClass.toString();
            outputChosen = (Output)outputClass.newInstance();
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

    public Output getInputChosen(){
        return this.outputChosen;
    }
}
