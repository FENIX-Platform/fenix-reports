package org.fao.fenix.export.plugins.output.table;


import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.export.core.dto.CoreOutput;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class OutputTableExcel extends Output2 {

    private static final Logger LOGGER = Logger.getLogger(OutputTableExcel.class);

    Map<String, Object> config;


    @Override
    public void init(Map<String, Object> config) {this.config = config;}

    @Override
    public CoreOutput getFile(CoreData resource) {
        LOGGER.warn("start file!!");
        LOGGER.warn("medata");
        Collection columns =((DSDDataset)resource.getMetadata().getDsd()).getColumns();


        BufferedInputStream buff;
        String a = "asd";
        try {
            buff = new BufferedInputStream(new FileInputStream(a));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        return null; //TODO
    }

    private void createExcel(BufferedInputStream buff, Collection collection, Iterator data){
        while(data.hasNext()){



        }





    }
}
