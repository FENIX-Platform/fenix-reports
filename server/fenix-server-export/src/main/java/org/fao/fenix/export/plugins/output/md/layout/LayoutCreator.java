package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.*;
import com.itextpdf.text.List;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.log4j.Logger;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.layout.utils.ContentEvent;
import org.fao.fenix.export.plugins.output.md.layout.utils.IndexModel;
import org.fao.fenix.export.plugins.output.md.layout.utils.MDFontTypes;
import org.fao.fenix.export.plugins.output.md.layout.utils.RegistrationFont;

import java.io.IOException;
import java.util.*;

public class LayoutCreator {

    private static final Logger LOGGER = Logger.getLogger(LayoutCreator.class);
    private  StyleSheetCreator styleSheetCreator;
    private static float SIMPLE_HEIGHT_MARGIN = 15;
    private static int SIMPLE_RIGHT_MARGIN = 0;
    private  static String DATE_TYPEFIELD = Date.class.toString();
    private  static String STRING_TYPEFIELD = String.class.toString();
    private  static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private  static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    ArrayList<Chapter> chapterList;
    private Map<String,IndexModel> indexModelMap;
    private ContentEvent event;
    private Document document;
    private RegistrationFont registrationFont;
    private TreeMap<String,Object> modelData ;

    public LayoutCreator(Document document, ContentEvent event) throws DocumentException {
        this.document = document;
        this.event = event;
        // register all fenix fonts and styles
        registrationFont = new RegistrationFont();
        registrationFont.registerAll();
    }


    public Document init (TreeMap<String,Object> modelData) throws DocumentException, IOException {

        // register all f

        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        PdfPTable table = null;
        Chapter indexChapter = null;


       /* createIndexModel();

        makeIndex();*/
        createBody();



      //  Iterator<String> dataIterator = modelData.keySet().iterator();

        return this.document;

    }

    private void createTitle (MDSDescriptor descriptor) throws DocumentException {
        document.add(new Paragraph(5 , descriptor.getValue().toString(),  MDFontTypes.valueField.getFontType() ));
        document.add(styleSheetCreator.getSpaceParagraph("title"));
    }


    private void createBody () throws DocumentException {


        Set<String> keys = modelData.keySet();
        int indexChapter =1;
        int margin = 0;
        for(String key: keys) {

           MDSDescriptor element = (MDSDescriptor)modelData.get(key);
            if(element.getTitleBean().equals("uid")){
                System.out.println("stop!");
            }
            processDocumentBody(margin,element,key,indexChapter, modelData);
        }
    }


    private void processDocumentBody(int margin, MDSDescriptor element, String key, int indexChapter, TreeMap<String, Object> dataModel) throws DocumentException {


        boolean isBiggerHeaderMArgin = key.equals("1");

        if (isAStringObject(element.getValue())) {
           writeSimpleElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
        }else if(isAnArrayObject(element.getValue())){
            System.out.println("array!");
            ArrayList<Object> values = (ArrayList<Object>) element.getValue();
            writeArrayElement (margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter, values);


        }else if(isARecursiveObject(element.getValue())) {
            System.out.println("recursive!");
            writeRecursiveElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
            TreeMap<String,Object> recursiveData = (TreeMap<String,Object>)element.getValue();

            Set<String> recKyes = recursiveData.keySet();
            for(String recKey: recKyes) {
                MDSDescriptor elemRec = (MDSDescriptor)recursiveData.get(recKey);
                processDocumentBody(margin+5, elemRec,recKey,indexChapter, recursiveData);
            }
        }

    }

    private boolean isAnArrayObject (Object object) {
        return object.getClass().toString().equals(ARRAY_TYPEFIELD);
    }

    private boolean isARecursiveObject (Object object) {
        return object.getClass().toString().equals(RECURSIVE_TYPEFIELD);
    }

    private boolean isAStringObject (Object object) {
        return object.getClass().toString().equals(STRING_TYPEFIELD) || object.getClass().toString().equals(DATE_TYPEFIELD) ;
    }


    private void writeSimpleElement (int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter ) throws DocumentException {

        float marginApplied = (isBiggerHeaderMargin)?  SIMPLE_HEIGHT_MARGIN+5: SIMPLE_HEIGHT_MARGIN;

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase( value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());

        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT );
        titleCell.setPaddingLeft(rightMargin);

