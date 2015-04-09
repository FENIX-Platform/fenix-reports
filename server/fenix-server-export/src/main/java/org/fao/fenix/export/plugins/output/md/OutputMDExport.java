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
import org.fao.fenix.export.plugins.output.md.layout.LayoutCreator;
import org.fao.fenix.export.plugins.output.md.layout.utils.MDFontTypes;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
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

    /** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {
        /** Alternating phrase for the header. */
        Phrase[] header = new Phrase[2];
        /** Current page number (will be reset for every chapter). */
        int pagenumber;

        /**
         * Initialize one of the headers.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            header[0] = new Phrase("Movie history");
            pagenumber = 1;
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
                    Element.ALIGN_CENTER, new Phrase(String.format(" %d", pagenumber-1), MDFontTypes.footerField.getFontType()),
                    ((rect.getLeft() + rect.getRight())-50), rect.getBottom() - 18, 0);
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
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        baos = new ByteArrayOutputStream();
        PdfWriter contentWriter = PdfWriter.getInstance(document, baos);

        HeaderFooter event = new HeaderFooter();
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

}
