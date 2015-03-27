package org.fao.fenix.export.plugins.output.md.layout;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.CMYKColor;

public enum FontType {

    title(new Font(Font.FontFamily.HELVETICA ,25, Font.NORMAL,
            new CMYKColor(0.9f, 0.7f, 0.4f, 0.1f))),
    firstLevel(new Font(Font.FontFamily.HELVETICA ,15, Font.BOLDITALIC,
            new CMYKColor(0.9f, 0.7f, 0.4f, 0.1f))),
    normal(new Font(Font.FontFamily.HELVETICA ,10, Font.NORMAL,
            new CMYKColor(0.9f, 0.7f, 0.4f, 0.1f)));


    private Font fontType;


    FontType(Font fontType) { this.fontType = fontType; }

    public Font getFontType() {
        return fontType;
    }


}
