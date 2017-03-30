package org.fao.fenix.export.core.controller;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.factory.OutputFactory;
import org.fao.fenix.export.core.output.plugin.Output;

import java.io.OutputStream;

public class GeneralController {

    private static final Logger LOGGER = Logger.getLogger(GeneralController.class);

    private Input inputPlugin;
    private Output outputPlugin;

    //INIT
    public GeneralController() {}
    public GeneralController(CoreConfig config) throws Exception {
        init(config);
    }
    public void init(CoreConfig config) throws Exception {
        inputPlugin = InputFactory.getInstance().getPlugin(config.getInput(), config.getResource());
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
