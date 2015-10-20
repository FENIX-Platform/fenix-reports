package org.fao.fenix.export.core.dto;

public class CoreOutputHeader {

    private String name;
    private CoreOutputType type;
    private Integer size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoreOutputType getType() {
        return type;
    }

    public void setType(CoreOutputType type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
