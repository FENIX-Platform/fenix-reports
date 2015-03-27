package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.layout.utils.ContentEvent;

import java.util.*;

public class LayoutCreator {

    private  StyleSheetCreator styleSheetCreator;
    private FontType fontType;
    private  static String DATE_TYPEFIELD = Date.class.toString();
    private  static String STRING_TYPEFIELD = String.class.toString();
    private  static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private  static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    ArrayList<Chapter> chapterList;



    private ContentEvent event;
    private Document document;
    private TreeMap<String,Object> modelData ;

    public LayoutCreator(Document document, ContentEvent event) throws DocumentException {
        this.document = document;
        this.event = event;
    }


    public Document init (TreeMap<String,Object> modelData) throws DocumentException {
        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        PdfPTable table = null;
        Chapter indexChapter = null;

        Iterator<String> dataIterator = modelData.keySet().iterator();

        int counter=0, initMargin ;
        while(dataIterator.hasNext()) {
            MDSDescriptor temp = (MDSDescriptor) this.modelData.get(dataIterator.next());

            initMargin = 0;
            if (counter == 0) {
                createTitle(temp);
                indexChapter = new Chapter("Index", -1);
                indexChapter.setNumberDepth(-1); // not show number style
                table = new PdfPTable(2);



            }else{
              //createBody(temp,initMargin);

                indexAfter(temp,table, indexChapter);
            }
            counter++;
        }
        return this.document;

    }

    private void createTitle (MDSDescriptor descriptor) throws DocumentException {
        document.add(new Paragraph(5 , descriptor.getValue().toString(),  FontType.title.getFontType() ));
        document.add(styleSheetCreator.getSpaceParagraph("title"));
    }

/*

    private void createBody (Object descriptor, int initMargin) throws DocumentException {

        String type = descriptor.getValue().getClass().toString();


        if (type.equals(STRING_TYPEFIELD) || type.equals(DATE_TYPEFIELD)) {
            addStringTypeField(descriptor, initMargin);
        }

        else if(type.equals(ARRAY_TYPEFIELD)) {

            System.out.println("array!");

            initMargin++;


            addOnlyTitle(descriptor);
            ArrayList<TreeMap> tmpList = (ArrayList<TreeMap>) descriptor.getValue();
            for (int i = 0; i < tmpList.size(); i++) {

                TreeMap<String, MDSDescriptor> tmpObjects = tmpList.get(i);
                Iterator<String> it = tmpObjects.keySet().iterator();
                while (it.hasNext()) {
                    createBody(tmpObjects.get(it.next()), initMargin);
                }
            }

           */
/* for(int i =0; i< ((ArrayList<MDSDescriptor>)(descriptor.getValue())).size(); i++){
                document.add(new Paragraph(""));
                createBody(((ArrayList<MDSDescriptor>) (descriptor.getValue())).get(i), i);
            }*//*


        }else if(type.equals(RECURSIVE_TYPEFIELD)){

            addOnlyTitle(descriptor);

            TreeMap<String, Object> tmp = (TreeMap)descriptor.getValue();

            Iterator<String> it = tmp.keySet().iterator();

            while(it.hasNext()){
                createBody((MDSDescriptor) tmp.get(it.next()), initMargin++);
            }


         */
/*   createBody((MDSDescriptor)descriptor.getValue(), initMargin++);*//*


        }


    }
*/



    private void createFooter () {

    }

    private void addStringTypeField (MDSDescriptor descriptor, int margin) throws DocumentException {
        String title = (descriptor.getTitleToVisualize()!= null)? descriptor.getTitleToVisualize(): descriptor.getTitleBean();
        String value = descriptor.getValue().toString();

        Phrase titlePhrase = new Phrase(12,title, FontType.normal.getFontType());
        Phrase valuePhrase = new Phrase(12,value, FontType.normal.getFontType());
        Paragraph p = new Paragraph(titlePhrase + "  :  ");
        p.add(valuePhrase);

        document.add(p);
    }


    private void addOnlyTitle (MDSDescriptor toAdd) throws DocumentException {

        String title = (toAdd.getTitleToVisualize()!= null)? toAdd.getTitleToVisualize(): toAdd.getTitleBean();
        document.add(new Paragraph(new Phrase(12,title,FontType.normal.getFontType())));
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

    private void indexAfter(MDSDescriptor mdsDescriptor, PdfPTable table, Chapter indexChapter) throws DocumentException {


            PdfPCell left = new PdfPCell(new Phrase("X"));
            left.setBorder(Rectangle.NO_BORDER);

            Chunk pageno = new Chunk(mdsDescriptor.getTitleToVisualize());
            PdfPCell right = new PdfPCell(new Phrase(pageno));
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            right.setBorder(Rectangle.NO_BORDER);

            table.addCell(left);
            table.addCell(right);

            indexChapter.add(table);
            document.add(indexChapter);
            // add content chapter

        }
    }



