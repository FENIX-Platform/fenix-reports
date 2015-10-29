package org.fao.fenix.export.plugins.input.metadata;


import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.plugins.input.metadata.mediator.MDClientMediator;

import java.util.Map;

public class InputMD  extends Input {

    private static MDClientMediator mediator;
    private Resource resource;


    @Override
    public void init(Map<String, Object> config, Resource resource) {

        this.resource = resource;
        mediator = new MDClientMediator();
        try {
            resource.setMetadata(mediator.getMetadata(config));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CoreData getResource() {

        return new CoreTableData(resource.getMetadata(), resource.getData().iterator());
    }


}
