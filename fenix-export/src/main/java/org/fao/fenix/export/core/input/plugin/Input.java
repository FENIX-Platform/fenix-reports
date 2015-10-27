package org.fao.fenix.export.core.input.plugin;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;

import java.util.Map;

public abstract class Input {

    public abstract void init(Map<String,Object> config, Resource resource);
    public abstract CoreData getResource();

}
