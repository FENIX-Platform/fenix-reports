package org.fao.fenix.export.plugins.input.fmd.dataModel.utils;

public class FMDescriptor {

    private String titleBean;
    private String titleToVisualize;
    private String description;
    private Object value;

    public FMDescriptor(String titleBean, String titleToVisualize, String description, Object value) {
        this.titleBean = titleBean;
        this.value = value;
        this.description = description;
        this.titleToVisualize = titleToVisualize;
    }

    public FMDescriptor(String titleBean, String titleToVisualize, String description){
        this.titleBean = titleBean;
        this.description = description;
        this.titleToVisualize = titleToVisualize;
    }


    public FMDescriptor(){};

    public Object getValue() {
        return value;
    }

    public FMDescriptor setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleToVisualize() {
        return titleToVisualize;
    }

    public void setTitleToVisualize(String titleToVisualize) {
        this.titleToVisualize = titleToVisualize;
    }

    public String getTitleBean() {
        return titleBean;
    }

    public void setTitleBean(String titleBean) {
        this.titleBean = titleBean;
    }
}
