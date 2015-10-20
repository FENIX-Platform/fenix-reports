package org.fao.fenix.export.core.output.plugin;

import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.data.CoreData;

import java.io.OutputStream;
import java.util.Map;

public abstract class Output {

    public abstract void init(Map<String,Object> config);

    public abstract void process(CoreData resource) throws Exception;

    public abstract CoreOutputHeader getHeader() throws Exception;

    public abstract void write(OutputStream outputStream) throws Exception;

}
