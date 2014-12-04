package org.fao.fenix.export.core.input.factory;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
import org.fao.fenix.export.core.utils.reader.PropertiesReader;

/**
 * Created by fabrizio on 12/1/14.
 */
public class InputFactory {


    private static InputFactory inputFactory;

    private InputFactory(){}

    public static InputFactory getInstance() {

        if (inputFactory == null)
        {
            inputFactory = new InputFactory();
        }

        return inputFactory;
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
