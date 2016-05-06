package org.fao.fenix.export.core.utils.adapter;


import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.DSDDocument;
import org.fao.fenix.commons.msd.dto.full.DSDGeographic;
import org.fao.fenix.commons.msd.dto.templates.identification.DSDCodelist;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.commons.utils.JSONUtils;

public class FenixAdapter {


    public  static RepresentationType getRepresentationType(String metadataField, JsonNode resourceNode) throws Exception {
        JsonNode metadataNode = resourceNode != null && resourceNode.get(metadataField) != null ? resourceNode.get(metadataField) : null;
        return getRepresentationType(metadataNode);
    }

    public static RepresentationType getRepresentationType(JsonNode metadataNode) throws Exception {


        String representationTypeLabel = metadataNode != null &&  metadataNode.get("meContent")!= null &&  metadataNode.get("meContent").get("resourceRepresentationType")!= null ? metadataNode.path("meContent").path("resourceRepresentationType").textValue() : null;
        return representationTypeLabel != null ? RepresentationType.valueOf(representationTypeLabel) : RepresentationType.dataset;
    }

    public static Resource decodeResource(String source, RepresentationType resourceType) throws Exception {
        switch (resourceType) {
            case codelist:
                return JSONUtils.decode(source, Resource.class, DSDCodelist.class, Code.class);
            case dataset:
                return JSONUtils.decode(source, Resource.class, DSDDataset.class, Object[].class);
            case geographic:
                return JSONUtils.decode(source, Resource.class, DSDGeographic.class, Object.class);
            case document:
                return JSONUtils.decode(source, Resource.class, DSDDocument.class, Object.class);
            default:
                return null;
        }
    }

}
