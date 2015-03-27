package org.fao.fenix.export.plugins.output.md;


import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
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
import org.fao.fenix.export.plugins.output.md.layout.utils.ContentEvent;

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
    private final String MDSD_URL = "http://hqlprfenixapp2.hq.un.fao.org:12900/wds/rest/mdsd/";
    private JsonNode mdsdNode;
    private ByteArrayOutputStream baos;

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
        ContentEvent event = new ContentEvent();
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
