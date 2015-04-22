package org.fao.fenix.export.plugins.output.md.standard;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.log4j.Logger;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.full.StyleSheetCreator;
import org.fao.fenix.export.plugins.output.md.layout.factory.LayoutCreator;
import org.fao.fenix.export.plugins.output.md.layout.utils.ColorType;
import org.fao.fenix.export.plugins.output.md.layout.utils.MDFontTypes;
import org.fao.fenix.export.plugins.output.md.layout.utils.RegistrationFont;
import org.fao.fenix.export.plugins.output.md.layout.utils.SpecialBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

public class StandardLayoutCreator extends LayoutCreator {

    private static final Logger LOGGER = Logger.getLogger(StandardLayoutCreator.class);
    private StyleSheetCreator styleSheetCreator;
    private static float SIMPLE_HEIGHT_MARGIN = 19;
    private static int MARGIN_TO_ADD = 6;

    private static int SIMPLE_RIGHT_MARGIN = 0;
    private static String DATE_TYPEFIELD = Date.class.toString();
    private static String STRING_TYPEFIELD = String.class.toString();
    private static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    private static int[] COLSPAN_TABLE = new int[] {2,3};
    private Document document;
    private RegistrationFont registrationFont;
    private TreeMap<String, Object> modelData;


    public StandardLayoutCreator(Document document) throws DocumentException {
        this.document = document;
        // register all fenix fonts and styles
        registrationFont = new RegistrationFont();
        registrationFont.registerAll();
    }


    @Override
    public Document init(TreeMap<String, Object> modelData) throws DocumentException, IOException {

        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        createBody();
        return document;
    }

    private void createTitle(MDSDescriptor descriptor) throws DocumentException {
        document.add(new Paragraph(5, descriptor.getValue().toString(), MDFontTypes.valueField.getFontType()));
        document.add(styleSheetCreator.getSpaceParagraph("title"));
    }


    private void createBody() throws DocumentException {

        Set<String> keys = modelData.keySet();
        int indexChapter = 1;
        int margin = 0;
        for (String key : keys) {
            MDSDescriptor element = (MDSDescriptor) modelData.get(key);
            processDocumentBody(margin, element, key, indexChapter, modelData);
        }
    }


