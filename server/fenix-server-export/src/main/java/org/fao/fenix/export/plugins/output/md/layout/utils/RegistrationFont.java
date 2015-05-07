package org.fao.fenix.export.plugins.output.md.layout.utils;


import com.itextpdf.text.FontFactory;

public class RegistrationFont {

    private  boolean isRegistered = false;
    private  final String RELATIVE_PATH = this.getClass().getClassLoader().getResource("../").getPath();

    public RegistrationFont() {}


    private  boolean init () {

        System.out.println("ss");
        for(FenixFonts f: FenixFonts.values()) {
            FontFactory.register(RELATIVE_PATH + f.getUrl(), f.toString());
        }
        isRegistered = true;
        return isRegistered;
    }

    public boolean registerAll () {
        boolean result = (isRegistered)? isRegistered: init();
        return result;
    }

}
