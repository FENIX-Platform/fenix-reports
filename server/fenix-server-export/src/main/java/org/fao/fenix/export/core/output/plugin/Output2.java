package org.fao.fenix.export.core.output.plugin;

import org.fao.fenix.export.core.dto.CoreOutput;
import org.fao.fenix.export.core.dto.data.CoreData;

import java.util.Map;

public abstract class Output2 {

    public abstract void init(Map<String,Object> config);
    public abstract CoreOutput getFile(CoreData resource);


}
