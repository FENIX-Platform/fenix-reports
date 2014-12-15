package org.fao.fenix.export.core.dto;

import java.io.BufferedInputStream;

public class CoreOutput {

    private CoreOutputHeader header;
    private BufferedInputStream content;

    public CoreOutputHeader getHeader() {
        return header;
    }

    public void setHeader(CoreOutputHeader header) {
        this.header = header;
    }

    public BufferedInputStream getContent() {
        return content;
    }

    public void setContent(BufferedInputStream content) {
        this.content = content;
    }
}
