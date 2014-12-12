package org.fao.fenix.export.core.controller;

import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.CoreOutput;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.input.plugin.Input2;
import org.fao.fenix.export.core.output.factory.OutputFactory;
import org.fao.fenix.export.core.output.plugin.Output2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;



public class GeneralController2 {
    private Input2 inputPlugin;
    private Output2 outputPlugin;

    //INIT
    public GeneralController2(CoreConfig config) throws Exception {
        inputPlugin = InputFactory.getInstance().getPlugin(config.getInput(), config.getResource());
        outputPlugin = OutputFactory.getInstance().getPlugin(config.getOutput());
    }


    //Business
    CoreOutput output;
    public CoreOutput flow() throws Exception {
        if (output==null) {
            output = outputPlugin.getFile(inputPlugin.getResource());
        }
        return output;
    }

    //Produce output data
    public CoreOutput produce() throws Exception {
        return flow();
    }

    public void produce(OutputStream outputStream) throws Exception {
        copy(flow().getContent(), outputStream);
    }

    //Utils
    public CoreOutputHeader getHeader() throws Exception {
        return flow().getHeader();
    }




    private void copy(BufferedInputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        for (int c = input.read(buffer); c>0; c = input.read(buffer))
            output.write(buffer,0,c);
        input.close();
        output.close();
    }
}
