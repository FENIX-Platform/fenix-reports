package org.fao.fenix.export.plugins.output.md.layout.utils;


public enum FenixFonts {

    roboto_normal("fonts/roboto/Roboto-Regular.ttf"),
    roboto_light("fonts/roboto/Roboto-Light.ttf"),
    roboto_bold("fonts/roboto/Roboto-Bold.ttf"),
    roboto_thin("fonts/roboto/Roboto-Thin.ttf"),
    roboto_thin2("fonts/roboto/Roboto-ThinItalic.ttf");


    private String url;

    FenixFonts(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
