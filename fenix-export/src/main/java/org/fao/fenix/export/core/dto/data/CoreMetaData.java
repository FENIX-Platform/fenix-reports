package org.fao.fenix.export.core.dto.data;


import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.Metadata;

import java.util.Iterator;

public class CoreMetaData extends CoreData<Object[]> {

    private MeIdentification metadata;
    private Iterator<Object[]> data;
    private JsonNode mdsd;
    private Metadata metadataTemplate;


    public CoreMetaData() {
    }

    public CoreMetaData(MeIdentification metadata, Iterator<Object[]> data, JsonNode mdsd, Metadata metadataTemplate) {
        this.metadata = metadata;
        this.data = data;
        this.mdsd = mdsd;
        this.metadataTemplate = metadataTemplate;
    }

    public CoreMetaData(MeIdentification metadata, Iterator<Object[]> data) {
        this.metadata = metadata;
        this.data = data;
    }

    @Override
    public MeIdentification getMetadata() {
        return this.metadata;
    }

    @Override
    public Iterator<Object[]> getData() {
        return this.data;
    }

    public void setMetadata(MeIdentification metadata) {
        this.metadata = metadata;
    }

    public void setData(Iterator<Object[]> data) {
        this.data = data;
    }

    public JsonNode getMdsd() {
        return mdsd;
    }

    public void setMdsd(JsonNode mdsd) {
        this.mdsd = mdsd;
    }

    public Metadata getMetadataTemplate() {
        return metadataTemplate;
    }

    public void setMetadataTemplate(Metadata metadataTemplate) {
        this.metadataTemplate = metadataTemplate;
    }
}
