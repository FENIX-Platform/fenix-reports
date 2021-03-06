package org.fao.fenix.export.plugins.output.fmd;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreGenericData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.output.fmd.layout.standard.FMDLayoutCreator;
import org.fao.fenix.export.plugins.output.md.data.DataCreator;
import org.fao.fenix.export.plugins.output.md.layout.utils.ColorType;
import org.fao.fenix.export.plugins.output.md.layout.utils.MDFontTypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

public class OutputFMDExport extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputFMDExport.class);
    private Map<String, Object> config;
    private DataCreator dataCreator ;
    private ByteArrayOutputStream baos;
    private final String LANGUAGE_PROPERTY = "lang";
    private static String UPPER_TITLE = "Global Foot and Mouth Disease (FMD)";

    private final String FILENAME_PROPERTY = "fileName";
    private final String FILENAME_DEFAULT = "fmdExport.pdf";

    private final String FONTNAME_TITLEPAGES = "roboto_thin";
    private final float LOGO_HEIGHT = 100;
    private final float LOGO_WIDTH = 130;
    private final float OFFSET_RIGHT_TITLE = 49;
    private final float OFFSET_TOP_TITLE = 5;
    private final float OFFSET_RIGHT_LOGO = 7;
    private final float OFFSET_TOP_LOGO = 3;
    private final float DELIMITER_X_POS = 200;
    private final float DELIMITER_Y_POS_START = 805;
    private final float DELIMITER_Y_POS_END = 788;
    private final float OFFSET_FOOTER_UP = 18;
    private final int MARGIN_LEFT  = 50;
    private final int MARGIN_UP  = 80;
    private final int MARGIN_BOTTOM  = 50;
    private final int MARGIN_RIGHT  = 50;
    private final int RIGHT_OFFSET_FOOTER = 50;
    private static float SEPARATOR_WIDTH = (float) 0.71;


    /** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {

        int pagenumber;
        String title;
        private final static String IMAGE_PATH = "/images/logo/FAO_logo.png";
        Image logo;
        Phrase titlePhrase;
        Font titleHeaderFont;

        public HeaderFooter(String title) {
            this.title = title;
            titlePhrase = new Phrase(title);
        }


        public void onOpenDocument(PdfWriter writer, Document document) {
            pagenumber = -1;
            try {
                logo = Image.getInstance(getClass().getClassLoader().getResource(IMAGE_PATH));
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void onStartPage(PdfWriter writer, Document document) {

            pagenumber = (pagenumber!=0)? pagenumber+1 : pagenumber+2;
        }


        public void onEndPage(PdfWriter writer, Document document) {

            if (pagenumber != 0) {
                if(titleHeaderFont == null) {
                    titleHeaderFont = FontFactory.getFont((FONTNAME_TITLEPAGES), 12, ColorType.blueFenix.getCmykColor());
                }
                Phrase titlePhrase = new Phrase(title,titleHeaderFont);
                Rectangle rect = writer.getBoxSize("art");
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, new Phrase(String.format(" %d", pagenumber - 1), MDFontTypes.footerField.getFontType()),
                        ((rect.getLeft() + rect.getRight()) - RIGHT_OFFSET_FOOTER), rect.getBottom() - OFFSET_FOOTER_UP, 0);
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

        String language = (config.get(LANGUAGE_PROPERTY)!= null && !config.get(LANGUAGE_PROPERTY).toString().equals(""))? config.get(LANGUAGE_PROPERTY).toString(): "EN";

        // TODO: setting configuration pagesize
        Document document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT,
                MARGIN_UP, MARGIN_BOTTOM);
        baos = new ByteArrayOutputStream();
        PdfWriter contentWriter = PdfWriter.getInstance(document, baos);

        HeaderFooter event = new HeaderFooter(UPPER_TITLE);
        contentWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        contentWriter.setPageEvent(event);

        document.open();
        FMDLayoutCreator layoutCreator = new FMDLayoutCreator(document);

        TreeMap<String,Object> dataModel = new TreeMap<String,Object>(((CoreGenericData)resource).getDataStructure());
        document = layoutCreator.init(dataModel, UPPER_TITLE, contentWriter);
        document.close();

    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(((config.get(FILENAME_PROPERTY) != null) ? config.get(FILENAME_PROPERTY).toString() : FILENAME_DEFAULT));
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

}
