package org.fao.fenix.export.core.dto;

public enum CoreOutputType {

    excel ("application/excel"); //TODO find right content type

    private String contentType;

    CoreOutputType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
