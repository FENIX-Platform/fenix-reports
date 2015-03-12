package org.fao.fenix.export.plugins.input.metadata;


import org.fao.fenix.commons.msd.dto.data.codelist.MeIdentification;
import org.fao.fenix.commons.msd.dto.data.dataset.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.plugins.input.metadata.mediator.MDClientMediator;

import java.util.Map;

public class InputMD  extends Input {

    private static MDClientMediator mediator = new MDClientMediator();


    @Override
    public void init(Map<String, Object> config, Resource resource) {

        MeIdentification me = null;
        me = mediator.getMetadata("http://faostat3.fao.org/d3s2/v2/msd/resources/metadata/uid/iodioido?full=true");
        System.out.println(me);
    }

    @Override
    public CoreData getResource() {
        return null;
    }
}
