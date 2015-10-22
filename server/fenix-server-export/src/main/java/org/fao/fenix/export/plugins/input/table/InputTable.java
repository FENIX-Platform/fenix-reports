package org.fao.fenix.export.plugins.input.table;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.plugins.input.table.bridge.D3SBridge;

import java.util.Map;

public class InputTable extends Input {

    Resource resource;
    Map<String, Object> config;
    D3SBridge bridge;

    @Override
    public void init(Map<String, Object> config, Resource resource) {
        this.bridge = new D3SBridge();

        this.config = config;

        this.resource = this.bridge.getData(this.config);

    }

    @Override
    public CoreData getResource() {
        return new CoreTableData(resource.getMetadata(), resource.getData().iterator());
    }
}
