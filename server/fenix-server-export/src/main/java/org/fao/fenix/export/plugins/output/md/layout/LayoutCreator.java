package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.*;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;

import java.util.Iterator;
import java.util.TreeMap;

public class LayoutCreator {

    private  StyleSheetCreator styleSheetCreator;
    private FontType fontType;

    private Document document;
    private TreeMap<String,Object> modelData ;

    public LayoutCreator (Document document){
        this.document = document;
    }


    public Document init (TreeMap<String,Object> modelData) throws DocumentException {
        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        styleSheetCreator.init();

        Iterator<String> dataIterator = modelData.keySet().iterator();

        int counter = 0;
        while(dataIterator.hasNext()) {
            MDSDescriptor temp = (MDSDescriptor) this.modelData.get(dataIterator.next());
            if (counter == 0) {
                createTitle(temp);
            }
        }


        return this.document;

    }

    private void createTitle (MDSDescriptor descriptor) throws DocumentException {
        Font f1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        f1.setColor(BaseColor.RED);
        String title = (descriptor.getTitleToVisualize()!= null)? descriptor.getTitleToVisualize(): descriptor.getTitleBean();
        Phrase phrase =new Phrase(title + ": "+ descriptor.getValue());
        phrase.setFont(f1);
        Paragraph paragraph = new Paragraph(phrase);

        paragraph.setFont(f1);

        document.add(paragraph);

    }


    private void createBody () {


    }

    private void createFooter () {

    }
}
