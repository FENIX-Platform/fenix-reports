package org.fao.fenix.export.core.controller;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.input.plugin.Input2;
import org.fao.fenix.export.core.output.factory.OutputFactory;
import org.fao.fenix.export.core.output.plugin.Output2;

import java.io.OutputStream;


public class GeneralController2 {

    private static final Logger LOGGER = Logger.getLogger(GeneralController2.class);


    private Input2 inputPlugin;
    private Output2 outputPlugin;

    //INIT
    public GeneralController2(CoreConfig config) throws Exception {
        LOGGER.warn("start creation of input and output plugins");
        LOGGER.warn("inputPlugin creation");
        inputPlugin = InputFactory.getInstance().getPlugin(config.getInput(), config.getResource());
        LOGGER.warn(config.getOutput());
        outputPlugin = OutputFactory.getInstance().getPlugin(config.getOutput());
    }


    //Business
    boolean processed = false;
    public void process() throws Exception {
        if (!processed) {
            outputPlugin.process(inputPlugin.getResource());
            processed = true;
        }
    }

    //Produce output data
    public void write(OutputStream outputStream) throws Exception {
        process();
        outputPlugin.write(outputStream);
    }

    //Utils
    public CoreOutputHeader getHeader() throws Exception {
        process();
        return outputPlugin.getHeader();
    }



}
