package org.fao.fenix.export.core.dto;

public enum CoreOutputType {

    xlsx("application/vnd.openxml"),
    xls("application/vnd.ms-excel"),
    pdf("application/pdf"),
    rtf("application/rtf"),
    zip("application/x-gzip");


    private String contentType;

    CoreOutputType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
