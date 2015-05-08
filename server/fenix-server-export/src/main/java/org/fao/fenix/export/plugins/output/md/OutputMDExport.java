package org.fao.fenix.export.plugins.output.md;


import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.input.metadata.mediator.MDClientMediator;
import org.fao.fenix.export.plugins.output.md.data.DataCreator;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.layout.factory.LayoutCreator;
import org.fao.fenix.export.plugins.output.md.layout.utils.ColorType;
import org.fao.fenix.export.plugins.output.md.layout.utils.MDFontTypes;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class OutputMDExport extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputMDExport.class);
    private Map<String, Object> config;
    private Document document;
    private HeaderFooter event;
    private FileOutputStream temp;
    private MeIdentification metadata;
    private DataCreator dataCreator ;
    private final String MDSD_URL = "http://faostat3.fao.org/d3s2/v2/mdsd";
    private JsonNode mdsdNode;
    private ByteArrayOutputStream baos;

    private final float LOGO_HEIGHT = 100;
    private final float LOGO_WIDTH = 130;
    private final float OFFSET_RIGHT_TITLE = 49;
    private final float OFFSET_TOP_TITLE = 5;

    private final float OFFSET_RIGHT_LOGO = 7;
    private final float OFFSET_TOP_LOGO = 3;


    private final int TITLE_CHAR_NUMBERS_LIMIT = 25;

    private final float DELIMITER_X_POS = 200;
    private final float DELIMITER_Y_POS_START = 805;
    private final float DELIMITER_Y_POS_END = 788;
    private final float OFFSET_TITLE_LEFT = 70;

    private final float DELIMITER_DOWN_OFFSET = 20;

    private final int MARGIN_LEFT  = 50;
    private final int MARGIN_UP  = 80;
    private final int MARGIN_BOTTOM  = 50;
    private final int MARGIN_RIGHT  = 50;
    private final int LOGO_SCALE_PERCENTAGE = 10;
    private final int RIGHT_OFFSET_FOOTER = 50;
    private static float SEPARATOR_WIDTH = (float) 0.71;


    /** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {

        int pagenumber;
        String title;
        private final static String IMAGE_PATH = "logo/FAO_logo.png";
        Image logo;
        Phrase titlePhrase;

        public HeaderFooter(String title) {
            this.title = title;
            titlePhrase = new Phrase(title, MDFontTypes.headerField.getFontType());
        }


        public void onOpenDocument(PdfWriter writer, Document document) {
            pagenumber = -1;
            try {
                logo = Image.getInstance(this.getClass().getClassLoader().getResource("../").getPath() + IMAGE_PATH);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void onChapter(PdfWriter writer, Document document,
                              float paragraphPosition, Paragraph title) {
            // pagenumber = 1;
            System.out.println("stop here");
        }

        public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {


            System.out.println("start!");
        }

        public void onStartPage(PdfWriter writer, Document document) {

            pagenumber = (pagenumber!=0)? pagenumber+1 : pagenumber+2;
        }


        public void onEndPage(PdfWriter writer, Document document) {

            if (pagenumber != 0) {
                PdfPCell line = new PdfPCell();
                Rectangle rect = writer.getBoxSize("art");
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, new Phrase(String.format(" %d", pagenumber - 1), MDFontTypes.footerField.getFontType()),
                        ((rect.getLeft() + rect.getRight()) - RIGHT_OFFSET_FOOTER), rect.getBottom() - 18, 0);
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, titlePhrase,
                        ((rect.getLeft() + rect.getRight()) / 2)-OFFSET_RIGHT_TITLE, rect.getTop() + OFFSET_TOP_TITLE, 0);

                logo.scaleToFit(LOGO_HEIGHT, LOGO_WIDTH);
                logo.setAbsolutePosition(rect.getLeft() + OFFSET_RIGHT_LOGO, rect.getTop() - OFFSET_TOP_LOGO);
                try {
                    document.add(logo);

                    PdfContentByte cb = writer.getDirectContent();
                    cb.setColorStroke(ColorType.borderGrey.getCmykColor());
                    cb.setLineWidth(SEPARATOR_WIDTH);
                    //DELIMITER_X_POS - offsetToRMV * OFFSET_TITLE_LEFT
                    cb.moveTo( DELIMITER_X_POS, DELIMITER_Y_POS_END);
                    cb.lineTo( DELIMITER_X_POS, DELIMITER_Y_POS_START);
                    cb.stroke();

                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(Map<String, Object> config) {this.config = config;
        dataCreator = new DataCreator();}

    @Override
    public void process(CoreData resource) throws Exception {
        metadata = resource.getMetadata();
        if(mdsdNode == null)
            getMdsd();

        // getting data in the right format
        String language = (config.get("lang")!= null && !config.get("lang").toString().equals(""))? config.get("lang").toString(): "EN";
        dataCreator.initDataFromMDSD(mdsdNode,resource.getMetadata(), language);

        // TODO: setting configuration pagesize
        Document document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT,
                MARGIN_UP, MARGIN_BOTTOM);
        baos = new ByteArrayOutputStream();
        PdfWriter contentWriter = PdfWriter.getInstance(document, baos);
        String title = retrieveTitle((TreeMap<String, Object>) dataCreator.getMetaDataCleaned());

        HeaderFooter event = new HeaderFooter(title);
        contentWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        contentWriter.setPageEvent(event);

        document.open();
        boolean isFull = config.get("full")!= null? Boolean.getBoolean(config.get("full").toString()): false;
        LayoutCreator layoutCreator = LayoutCreator.createInstance(isFull, document);

        document = layoutCreator.init((TreeMap<String, Object>) dataCreator.getMetaDataCleaned(), title, contentWriter);
        document.close();

    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(((config.get("fileName") != null) ? config.get("fileName").toString() : "fenixExport.pdf"));
        coreOutputHeader.setSize(baos.size());
        coreOutputHeader.setType(CoreOutputType.pdf);
        return coreOutputHeader;
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        baos.writeTo(outputStream);
        outputStream.close();
        outputStream.flush();
    }


    private void getMdsd () throws IOException {
        mdsdNode =  new MDClientMediator().getParsedMDSD(MDSD_URL);
    }

    private String retrieveTitle (TreeMap<String, Object> mdsdStructure) {
        String result = null ;
        Set<String> keys = mdsdStructure.keySet();
        for(String key: keys) {
            MDSDescriptor  tmp= (MDSDescriptor)mdsdStructure.get(key);
            if(tmp.getTitleBean().equals("title")){
                result = tmp.getValue().toString();
                break;
            }
        }
        return result;
    }

}
