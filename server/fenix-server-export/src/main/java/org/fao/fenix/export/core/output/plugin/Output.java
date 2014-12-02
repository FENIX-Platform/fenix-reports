package org.fao.fenix.export.core.output.plugin;

import org.codehaus.jackson.JsonNode;

/**
 * Created by fabrizio on 12/1/14.
 */
public interface Output {

    public void init(JsonNode output);

    public String getExportFormat();
}
