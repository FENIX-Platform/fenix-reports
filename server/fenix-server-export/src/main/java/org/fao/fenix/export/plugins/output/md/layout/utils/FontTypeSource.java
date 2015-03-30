package org.fao.fenix.export.plugins.output.md.layout.utils;

public enum FontTypeSource {


    robotoRegular("fonts/roboto/Roboto-Regular.ttf"),
    robotoLight("fonts/roboto/Roboto-Light.ttf");


    private String fontSource;

    FontTypeSource(String fontSource) {
        this.fontSource = fontSource;
    }

    public String getFontSource() {
        return fontSource;
    }
}
