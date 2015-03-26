package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.*;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class LayoutCreator {

    private  StyleSheetCreator styleSheetCreator;
    private FontType fontType;
    private final static String DATE_TYPEFIELD = Date.class.toString();
    private final static String STRING_TYPEFIELD = String.class.toString();
    private final static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private final static String ARRAY_TYPEFIELD = ArrayList.class.toString();



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
                createBody(temp,0);
            }else{
                int initMargin = 0;

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
        document.add(new Paragraph(""));


    }


    private void createBody (MDSDescriptor descriptor, int initMargin) {

        System.out.println(descriptor.getValue().getClass().toString());

    }

    private void createFooter () {

    }
}
