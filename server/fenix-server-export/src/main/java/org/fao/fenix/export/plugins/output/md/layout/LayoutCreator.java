package org.fao.fenix.export.plugins.output.md.layout;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.log4j.Logger;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.layout.utils.ContentEvent;
import org.fao.fenix.export.plugins.output.md.layout.utils.FontType;
import org.fao.fenix.export.plugins.output.md.layout.utils.IndexModel;

import java.util.*;

public class LayoutCreator {

    private static final Logger LOGGER = Logger.getLogger(LayoutCreator.class);
    private  StyleSheetCreator styleSheetCreator;
    private FontType fontType;
    private  static String DATE_TYPEFIELD = Date.class.toString();
    private  static String STRING_TYPEFIELD = String.class.toString();
    private  static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private  static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    ArrayList<Chapter> chapterList;
    private Map<String,IndexModel> indexModelMap;



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

        createIndexModel();

        makeIndex();

        Iterator<String> dataIterator = modelData.keySet().iterator();

        return this.document;

    }

    private void createTitle (MDSDescriptor descriptor) throws DocumentException {
        document.add(new Paragraph(5 , descriptor.getValue().toString(),  FontType.valueField.getFontType() ));
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

        Phrase titlePhrase = new Phrase(12,title, FontType.valueField.getFontType());
        Phrase valuePhrase = new Phrase(12,value, FontType.valueField.getFontType());
        Paragraph p = new Paragraph(titlePhrase + "  :  ");
        p.add(valuePhrase);

        document.add(p);
    }


    private void addOnlyTitle (MDSDescriptor toAdd) throws DocumentException {

        String title = (toAdd.getTitleToVisualize()!= null)? toAdd.getTitleToVisualize(): toAdd.getTitleBean();
        document.add(new Paragraph(new Phrase(12,title,FontType.valueField.getFontType())));
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
        Paragraph paragraph = new Paragraph("Index", FontType.titleField.getFontType());

        Chapter indexChapter = new Chapter(paragraph,0);

        Set<String> keys = indexModelMap.keySet();

        for(String key : keys ){

            PdfPCell left = new PdfPCell(new Phrase(key,FontType.valueField.getFontType()));
            left.setBorder(Rectangle.NO_BORDER);


            System.out.println(key);
            System.out.println(indexModelMap.get(key).getTitle());
            String prhase =(indexModelMap.get(key).getTitle()!=null)? indexModelMap.get(key).getTitle(): "notTitle";
            Chunk pageno = new Chunk(prhase,FontType.valueField.getFontType());
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


            indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));


            String indexUpd = indexKey + ".0";

            TreeMap<String,MDSDescriptor> tempArray = ((TreeMap<String, MDSDescriptor>) ((ArrayList) tempDescriptor.getValue()).get(0));

            Set<String> arrayKeys = tempArray.keySet();

            for(String key: arrayKeys) {
                indexUpd  = updateCounter(indexUpd);
                fillIndex(tempArray.get(key),indexUpd);
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