        PdfPCell valueCell  = new PdfPCell();
        valueCell.addElement(new Phrase(value.getValue().toString(), MDFontTypes.valueField.getFontType()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell descripCell = new PdfPCell();
        Phrase description = new Phrase(value.getDescription().toString());
        descripCell.addElement(new Phrase(value.getDescription().toString(), MDFontTypes.descriptionField.getFontType()));
        descripCell.setBorder(Rectangle.NO_BORDER);
        descripCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(titleCell);
        table.addCell(valueCell);
        table.addCell(descripCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);
    }

    private void writeRecursiveElement (int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter ) throws DocumentException {

        String titleString = (value.getTitleToVisualize() != null)? value.getTitleToVisualize().toString() : "title";
        String descString = (value.getDescription() != null)? value.getDescription().toString() : "Description";

        Paragraph title = new Paragraph();
        title.add( new Phrase(titleString, MDFontTypes.titleField.getFontType()));
        title.setIndentationLeft(rightMargin);

        Paragraph description = new Paragraph();
        description.add(new Phrase(descString, MDFontTypes.descriptionField.getFontType()));
        description.setIndentationLeft(rightMargin);

        description.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(title);
        document.add(description);



      /*  float marginApplied = (isBiggerHeaderMargin)?  SIMPLE_HEIGHT_MARGIN+5: SIMPLE_HEIGHT_MARGIN;

        PdfPTable table = new PdfPTable(3);

        PdfPCell titleCell = new PdfPCell();
        String titleString = (value.getTitleToVisualize() != null)? value.getTitleToVisualize().toString() : "title";
        String descString = (value.getDescription() != null)? value.getDescription().toString() : "Description";

        Phrase title = new Phrase(titleString, MDFontTypes.titleField.getFontType());

        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT );
        titleCell.setPaddingRight(rightMargin);


        PdfPCell valueCell  = new PdfPCell();
        valueCell.addElement(new Phrase("", MDFontTypes.valueField.getFontType()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell descripCell = new PdfPCell();
        descripCell.addElement(new Phrase(descString, MDFontTypes.descriptionField.getFontType()));
        descripCell.setBorder(Rectangle.NO_BORDER);
        descripCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(titleCell);
        table.addCell(valueCell);
        table.addCell(descripCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);*/
    }


    private void writeArrayElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter, ArrayList<Object> values) throws DocumentException {

        PdfPTable table = new PdfPTable(3);

        PdfPCell titleCell = new PdfPCell();
        String titleString = "";
        String descString = "";

        Phrase title = new Phrase(titleString, MDFontTypes.titleField.getFontType());

        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    //    titleCell.setPaddingRight(rightMargin);

        List valuesList = new List();
        for(int i= 0 ; i < values.size(); i++) {
            ListItem c = new ListItem(values.get(i).toString(), MDFontTypes.valueField.getFontType());
            valuesList.add(c);
        }


        PdfPCell valueCell  = new PdfPCell();
        valueCell.addElement(valuesList);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell descripCell = new PdfPCell();
        descripCell.addElement(new Phrase(descString, MDFontTypes.descriptionField.getFontType()));
        descripCell.setBorder(Rectangle.NO_BORDER);
        descripCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(titleCell);
        table.addCell(valueCell);
        table.addCell(descripCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);

    }



    private void createFooter () {

    }

    private void addStringTypeField (MDSDescriptor descriptor, int margin) throws DocumentException {
        String title = (descriptor.getTitleToVisualize()!= null)? descriptor.getTitleToVisualize(): descriptor.getTitleBean();
        String value = descriptor.getValue().toString();

        Phrase titlePhrase = new Phrase(12,title, MDFontTypes.valueField.getFontType());
        Phrase valuePhrase = new Phrase(12,value, MDFontTypes.valueField.getFontType());
        Paragraph p = new Paragraph(titlePhrase + "  :  ");
        p.add(valuePhrase);

        document.add(p);
    }


    private void addOnlyTitle (MDSDescriptor toAdd) throws DocumentException {

        String title = (toAdd.getTitleToVisualize()!= null)? toAdd.getTitleToVisualize(): toAdd.getTitleBean();
        document.add(new Paragraph(new Phrase(12,title, MDFontTypes.valueField.getFontType())));
        // then space (now I don't know how to do it :) )
    }

