package org.fao.fenix.export.plugins.output.md.standard;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
    private final float LOGO_HEIGHT = 60;
    private final float LOGO_WIDTH = 90;
    private StyleSheetCreator styleSheetCreator;
    private static float SIMPLE_HEIGHT_MARGIN = 19;
    private static float SIMPLE_HEIGHT_MARGIN_PARAGRAPH = 5;
    private final float OFFSET_RIGHT_TITLE = 100;
    private final float LOGO_TOP_OFFSET = 60;

    private static int MARGIN_TO_ADD = 6;
    private static final String IMG_PATH = "logo/newLogos/FAO_logo_Azzurro.png";
    private static String DESCRIPTION_COVER = "METADATA OVERVIEW";
    private static float SEPARATOR_WIDTH = (float) 0.71;
    private static int SIMPLE_RIGHT_MARGIN = 0;
    private static String DATE_TYPEFIELD = Date.class.toString();
    private static String STRING_TYPEFIELD = String.class.toString();
    private static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    private static int[] COLSPAN_TABLE = new int[]{2, 3};
    private Document document;
    private RegistrationFont registrationFont;
    private TreeMap<String, Object> modelData;


    public StandardLayoutCreator(Document document) throws DocumentException {
        this.document = document;
        // register all fenix fonts and styles
        registrationFont = new RegistrationFont();
        registrationFont.registerAll();
    }


    public void createCover(String title, PdfWriter writer) throws DocumentException, IOException {


        Paragraph titleLAbel = new Paragraph(title, MDFontTypes.coverTitle.getFontType());

        Image logo = Image.getInstance(this.getClass().getClassLoader().getResource("../").getPath() + IMG_PATH);

        Rectangle rect = writer.getBoxSize("art");

        PdfContentByte cb = writer.getDirectContent();
        cb.setColorStroke(ColorType.borderGrey.getCmykColor());
        cb.setLineWidth(SEPARATOR_WIDTH);


        cb.moveTo(((rect.getLeft() + rect.getRight()) / 2) - OFFSET_RIGHT_TITLE, (rect.getTop() / 2) + 55);
        cb.lineTo(rect.getRight() - 15, (rect.getTop() / 2) + 55);
        cb.stroke();


        logo.scaleToFit(LOGO_HEIGHT, LOGO_WIDTH);
        logo.setAbsolutePosition(((rect.getLeft() + rect.getRight()) / 2) - OFFSET_RIGHT_TITLE - 8, (rect.getTop() / 2) + LOGO_TOP_OFFSET);
        document.add(logo);

        titleLAbel.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT_TITLE + 50));
        titleLAbel.setSpacingBefore((rect.getTop() / 2) - 72);
        titleLAbel.setSpacingAfter(20);

        titleLAbel.setAlignment(Element.ALIGN_LEFT);
        document.add(titleLAbel);

        Chunk CONNECT = new Chunk(new LineSeparator(SEPARATOR_WIDTH, 100, ColorType.borderGrey.getCmykColor(), Element.ALIGN_CENTER, 3.5f));

        Paragraph separator = new Paragraph(CONNECT);
        separator.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT_TITLE + 50));
        document.add(separator);

        Paragraph description = new Paragraph(DESCRIPTION_COVER, MDFontTypes.coverDesc.getFontType());
        description.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT_TITLE + 50));
        description.setSpacingAfter(10);
        document.add(description);

        document.newPage();
    }


    @Override
    public Document init(TreeMap<String, Object> modelData, String title, PdfWriter writer) throws DocumentException, IOException {
        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        createCover(title, writer);
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

        if (!SpecialBean.isSpecialBean(element.getTitleBean())) {

            if (isAStringObject(element.getValue()) && !element.getTitleBean().equals("title") && !element.getValue().toString().equals("")) {
                // simple case
                writeSimpleElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
            } else if (isAnArrayObject(element.getValue())) {
                // array case
                ArrayList<Object> values = (ArrayList<Object>) element.getValue();
                writeArrayElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter, values);


            } else if (isARecursiveObject(element.getValue())) {
                // recursive case
                writeRecursiveElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
                TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

                Set<String> recKyes = recursiveData.keySet();
                for (String recKey : recKyes) {

                    MDSDescriptor elemRec = (MDSDescriptor) recursiveData.get(recKey);
                    processDocumentBody(margin + MARGIN_TO_ADD, elemRec, recKey, indexChapter, recursiveData);
                }
            }
        } else {
            // special bean case
            element.setValue(getStringFromSpecialBean(element));
            if (element.getValue() != null) {
                writeSimpleElement(margin, isBiggerHeaderMArgin, element, indexChapter);
            }
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
        Paragraph title = new Paragraph(value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);


        System.out.println(value.getTitleToVisualize().toString());
        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        valueCell.addElement(new Phrase(value.getValue().toString(), MDFontTypes.valueField.getFontType()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setColspan(2);

        setRightHeightOfCells(titleCell, valueCell, value);

        table.addCell(titleCell);
        table.addCell(valueCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);
    }


    private void setRightHeightOfCells(PdfPCell titleCell, PdfPCell valueCell, MDSDescriptor element) {

        int words = countCharNumber(element.getValue().toString());
        if (words > 60) {
            titleCell.setVerticalAlignment(Element.ALIGN_TOP);
        }
    }

    private int countCharNumber(String word) {
        int counter = 0;
        if (word != null && !word.equals("")) {
            char[] arr = word.toCharArray();
            counter = arr.length;
        }
        return counter;
    }

    private void writeRecursiveElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        String titleString = (value.getTitleToVisualize() != null) ? value.getTitleToVisualize().toString() : "title";

        Paragraph title = new Paragraph();
        Phrase phrase = new Phrase(titleString, MDFontTypes.titleField.getFontType());
        title.add(phrase);
        title.setIndentationLeft(rightMargin);
        if (isAllUppercase(titleString)) {
            LineSeparator UNDERLINE =
                    new LineSeparator(SEPARATOR_WIDTH, 100, ColorType.borderGrey.getCmykColor(), Element.ALIGN_CENTER, -2);
            title.add(UNDERLINE);
        }
        title.setSpacingAfter(SIMPLE_HEIGHT_MARGIN_PARAGRAPH);
        document.add(title);
    }


    private boolean isAllUppercase(String s) {
        if (s != null) {
            for (char c : s.toCharArray()) {
                if (!Character.isUpperCase(c) && !Character.isWhitespace(c))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }


    private void writeArrayElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter, ArrayList<Object> values) throws DocumentException {

        int arraySize = values.size();

        boolean isUniqueValue = arraySize == 1;
        PdfPTable table = new PdfPTable(2);
        table.setWidths(COLSPAN_TABLE);
        table.setWidthPercentage(100);


        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase(value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());
        titleCell.setVerticalAlignment(Element.ALIGN_TOP);
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
                String toAdd = (i != arraySize - 1) ? " , " : " ";
                Phrase phrase = new Phrase(codeLabel[z] + toAdd, MDFontTypes.valueField.getFontType());
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


    private String getStringFromSpecialBean(MDSDescriptor element) {

        String result = null;

        if (isAnArrayObject(element.getValue())) {
            result = "";
            ArrayList<Object> values = (ArrayList<Object>) element.getValue();
            for (int i = 0; i < values.size(); i++) {
                if (isAStringObject(values.get(i))) {
                    result += values.get(i).toString().split("-")[1];
                    if (i < values.size() - 1) {
                        result += ", ";
                    }
                } else {
                    if (result == null) {
                        result = getStringFromSpecialBean((MDSDescriptor) values.get(i));
                    }
                }
            }
            return result;
        } else if (isARecursiveObject(element.getValue())) {
            TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

            for (String key : recursiveData.keySet()) {
                if (result == null) {
                    result = getStringFromSpecialBean((MDSDescriptor) recursiveData.get(key));
                }
            }
        }
        return result;
    }


}