    private void processDocumentBody(int margin, MDSDescriptor element, String key, int indexChapter, TreeMap<String, Object> dataModel) throws DocumentException {


        boolean isBiggerHeaderMArgin = key.equals("1");

        if(element.getTitleBean().equals("disseminationPeriodicity")){
            System.out.println("msds");
        }

        System.out.println(element.getTitleBean());

        if(!SpecialBean.isSpecialBean(element.getTitleBean())) {

            if (isAStringObject(element.getValue()) && !element.getTitleBean().equals("title")) {
                writeSimpleElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
            } else if (isAnArrayObject(element.getValue())) {
                System.out.println("array!");
                ArrayList<Object> values = (ArrayList<Object>) element.getValue();
                writeArrayElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter, values);


            } else if (isARecursiveObject(element.getValue())) {
                System.out.println("recursive!");
                writeRecursiveElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
                TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

                Set<String> recKyes = recursiveData.keySet();
                for (String recKey : recKyes) {

                    MDSDescriptor elemRec = (MDSDescriptor) recursiveData.get(recKey);
                    processDocumentBody(margin + MARGIN_TO_ADD, elemRec, recKey, indexChapter, recursiveData);
                }
            }
        }else {
            System.out.println("special!");
            element.setValue(getStringFromSpecialBean(element));
            writeSimpleElement(margin,isBiggerHeaderMArgin,element,indexChapter);
        }

    }

    private boolean isAnArrayObject(Object object) {
        return object.getClass().toString().equals(ARRAY_TYPEFIELD);
    }

    private boolean isARecursiveObject(Object object) {
        return object.getClass().toString().equals(RECURSIVE_TYPEFIELD);
    }

    private boolean isAStringObject(Object object) {
        return object.getClass().toString().equals(STRING_TYPEFIELD) || object.getClass().toString().equals(DATE_TYPEFIELD);
    }


    private void writeSimpleElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        float marginApplied = (isBiggerHeaderMargin) ? SIMPLE_HEIGHT_MARGIN + 5 : SIMPLE_HEIGHT_MARGIN;

        PdfPTable table = new PdfPTable(2);
        table.setWidths(COLSPAN_TABLE);
        table.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase(value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);


        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        valueCell.addElement(new Phrase(value.getValue().toString(), MDFontTypes.valueField.getFontType()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setColspan(2);

        table.addCell(titleCell);
        table.addCell(valueCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);
    }

    private void writeRecursiveElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        String titleString = (value.getTitleToVisualize() != null) ? value.getTitleToVisualize().toString() : "title";

        Paragraph title = new Paragraph();
        title.add(new Phrase(titleString, MDFontTypes.titleField.getFontType()));
        title.setIndentationLeft(rightMargin);
        document.add(title);

    }


    private void writeArrayElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter, ArrayList<Object> values) throws DocumentException {

        int arraySize = values.size();

        boolean isUniqueValue = arraySize == 1;
        PdfPTable table = new PdfPTable(2);
        table.setWidths(COLSPAN_TABLE);
        table.setWidthPercentage(100);


        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase(value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);

        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        for (int i = 0; i < arraySize; i++) {
            String[] codeLabel = values.get(i).toString().split("-");

            PdfPCell[] cells = new PdfPCell[codeLabel.length];

            for (int z = 0; z < codeLabel.length; z++) {
                Phrase phrase = new Phrase(codeLabel[z], MDFontTypes.valueField.getFontType());
                valueCell.addElement(phrase);
            }

            if (i == arraySize - 1)
                table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
        }

        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(titleCell);
        table.addCell(valueCell);

        document.add(table);

        Paragraph blank = new Paragraph("", MDFontTypes.titleField.getFontType());
        blank.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
        document.add(blank);
    }

    private void setBorderCell(boolean isOnly, PdfPCell... cells) {

        if (isOnly) {
            for (PdfPCell cell : cells) {
                cell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                cell.setBorderColor(ColorType.borderGrey.getCmykColor());
            }
        } else {
            for (PdfPCell cell : cells) {
                cell.setBorder(Rectangle.BOTTOM);
                cell.setBorderColor(ColorType.borderGrey.getCmykColor());
            }
        }

    }


    private void createFooter() {
        //TODO

    }


    private String updateCounter(String counter) {

        String[] counterChunks = counter.split("\\.");
        int lengthSplit = counterChunks.length;

        String result = "";
        for (int i = 0; i < lengthSplit; i++) {
            if (i != lengthSplit - 1) {
                result += counterChunks[i] + ".";
            } else {
                result += Integer.parseInt(counterChunks[i]) + 1;
            }
        }

        return result;
    }


    private String getStringFromSpecialBean (MDSDescriptor element) {

        String result = null;
     /*   if(isAStringObject(element.getValue())) {
            result = element.getValue().toString().split("-")[1];
            return result;
        }*/
        if(isAnArrayObject(element.getValue())) {
            result = "";
            ArrayList<Object> values = (ArrayList<Object>) element.getValue();
            for(int i=0; i<values.size(); i++) {
                if (isAStringObject(values.get(i))) {
                    result+= values.get(i).toString().split("-")[1] ;
                    if(i<values.size()-1){
                        result+= ", ";
                    }
                } else {
                    if (result == null) {
                        result = getStringFromSpecialBean((MDSDescriptor) values.get(i));
                    }
                }
            }
            return result;
        }else if(isARecursiveObject(element.getValue())) {
            TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

            for(String key: recursiveData.keySet()) {
                if (result == null) {
                    result = getStringFromSpecialBean((MDSDescriptor) recursiveData.get(key));
                }
            }
        }
        return result;
    }


}



