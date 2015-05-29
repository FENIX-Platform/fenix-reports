package org.fao.fenix.export.plugins.output.md.layout.factory;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.fao.fenix.export.plugins.output.md.full.FullLayoutCreator;
import org.fao.fenix.export.plugins.output.md.standard.StandardLayoutCreator;

import java.io.IOException;
import java.util.TreeMap;

public abstract class LayoutCreator {

    private static LayoutCreator layoutCreator;

    public static LayoutCreator createInstance(boolean isFull, Document document) throws DocumentException {

        if( isFull) {
            layoutCreator = new FullLayoutCreator(document);
        }else {
            layoutCreator = new StandardLayoutCreator(document);
        }
        return layoutCreator;

    }

    public abstract Document  init(TreeMap<String, Object> modelData, String title,PdfWriter writer) throws DocumentException, IOException;
}