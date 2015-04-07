package org.fao.fenix.export.plugins.output.md.layout.utils;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

public enum MDFontTypes {

    titleField(FontFactory.getFont("roboto_thin",10,ColorType.blueFenix.getCmykColor()/*, "Identity-H", BaseFont.EMBEDDED, 10, Font.UNDEFINED,  ColorType.blueFenix.getCmykColor()))*/)),
    valueField((FontFactory.getFont(("roboto_light"),9,ColorType.grey.getCmykColor()/*, "Identity-H", BaseFont.EMBEDDED, 9, Font.UNDEFINED,  ColorType.grey.getCmykColor()))*/))),
    descriptionField((FontFactory.getFont(("roboto_light"),8,ColorType.grey.getCmykColor()/*, "Identity-H", BaseFont.EMBEDDED, 8, Font.UNDEFINED,  ColorType.grey.getCmykColor()))*/)));


    private Font fontType;

    MDFontTypes(Font fontType) { this.fontType = fontType;
    }

    public Font getFontType() {
        return fontType;
    }

}
