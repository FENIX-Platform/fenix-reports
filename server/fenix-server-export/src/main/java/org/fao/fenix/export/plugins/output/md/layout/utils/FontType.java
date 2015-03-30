package org.fao.fenix.export.plugins.output.md.layout.utils;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;

public enum FontType {

    title(new Font(Font.FontFamily.HELVETICA ,25, Font.NORMAL,
            new CMYKColor(0.9f, 0.7f, 0.4f, 0.1f))),
    titleField(FontFactory.getFont(("fonts/roboto/Roboto-Light.ttf"), "Identity-H", BaseFont.EMBEDDED, 15, Font.UNDEFINED,  ColorType.blueFenix.getCmykColor())),
    valueField(FontFactory.getFont(("fonts/roboto/Roboto-Regular.ttf"), "Identity-H", BaseFont.EMBEDDED, 12, Font.UNDEFINED, ColorType.grey.getCmykColor())),
    descriptionField(FontFactory.getFont(("fonts/roboto/Roboto-Regular.ttf"), "Identity-H", BaseFont.EMBEDDED, 10, Font.UNDEFINED, ColorType.grey.getCmykColor()));



    private Font fontType;


    FontType(Font fontType) { this.fontType = fontType; }

    public Font getFontType() {
        return fontType;
    }


}
