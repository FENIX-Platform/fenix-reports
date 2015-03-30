package org.fao.fenix.export.plugins.output.md.layout.utils;


import com.itextpdf.text.pdf.CMYKColor;

public enum ColorType {


    grey(new CMYKColor((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.80)),
    blueFenix( new CMYKColor(100,52, 0, 0));


    private CMYKColor cmykColor;


    ColorType(CMYKColor cmykColor) { this.cmykColor = cmykColor; }


    public CMYKColor getCmykColor() {
        return cmykColor;
    }

}
