package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.CMYKColor;

import java.util.HashMap;

public class StyleSheetCreator {


    private HashMap<FontType, Font> fontMap;
    private FontType fontType;


    public void init () {

        fontMap = new HashMap<>();
        fontMap.put(fontType.title,FontFactory.getFont(FontFactory.TIMES, 18, Font.BOLD, new CMYKColor(255, 0, 0, 64)));
        fontMap.put(fontType.normal, FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL));
    }


    public Font getFont (FontType font) {
        if(fontMap!= null)
            return fontMap.get(font);
        return null;
    }
}
