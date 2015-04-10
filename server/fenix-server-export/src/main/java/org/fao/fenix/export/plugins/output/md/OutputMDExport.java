package org.fao.fenix.export.plugins.output.md;


import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.input.metadata.mediator.MDClientMediator;
import org.fao.fenix.export.plugins.output.md.data.DataCreator;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.plugins.output.md.layout.LayoutCreator;
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
    private FileOutputStream temp;
    private MeIdentification metadata;
    private DataCreator dataCreator ;
    private final String MDSD_URL = "http://faostat3.fao.org/d3s2/v2/mdsd";
    private JsonNode mdsdNode;
    private ByteArrayOutputStream baos;
    private final int MARGIN_LEFT  = 50;
    private final int MARGIN_UP  = 80;
    private final int MARGIN_BOTTOM  = 50;
    private final int MARGIN_RIGHT  = 50;



    /** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {

        /** Current page number (will be reset for every chapter). */
        int pagenumber;
        String title;
        private final static String IMAGE_PATH = "logo/FAO_logo.png";
        Image logo;

        public HeaderFooter(String title) {
            this.title = title;
        }


        /**
         * Initialize one of the headers.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            pagenumber = 1;
            try {
                logo = Image.getInstance(this.getClass().getClassLoader().getResource("../").getPath() + IMAGE_PATH);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Initialize one of the headers, based on the chapter title;
         * reset the page number.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
         *      com.itextpdf.text.Paragraph)
         */
        public void onChapter(PdfWriter writer, Document document,
                              float paragraphPosition, Paragraph title) {
           // pagenumber = 1;
        }

        /**
         * Increase the page number.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber++;
        }

        /**
         * Adds the header and the footer.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(String.format(" %d", pagenumber - 1), MDFontTypes.footerField.getFontType()),
                    ((rect.getLeft() + rect.getRight())-50), rect.getBottom() - 18, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(title.toUpperCase(), MDFontTypes.headerField.getFontType()),
                    ((rect.getLeft() + rect.getRight()) / 2), rect.getTop() + 5, 0);
            logo.scalePercent((float) 15);

            logo.setAbsolutePosition(rect.getLeft()+7,rect.getTop()-7);
            try {
                writer.getDirectContent().addImage(logo);
            } catch (DocumentException e) {
                e.printStackTrace();
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
        dataCreator.initDataFromMDSD(mdsdNode,resource.getMetadata());
        Document document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT,
                MARGIN_UP, MARGIN_BOTTOM);
        baos = new ByteArrayOutputStream();
        PdfWriter contentWriter = PdfWriter.getInstance(document, baos);
        String title = retrieveTitle((TreeMap<String, Object>) dataCreator.getMetaDataCleaned());

        HeaderFooter event = new HeaderFooter(title);
        contentWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));

        contentWriter.setPageEvent(event);

        document.open();
        LayoutCreator layoutCreator = new LayoutCreator(document);
        document = layoutCreator.init((TreeMap<String, Object>) dataCreator.getMetaDataCleaned());
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