    private void createIndexOnFirstPage(MDSDescriptor temp) throws DocumentException {

        chapterList = new ArrayList<Chapter>();
        String[] titles = {"asd", "asd", "as", "@", "2", "23", "2", "42"};
        for (int i = 0; i < titles.length; i++) {
            Chunk chapTitle = new Chunk(titles[i] + " " + i);
            Chapter chapter = new Chapter(new Paragraph(chapTitle), i);
            chapTitle.setLocalDestination(chapter.getTitle().getContent());
            document.add(chapter);
            chapterList.add(chapter);
        }
    }

    private void makeIndex() throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        Paragraph paragraph = new Paragraph("Index", MDFontTypes.titleField.getFontType());

        Chapter indexChapter = new Chapter(paragraph,0);

        Set<String> keys = indexModelMap.keySet();

        for(String key : keys ){

            PdfPCell left = new PdfPCell(new Phrase(key, MDFontTypes.valueField.getFontType()));
            left.setBorder(Rectangle.NO_BORDER);

            String prhase =(indexModelMap.get(key).getTitle()!=null)? indexModelMap.get(key).getTitle(): "notTitle";
            Chunk pageno = new Chunk(prhase, MDFontTypes.valueField.getFontType());
            PdfPCell right = new PdfPCell(new Phrase(pageno));
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            right.setBorder(Rectangle.NO_BORDER);

            table.addCell(left);
            table.addCell(right);
        }
        indexChapter.add(table);
        document.add(indexChapter);
    }


    private  void createIndexModel () {
        indexModelMap = new HashMap<String, IndexModel>();

        Set<String> keys = modelData.keySet();

        int indexCounter = 0;

        for( String key: keys){

            indexCounter++;

            MDSDescriptor tmp = (MDSDescriptor) modelData.get(key);

            fillIndex(tmp, ""+indexCounter);


        }

    }


    private void fillIndex (MDSDescriptor tempDescriptor, String indexKey) {


        if(tempDescriptor.getValue().getClass().toString().equals(STRING_TYPEFIELD) || tempDescriptor.getValue().getClass().toString().equals(DATE_TYPEFIELD)){

            indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));

        }
         // discendents of the root
        else if(tempDescriptor.getValue().getClass().toString().equals(TreeMap.class.toString())){

            String indexUpd = indexKey + ".0";

            TreeMap<String, MDSDescriptor> tmpTreeMAp = (TreeMap<String,MDSDescriptor>)tempDescriptor.getValue();

            Set<String> keys = ((TreeMap<String,MDSDescriptor>)tempDescriptor.getValue()).keySet();
            indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));

            for(String key : keys) {

                indexUpd  = updateCounter(indexUpd);
                fillIndex(tmpTreeMAp.get(key),indexUpd);
            }
        }

        else if(tempDescriptor.getValue().getClass().toString().equals(ArrayList.class.toString())){

            if(tempDescriptor.getTitleBean().equals("OjCode")){

                indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));

            }else {

                String indexUpd = indexKey + ".0";
                TreeMap<String, MDSDescriptor> tempArray = ((TreeMap<String, MDSDescriptor>) ((ArrayList) tempDescriptor.getValue()).get(0));
                Set<String> arrayKeys = tempArray.keySet();

                for (String key : arrayKeys) {
                    indexUpd = updateCounter(indexUpd);
                    fillIndex(tempArray.get(key), indexUpd);
                }
            }


/*

            ArrayList<T> tmpTreeMAp =(ArrayList<T>)tempDescriptor.getValue();
            Class<T> persistentClass = (Class<T>)
                    ((ParameterizedType)getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0];


            LOGGER.error(persistentClass.getClass().toString()); */


        }


        LOGGER.error(tempDescriptor.getValue().getClass().toString());


    }

    private String updateCounter ( String counter) {

        String[] counterChunks = counter.split("\\.");
        int lengthSplit = counterChunks.length;

        String result = "";
        for(int i=0; i<lengthSplit; i++){
            if(i!= lengthSplit-1){
                result+= counterChunks[i]+".";
            }else{
                result+= Integer.parseInt(counterChunks[i])+ 1 ;
            }
        }

        return result;
    }
}




