package org.fao.fenix.export.plugins.output.md.layout.utils;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

public enum MDFontTypes {

    titleField(FontFactory.getFont("roboto_light",9,ColorType.blueFenix.getCmykColor())),
    valueField((FontFactory.getFont(("roboto_light"),9,ColorType.grey.getCmykColor()))),
    descriptionField((FontFactory.getFont(("roboto_light"),5,ColorType.descGrey.getCmykColor()))),
    footerField((FontFactory.getFont(("roboto_light"), 7, ColorType.grey.getCmykColor()))),
    headerField((FontFactory.getFont(("roboto_thin"), 12, ColorType.blueFenix.getCmykColor()))),
    coverTitle((FontFactory.getFont(("roboto_thin"), 26, ColorType.descGrey.getCmykColor()))),
    coverDesc((FontFactory.getFont(("roboto_bold"), 16, ColorType.blueFenix.getCmykColor())));



    private Font fontType;

    MDFontTypes(Font fontType) { this.fontType = fontType;
    }

    public Font getFontType() {
        return fontType;
    }

}
