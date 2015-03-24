package org.fao.fenix.export.plugins.output.md;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.output.md.data.DataCreator;
import org.fao.fenix.export.plugins.output.md.layout.LayoutCreator;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
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

    @Override
    public void init(Map<String, Object> config) {this.config = config;
        dataCreator = new DataCreator();}

    @Override
    public void process(CoreData resource) throws Exception {
        metadata = resource.getMetadata();
        if(mdsdNode == null)
            getMdsd();
        dataCreator.initDataFromMDSD(mdsdNode,resource.getMetadata());
        document = new Document();
        baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
        LayoutCreator layoutCreator = new LayoutCreator(document);
        document = layoutCreator.init((TreeMap<String, Object>) dataCreator.getMetaDataCleaned());
        document.add(new Paragraph("Prova pdf"));
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
        String result = null;
        Response response = ClientBuilder.newBuilder().build().target(MDSD_URL).request().get();


        result += "{\n" +
                "                \"$schema\":\"http://json-schema.org/draft-04/schema#\",\n" +
                "                \"description\":\"D3S Objects Schema\",\n" +
                "                \"definitions\":{\n" +
                "                    \"OJAxis\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"axisName\":{\n" +
                "                                \"$ref\":\"#/definitions/AxisType\"\n" +
                "                            },\n" +
                "                            \"axisSize\":{\n" +
                "                                \"type\":\"number\"\n" +
                "                            },\n" +
                "                            \"resolution\":{\n" +
                "                                \"$ref\":\"#/definitions/OJMeasure\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"OJMeasure\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"extend\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"composedMU\":{\n" +
                "                                \"type\":\"boolean\"\n" +
                "                            },\n" +
                "                            \"measurementSystem\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"nameMU\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"conversionToStandard\":{\n" +
                "                                \"type\":\"number\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"AxisType\":{\n" +
                "                        \"enum\":[\n" +
                "                            \"Row\",\n" +
                "                            \"Column\",\n" +
                "                            \"Vertical\",\n" +
                "                            \"Time\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"ResponsiblePartyRole\":{\n" +
                "                        \"enum\":[\n" +
                "                            \"Owner\",\n" +
                "                            \"Distributor\",\n" +
                "                            \"Other\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"OjContact\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"phone\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"address\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"emailAddress\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"hoursOfService\":{\n" +
                "                            \"type\":\"object\",\n" +
                "                            \"patternProperties\":{\n" +
                "                                \".{1,}\":{\n" +
                "                                    \"type\":\"string\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"contactInstruction\":{\n" +
                "                            \"type\":\"object\",\n" +
                "                            \"patternProperties\":{\n" +
                "                                \".{1,}\":{\n" +
                "                                    \"type\":\"string\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"OjResponsibleParty\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"pointOfContact\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"organization\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"organizationUnit\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"position\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"role\":{\n" +
                "                                \"$ref\":\"#/definitions/ResponsiblePartyRole\"\n" +
                "                            },\n" +
                "                            \"specify\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"contactInfo\":{\n" +
                "                                \"$ref\":\"#/definitions/OjContact\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"DocumentType\":{\n" +
                "                        \"enum\":[\n" +
                "                            \"Scientific paper\",\n" +
                "                            \"Methodological notes\",\n" +
                "                            \"Legal document\",\n" +
                "                            \"Handbook\",\n" +
                "                            \"Guidelines\",\n" +
                "                            \"Ad hoc press\",\n" +
                "                            \"News\",\n" +
                "                            \"Website\",\n" +
                "                            \"Other\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"OjCitation\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"documentKind\":{\n" +
                "                                \"$ref\":\"#/definitions/DocumentType\"\n" +
                "                            },\n" +
                "                            \"title\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"date\":{\n" +
                "                                \"type\":\"string\",\n" +
                "                                \"format\": \"date\"\n" +
                "                            },\n" +
                "                            \"documentContact\":{\n" +
                "                                \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                            },\n" +
                "                            \"notes\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"link\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"isbn\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"issn\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"OjCode\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"code\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"label\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"OjCodeList\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"codes\":{\n" +
                "                                \"type\":\"array\",\n" +
                "                                \"format\": \"table\",\n" +
                "                                \"items\":{\n" +
                "                                    \"$ref\":\"#/definitions/OjCode\"\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"idCodeList\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"version\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"extendedName\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1,}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"contactInfo\":{\n" +
                "                                \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                            },\n" +
                "                            \"codeListResources\":{\n" +
                "                                \"type\":\"array\",\n" +
                "                                \"format\": \"table\",\n" +
                "                                \"items\":{\n" +
                "                                    \"$ref\":\"#/definitions/OjCitation\"\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"link\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"OjPeriod\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"from\":{\n" +
                "                                \"type\":\"string\",\n" +
                "                                \"format\": \"date\"\n" +
                "                            },\n" +
                "                            \"to\":{\n" +
                "                                \"type\":\"string\",\n" +
                "                                \"format\": \"date\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                },\n" +
                "                \"type\":\"object\",\n" +
                "                \"properties\":{\n" +
                "                    \"uid\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"title\":\"UID\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"UID\"\n" +
                "                        },\n" +
                "                        \"description\":\"Unique identifier.\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Unique identifier.\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"version\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"title\":\"Version\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"Version\",\n" +
                "                            \"fr\":\"French\"\n" +
                "                        },\n" +
                "                        \"description\":\"This is the version of the metadata.\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"This is the version of the metadata.\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"parentIdentifiers\":{\n" +
                "                        \"type\":\"array\",\n" +
                "                        \"format\": \"table\",\n" +
                "                        \"title\":\"Parent Identifiers\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"Parent Identifiers\"\n" +
                "                        },\n" +
                "                        \"description\":\"Don't know that is this\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Don't know that is this\"\n" +
                "                        },\n" +
                "                        \"items\":{\n" +
                "                            \"type\":\"string\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"language\":{\n" +
                "                        \"title\":\"Language\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"Language\"\n" +
                "                        },\n" +
                "                        \"description\":\"Language of the data\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Language of the data\"\n" +
                "                        },\n" +
                "                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                    },\n" +
                "                    \"languageDetails\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"title\":\"Language Details\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"Language Details\"\n" +
                "                        },\n" +
                "                        \"description\":\"Details about the language\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Details about the language\"\n" +
                "                        },\n" +
                "                        \"patternProperties\":{\n" +
                "                            \".{1}\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"title\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"patternProperties\":{\n" +
                "                            \".{1}\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"creationDate\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"format\": \"date\",\n" +
                "                        \"title\":\"Creation Date\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"Creation Date\"\n" +
                "                        },\n" +
                "                        \"description\":\"Date for dataset creation\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Date for dataset creation\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"characterSet\":{\n" +
                "                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                    },\n" +
                "                    \"metadataStandardName\":{\n" +
                "                        \"type\":\"string\"\n" +
                "                    },\n" +
                "                    \"metadataStandardVersion\":{\n" +
                "                        \"type\":\"string\"\n" +
                "                    },\n" +
                "                    \"metadataLanguage\":{\n" +
                "                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                    },\n" +
                "                    \"contacts\":{\n" +
                "                        \"type\":\"array\",\n" +
                "                        \"format\": \"table\",\n" +
                "                        \"items\":{\n" +
                "                            \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"noDataValue\":{\n" +
                "                        \"type\":\"string\",\n" +
                "                        \"title\":\"No Data Value\",\n" +
                "                        \"title_i18n\":{\n" +
                "                            \"en\":\"No Data Value\"\n" +
                "                        },\n" +
                "                        \"description\":\"Geospatial data only\",\n" +
                "                        \"description_i18n\":{\n" +
                "                            \"en\":\"Geospatial data only\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meDocuments\":{\n" +
                "                        \"type\":\"array\",\n" +
                "                        \"format\": \"table\",\n" +
                "                        \"items\":{\n" +
                "                            \"$ref\":\"#/definitions/MeDocuments\"\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meInstitutionalMandate\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"legalActsAgreements\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"institutionalMandateDataSharing\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meAccessibility\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"seDataDissemination\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"seDistribution\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"onlineResource\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            },\n" +
                "                                            \"disseminationFormat\":{\n" +
                "                                                \"type\":\"array\",\n" +
                "                                                \"format\": \"table\",\n" +
                "                                                \"items\":{\n" +
                "                                                    \"type\":\"string\"\n" +
                "                                                }\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"seReleasePolicy\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"releaseCalendar\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"releaseCalendarAccess\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            },\n" +
                "                                            \"disseminationPeriodicity\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                            },\n" +
                "                                            \"embargoTime\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjPeriod\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seClarity\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"clarity\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"metadataCompletenessRate\":{\n" +
                "                                        \"type\":\"integer\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seConfidentiality\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"confidentialityPolicy\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"confidentialityDataTreatment\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"confidentialityStatus\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meContent\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"resourceRepresentationType\":{\n" +
                "                                \"type\":\"object\"\n" +
                "                            },\n" +
                "                            \"keywords\":{\n" +
                "                                \"type\":\"array\",\n" +
                "                                \"format\": \"tabs\",\n" +
                "                                \"items\":{\n" +
                "                                    \"type\":\"string\"\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"description\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"statisticalConceptsDefinitions\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seReferencePopulation\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"statisticalPopulation\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"statisticalUnit\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"referencePeriod\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"referenceArea\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seCoverage\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"coverageSectors\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"coverageSectorsDetails\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"coverageTime\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjPeriod\"\n" +
                "                                    },\n" +
                "                                    \"coverageGeographic\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seCodeList\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"numberOfLevels\":{\n" +
                "                                        \"type\":\"integer\"\n" +
                "                                    },\n" +
                "                                    \"typeOfCodeList\":{\n" +
                "                                        \"type\":\"object\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meDataQuality\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"qualityManagement\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"qualityAssessment\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"qualityAssurance\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seAccuracy\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"accuracyNonSampling\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"accuracySampling\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seDataRevision\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"revisionPolicy\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"revisionPractice\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seRelevance\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"userNeeds\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"userSatisfaction\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"completeness\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"completenessPercentage\":{\n" +
                "                                        \"type\":\"number\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seComparability\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"comparabilityGeographical\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"comparabilityTime\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"coherenceIntern\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seTimelinessPunctuality\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"timeliness\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"punctuality\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meMaintenance\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"maintenanceAgency\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seUpdate\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"updateDate\":{\n" +
                "                                        \"type\":\"string\",\n" +
                "                                        \"format\": \"date\"\n" +
                "                                    },\n" +
                "                                    \"updatePeriodicity\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seMetadataMaintenance\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"metadataLastCertified\":{\n" +
                "                                        \"type\":\"string\",\n" +
                "                                        \"format\": \"date\"\n" +
                "                                    },\n" +
                "                                    \"metadataLastPosted\":{\n" +
                "                                        \"type\":\"string\",\n" +
                "                                        \"format\": \"date\"\n" +
                "                                    },\n" +
                "                                    \"metadataLastUpdate\":{\n" +
                "                                        \"type\":\"string\",\n" +
                "                                        \"format\": \"date\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meReferenceSystem\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"referenceSystemIdentifier\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"referenceSystemName\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"patternProperties\":{\n" +
                "                                    \".{1}\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"referenceSystemAuthority\":{\n" +
                "                                \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                            },\n" +
                "                            \"seProjection\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"projection\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"projectionName\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"seProjectionParameters\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"zone\":{\n" +
                "                                                \"type\":\"integer\"\n" +
                "                                            },\n" +
                "                                            \"standardParallel\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"longitudeOfCentralMeridian\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"latitudeOfProjectionOrigin\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"falseEasting\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"falseNorthing\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"falseEastingNorthingUnits\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjMeasure\"\n" +
                "                                            },\n" +
                "                                            \"scaleFactorAtEquator\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"heightOfProspectivePointAboveSurface\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"longitudeOfProjectionCenter\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"latitudeOfProjectionCenter\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"scaleFactorAtCenterLine\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"straightVerticalLongitudeFromPole\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"scaleFactorAtProjectionOrigin\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"seObliqueLineAzimuth\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"properties\":{\n" +
                "                                                    \"azimuthAngle\":{\n" +
                "                                                        \"type\":\"number\"\n" +
                "                                                    },\n" +
                "                                                    \"azimuthMeasurePointLongitude\":{\n" +
                "                                                        \"type\":\"number\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"seObliqueLinePoint\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"properties\":{\n" +
                "                                                    \"obliqueLineLatitude\":{\n" +
                "                                                        \"type\":\"number\"\n" +
                "                                                    },\n" +
                "                                                    \"obliqueLineLongitude\":{\n" +
                "                                                        \"type\":\"number\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seEllipsoid\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"ellipsoid\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"ellipsoidName\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"seEllipsoidParameters\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"semiMajorAxis\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            },\n" +
                "                                            \"axisUnits\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjMeasure\"\n" +
                "                                            },\n" +
                "                                            \"denominatorOfFlatteringRatio\":{\n" +
                "                                                \"type\":\"number\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seDatum\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"datum\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"datumName\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meResourceStructure\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"seResourceDimensions\":{\n" +
                "                                \"type\":\"array\",\n" +
                "                                \"format\": \"table\",\n" +
                "                                \"items\":{\n" +
                "                                    \"$ref\":\"#/definitions/SeResourceDimensions\"\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seResourceRecords\":{\n" +
                "                                \"type\":\"array\",\n" +
                "                                \"format\": \"table\",\n" +
                "                                \"items\":{\n" +
                "                                    \"$ref\":\"#/definitions/SeResourceRecords\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meSpatialRepresentation\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"typeOfProduct\":{\n" +
                "                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                            },\n" +
                "                            \"processing\":{\n" +
                "                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                            },\n" +
                "                            \"noDataValue\":{\n" +
                "                                \"type\":\"string\"\n" +
                "                            },\n" +
                "                            \"seBoundingBox\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"xmin\":{\n" +
                "                                        \"type\":\"number\"\n" +
                "                                    },\n" +
                "                                    \"xmax\":{\n" +
                "                                        \"type\":\"number\"\n" +
                "                                    },\n" +
                "                                    \"ymin\":{\n" +
                "                                        \"type\":\"number\"\n" +
                "                                    },\n" +
                "                                    \"ymax\":{\n" +
                "                                        \"type\":\"number\"\n" +
                "                                    },\n" +
                "                                    \"seGridSpatialRepresentation\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"numberOfDimensions\":{\n" +
                "                                                \"type\":\"integer\"\n" +
                "                                            },\n" +
                "                                            \"axisDimensionProperties\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjAxis\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"seVectorSpatialRepresentation\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"topologyLevel\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"meStatisticalProcessing\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "                            \"seDataSource\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"sePrimaryDataCollection\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"dataCollector\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                                            },\n" +
                "                                            \"typeOfCollection\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                            },\n" +
                "                                            \"samplingProcedure\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"dataCollection\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"collectionPeriodicity\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"seSecondaryDataCollection\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"properties\":{\n" +
                "                                            \"originOfCollectedData\":{\n" +
                "                                                \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                            },\n" +
                "                                            \"organization\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"rawDataDescription\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            },\n" +
                "                                            \"dataCollection\":{\n" +
                "                                                \"type\":\"object\",\n" +
                "                                                \"patternProperties\":{\n" +
                "                                                    \".{1}\":{\n" +
                "                                                        \"type\":\"string\"\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seDataCompilation\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"missingData\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"weights\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"aggregationProcessing\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"aggregationFormula\":{\n" +
                "                                        \"type\":\"string\"\n" +
                "                                    },\n" +
                "                                    \"dataAdjustment\":{\n" +
                "                                        \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                                    },\n" +
                "                                    \"dataAdjustmentDetails\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"indexType\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"basePeriod\":{\n" +
                "                                        \"type\":\"string\",\n" +
                "                                        \"format\": \"date\"\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            },\n" +
                "                            \"seDataValidation\":{\n" +
                "                                \"type\":\"object\",\n" +
                "                                \"properties\":{\n" +
                "                                    \"dataValidationIntermediate\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"dataValidationOutput\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    },\n" +
                "                                    \"dataValidationSource\":{\n" +
                "                                        \"type\":\"object\",\n" +
                "                                        \"patternProperties\":{\n" +
                "                                            \".{1}\":{\n" +
                "                                                \"type\":\"string\"\n" +
                "                                            }\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    },\n" +
                "                    \"dsd\":{\n" +
                "                        \"type\":\"object\",\n" +
                "                        \"properties\":{\n" +
                "\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }";


        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jp = factory.createParser(result);

        mdsdNode = mapper.readTree(jp);
        mdsdNode = mapper.readTree(jp);

    }

}
