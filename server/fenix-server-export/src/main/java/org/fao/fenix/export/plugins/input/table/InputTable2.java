package org.fao.fenix.export.plugins.input.table;

import org.fao.fenix.commons.msd.dto.data.dataset.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input2;

import java.util.Map;

public class InputTable2 extends Input2 {

    Resource resource;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        this.resource = resource;
    }

    @Override
    public CoreData getResource() {
        return new CoreTableData(resource.getMetadata(), resource.getData().iterator());
    }
}
