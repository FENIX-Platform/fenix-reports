package org.fao.fenix.export.core.exportHandler.factory;

        import org.fao.fenix.export.core.exportHandler.communication.HandlerAdapter;
        import org.fao.fenix.export.core.input.plugin.Input;
        import org.fao.fenix.export.core.utils.configuration.ConfiguratorURL;
        import org.fao.fenix.export.core.utils.reader.PropertiesReader;


/**
 * Created by fabrizio on 12/4/14.
 */
public class HandlerFactory {

    private static HandlerFactory handlerFactory;

    private HandlerFactory() {
    }

    public static HandlerFactory getInstance() {

        if (handlerFactory == null) {
            handlerFactory = new HandlerFactory();
        }

        return handlerFactory;
    }

    private HandlerAdapter handler;

    public HandlerAdapter init(Input input) {

        try {

            String key = input.getInputName();
            String handlerPluginURL = ConfiguratorURL.getInstance().getHandlersProperties();
            String classHandlerPlugin = PropertiesReader.getInstance().getPropertyValue(handlerPluginURL, key);

            Class handlerClass = Class.forName(classHandlerPlugin);
            handler = (HandlerAdapter) handlerClass.newInstance();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return handler;


    }
}
