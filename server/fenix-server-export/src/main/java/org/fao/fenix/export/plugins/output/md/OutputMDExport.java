package org.fao.fenix.export.plugins.output.md;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.output.md.data.DataCreator;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class OutputMDExport extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputMDExport.class);
    private Map<String, Object> config;
    private Document document;
    private FileOutputStream temp;
    private MeIdentification metadata;
    private DataCreator dataCreator ;

    @Override
    public void init(Map<String, Object> config) {
        this.config = config;
        this.document = new Document();


    }

    @Override
    public void process(CoreData resource) throws Exception {
        metadata = resource.getMetadata();
    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(((config.get("fileName") != null) ? config.get("fileName").toString() : "fenixExport.pdf"));
        coreOutputHeader.setSize(100);
        coreOutputHeader.setType(CoreOutputType.pdf);
        return coreOutputHeader;
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {


        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            if (metadata != null) {
                compileData(document, metadata);
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();
        outputStream.close();
    }


    private void compileData(Document document, MeIdentification meIdentification) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {


        String url = "http://faostat3.fao.org/d3s2/v2/mdsd";


        dataCreator = new DataCreator();

        String result = null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();


        result +="{\n" +
                "  \"$schema\":\"http://json-schema.org/draft-04/schema#\",\n" +
                "  \"description\":\"D3S Objects Schema\",\n" +
                "  \"definitions\":{\n" +
                "    \"OJAxis\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"axisName\":{\n" +
                "          \"$ref\":\"#/definitions/AxisType\"\n" +
                "        },\n" +
                "        \"axisSize\":{\n" +
                "          \"type\":\"number\"\n" +
                "        },\n" +
                "        \"resolution\":{\n" +
                "          \"$ref\":\"#/definitions/OJMeasure\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"OJMeasure\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"extend\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"composedMU\":{\n" +
                "          \"type\":\"boolean\"\n" +
                "        },\n" +
                "        \"measurementSystem\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"nameMU\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"conversionToStandard\":{\n" +
                "          \"type\":\"number\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"AxisType\":{\n" +
                "      \"enum\":[\n" +
                "        \"Row\",\n" +
                "        \"Column\",\n" +
                "        \"Vertical\",\n" +
                "        \"Time\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"ResponsiblePartyRole\":{\n" +
                "      \"enum\":[\n" +
                "        \"Owner\",\n" +
                "        \"Distributor\",\n" +
                "        \"Other\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"OjContact\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"phone\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"address\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"emailAddress\":{\n" +
                "          \"type\":\"string\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"hoursOfService\":{\n" +
                "        \"type\":\"object\",\n" +
                "        \"patternProperties\":{\n" +
                "          \".{1,}\":{\n" +
                "            \"type\":\"string\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"contactInstruction\":{\n" +
                "        \"type\":\"object\",\n" +
                "        \"patternProperties\":{\n" +
                "          \".{1,}\":{\n" +
                "            \"type\":\"string\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"OjResponsibleParty\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"pointOfContact\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"organization\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"organizationUnit\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"position\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"role\":{\n" +
                "          \"$ref\":\"#/definitions/ResponsiblePartyRole\"\n" +
                "        },\n" +
                "        \"specify\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"role\":{\n" +
                "          \"$ref\":\"#/definitions/OjContact\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"DocumentType\":{\n" +
                "      \"enum\":[\n" +
                "        \"Scientific paper\",\n" +
                "        \"Methodological notes\",\n" +
                "        \"Legal document\",\n" +
                "        \"Handbook\",\n" +
                "        \"Guidelines\",\n" +
                "        \"Ad hoc press\",\n" +
                "        \"News\",\n" +
                "        \"Website\",\n" +
                "        \"Other\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"OjCitation\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"documentKind\":{\n" +
                "          \"$ref\":\"#/definitions/DocumentType\"\n" +
                "        },\n" +
                "        \"title\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"date\":{\n" +
                "          \"type\":\"date-time\"\n" +
                "        },\n" +
                "        \"documentContact\":{\n" +
                "          \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "        },\n" +
                "        \"notes\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"link\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"isbn\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"issn\":{\n" +
                "          \"type\":\"string\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"OjCode\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"code\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"label\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"OjCodeList\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"codes\":{\n" +
                "          \"type\":\"array\",\n" +
                "          \"items\":{\n" +
                "            \"$ref\":\"#/definitions/OjCode\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"idCodeList\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"version\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"extendedName\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1,}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"contactInfo\":{\n" +
                "          \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "        },\n" +
                "        \"codeListResources\":{\n" +
                "          \"type\":\"array\",\n" +
                "          \"items\":{\n" +
                "            \"$ref\":\"#/definitions/OjCitation\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"link\":{\n" +
                "          \"type\":\"string\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"OjPeriod\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"from\":{\n" +
                "          \"type\":\"date-time\"\n" +
                "        },\n" +
                "        \"to\":{\n" +
                "          \"type\":\"date-time\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"type\":\"object\",\n" +
                "  \"properties\":{\n" +
                "    \"uid\":{\n" +
                "      \"type\":\"string\",\n" +
                "      \"title\":\"Resource identification code\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Resource identification code\"\n" +
                "      }\n" +
                "\n" +
                "    },\n" +
                "    \"version\":{\n" +
                "      \"type\":\"string\",\n" +
                "      \"title\":\"Version\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Version\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"parentIdentifiers\":{\n" +
                "      \"type\":\"array\",\n" +
                "      \"title\":\"Parent(s) metadata ID\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Parent(s) metadata ID\"\n" +
                "      },\n" +
                "      \"items\":{\n" +
                "        \"type\":\"string\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"language\":{\n" +
                "      \"title\":\"Language(s)\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Language(s)\"\n" +
                "      },\n" +
                "\n" +
                "      \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "    },\n" +
                "    \"languageDetails\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"title\":\"Language details\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Language details\"\n" +
                "      },\n" +
                "\n" +
                "      \"patternProperties\":{\n" +
                "        \".{1}\":{\n" +
                "          \"type\":\"string\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"title\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"title\":\"Title\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Title\"\n" +
                "      },\n" +
                "      \"description\":\"Textual label used as title of the resource.\",\n" +
                "      \"description_i18n\":{\n" +
                "        \"en\":\"Textual label used as title of the resource.\"\n" +
                "      },\n" +
                "      \"patternProperties\":{\n" +
                "        \".{1}\":{\n" +
                "          \"type\":\"string\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"creationDate\":{\n" +
                "      \"type\":\"date-time\",\n" +
                "      \"title\":\"Creation date\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Creation date\"\n" +
                "      },\n" +
                "      \"description\":\"Creation date of the resource.\",\n" +
                "      \"description_i18n\":{\n" +
                "        \"en\":\"Creation date of the resource.\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"characterSet\":{\n" +
                "      \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "    },\n" +
                "    \"metadataStandardName\":{\n" +
                "      \"type\":\"string\",\n" +
                "      \"title\":\"Character-set\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Character-set\"\n" +
                "      },\n" +
                "      \"description\":\"Full name of the character coding standard used by the resource.\",\n" +
                "      \"description_i18n\":{\n" +
                "        \"en\":\"Full name of the character coding standard used by the resource.\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"metadataStandardVersion\":{\n" +
                "      \"type\":\"string\",\n" +
                "      \"title\":\"Used metadata standard\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Used metadata standard\"\n" +
                "      }\n" +
                "\n" +
                "    },\n" +
                "    \"metadataLanguage\":{\n" +
                "      \"title\":\"Version of metadata standard\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Version of metadata standard\"\n" +
                "      },\n" +
                "\n" +
                "      \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "    },\n" +
                "    \"contacts\":{\n" +
                "      \"type\":\"array\",\n" +
                "      \"title\":\"Contact(s)\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Contact(s)\"\n" +
                "      },\n" +
                "\n" +
                "      \"items\":{\n" +
                "        \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"noDataValue\":{\n" +
                "      \"type\":\"string\",\n" +
                "      \"title\":\"Value assigned to No-data\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"Value assigned to No-data\"\n" +
                "      }\n" +
                "\n" +
                "    },\n" +
                "    \"meDocuments\":{\n" +
                "      \"type\":\"array\",\n" +
                "      \"title\":\"DOCUMENTS\",\n" +
                "      \"title_i18n\":{\n" +
                "        \"en\":\"DOCUMENTS\"\n" +
                "      },\n" +
                "      \"items\":{\n" +
                "        \"$ref\":\"#/definitions/MeDocuments\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"meInstitutionalMandate\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"legalActsAgreements\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"title\":\"Legal acts/agreements\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Legal acts/agreements\"\n" +
                "          },\n" +
                "\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"institutionalMandateDataSharing\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"title\":\"Data sharing arrangements\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Data sharing arrangements\"\n" +
                "          },\n" +
                "\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meAccessibility\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"seDataDissemination\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"seDistribution\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"onlineResource\":{\n" +
                "                  \"type\":\"string\",\n" +
                "                  \"title\":\"Link to the on-line resource\",\n" +
                "                  \"title_i18n\":{\n" +
                "                    \"en\":\"Link to the on-line resource\"\n" +
                "                  }\n" +
                "                },\n" +
                "                \"disseminationFormat\":{\n" +
                "                  \"type\":\"array\",\n" +
                "                  \"title\":\"Dissemination formats\",\n" +
                "                  \"title_i18n\":{\n" +
                "                    \"en\":\"Dissemination formats\"\n" +
                "                  },\n" +
                "                  \"items\":{\n" +
                "                    \"type\":\"string\"\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"seReleasePolicy\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"releaseCalendar\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"releaseCalendarAccess\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                },\n" +
                "                \"disseminationPeriodicity\":{\n" +
                "                  \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                },\n" +
                "                \"embargoTime\":{\n" +
                "                  \"$ref\":\"#/definitions/OjPeriod\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seClarity\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"clarity\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Clarity\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Clarity\"\n" +
                "              },\n" +
                "              \"description\":\"Extent to which easily comprehensible metadata are available. It indicates whether a resource is accompanied by appropriate metadata and other relevant documentation.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Extent to which easily comprehensible metadata are available. It indicates whether a resource is accompanied by appropriate metadata and other relevant documentation.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"metadataCompletenessRate\":{\n" +
                "              \"type\":\"integer\",\n" +
                "              \"title\":\"Metadata completeness rate\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Metadata completeness rate\"\n" +
                "              },\n" +
                "              \"description\":\"The percentage of completeness of metadata offers a numerical evaluation of the extent to which the resource is documented.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"The percentage of completeness of metadata offers a numerical evaluation of the extent to which the resource is documented.\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seConfidentiality\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"confidentialityPolicy\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Confidentiality - Policy\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Confidentiality - Policy\"\n" +
                "              },\n" +
                "              \"description\":\"Legislative measures or other formal procedures which prevent unauthorized disclosure of data that identify a person or economic entity either directly or indirectly. It consists in textual description and references to legislation or other rules related to statistical confidentiality.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Legislative measures or other formal procedures which prevent unauthorized disclosure of data that identify a person or economic entity either directly or indirectly. It consists in textual description and references to legislation or other rules related to statistical confidentiality.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"confidentialityDataTreatment\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Confidentiality - Data treatment\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Confidentiality - Data treatment\"\n" +
                "              },\n" +
                "              \"description\":\"Rules applied for treating the resource to ensure confidentiality and prevent unauthorized disclosure.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Rules applied for treating the resource to ensure confidentiality and prevent unauthorized disclosure.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"confidentialityStatus\":{\n" +
                "              \"title\":\"Status of confidentiality\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Status of confidentiality\"\n" +
                "              },\n" +
                "              \"description\":\"Coded information describing the degree of confidentiality of the resource.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Coded information describing the degree of confidentiality of the resource.\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meContent\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"resourceRepresentationType\":{\n" +
                "          \"type\":\"representationtype\",\n" +
                "          \"title\":\"Type of resource\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Type of resource\"\n" +
                "          },\n" +
                "          \"description\":\"Typology of the resource the metadata refers to. This metadata element determines whether certain meta-data entities are, or are not applicable. For example, the metadata entities 'SpatialRepresentation' and 'ReferenceSystem' are only available for geospatial resource types (e.g. raster; vector).\",\n" +
                "          \"description_i18n\":{\n" +
                "            \"en\":\"Typology of the resource the metadata refers to. This metadata element determines whether certain meta-data entities are, or are not applicable. For example, the metadata entities 'SpatialRepresentation' and 'ReferenceSystem' are only available for geospatial resource types (e.g. raster; vector).\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"keywords\":{\n" +
                "          \"type\":\"array\",\n" +
                "          \"title\":\"Keywords\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Keywords\"\n" +
                "          },\n" +
                "          \"description\":\"Commonly used word(s), formalized word(s) or phrase(s) used to describe the resource.\",\n" +
                "          \"description_i18n\":{\n" +
                "            \"en\":\"Commonly used word(s), formalized word(s) or phrase(s) used to describe the resource.\"\n" +
                "          },\n" +
                "          \"items\":{\n" +
                "            \"type\":\"string\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"description\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"title\":\"Description\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Description\"\n" +
                "          },\n" +
                "          \"description\":\"Overview of the main characteristics of the resource and summary of the information contained in the resource, in an easily understandable manner, for technical and non-technical users.\",\n" +
                "          \"description_i18n\":{\n" +
                "            \"en\":\"Overview of the main characteristics of the resource and summary of the information contained in the resource, in an easily understandable manner, for technical and non-technical users.\"\n" +
                "          },\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"statisticalConceptsDefinitions\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"title\":\"Statistical concepts / definitions\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Statistical concepts / definitions\"\n" +
                "          },\n" +
                "          \"description\":\"Definitions of the statistical concepts under measure (i.e. the statistical domain) and the main variables provided. The considered types of variables (e.g. raw figures, annual growth rates, index, ow or stock data, ...) should be defined and described in accordance with internationally accepted statistical standards, guidelines, or good practices.\",\n" +
                "          \"description_i18n\":{\n" +
                "            \"en\":\"Definitions of the statistical concepts under measure (i.e. the statistical domain) and the main variables provided. The considered types of variables (e.g. raw figures, annual growth rates, index, ow or stock data, ...) should be defined and described in accordance with internationally accepted statistical standards, guidelines, or good practices.\"\n" +
                "          },\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seReferencePopulation\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"statisticalPopulation\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Statistical population\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Statistical population\"\n" +
                "              },\n" +
                "              \"description\":\"Target statistical population (one or more) the resource refers to.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Target statistical population (one or more) the resource refers to.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"statisticalUnit\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Statistical unit\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Statistical unit\"\n" +
                "              },\n" +
                "              \"description\":\"Simplest unit for which information is sought and for which statistics are ultimately compiled.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Simplest unit for which information is sought and for which statistics are ultimately compiled.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"referencePeriod\":{\n" +
                "              \"title\":\"Period of reference\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Period of reference\"\n" +
                "              },\n" +
                "              \"description\":\"Specific time periods (e.g. a day, a week, a month, a fiscal year, a calendar year or several calendar years) the statistical variables refer to.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Specific time periods (e.g. a day, a week, a month, a fiscal year, a calendar year or several calendar years) the statistical variables refer to.\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"referenceArea\":{\n" +
                "              \"title\":\"Area of reference\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Area of reference\"\n" +
                "              },\n" +
                "              \"description\":\"Type of geographical units the resource represents or refers to. Note that the spatial resolution must refer to the minimum mapping unit whose bounds are cially recognized indipendently from the measurement process of the phonomenon taken into account. Examples are: countries, administrative level 2, etc.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Type of geographical units the resource represents or refers to. Note that the spatial resolution must refer to the minimum mapping unit whose bounds are ocially recognized indipendently from the measurement process of the phonomenon taken into account. Examples are: countries, administrative level 2, etc.\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seCoverage\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"coverageSectors\":{\n" +
                "              \"title\":\"Main sector(s) (coded)\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Main sector(s) (coded)\"\n" +
                "              },\n" +
                "              \"description\":\"Sector(s) the resource refers to as specified in the selected codelist. The word 'Sector' indicates the subject area the resource refers to. These sectors can be institutional sectors, economic or other sectors (e.g. local government sector, agriculture, forestry, business services, etc.).\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Sector(s) the resource refers to as specified in the selected codelist. The word 'Sector' indicates the subject area the resource refers to. These sectors can be institutional sectors, economic or other sectors (e.g. local government sector, agriculture, forestry, business services, etc.).\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"coverageSectorsDetails\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"title\":\"Main sector(s)\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Main sector(s)\"\n" +
                "              },\n" +
                "              \"description\":\"Textual element delimiting the statistical results with regard to the main sectors covered.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Textual element delimiting the statistical results with regard to the main sectors covered.\"\n" +
                "              },\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"coverageTime\":{\n" +
                "              \"title\":\"Coverage period\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Coverage period\"\n" +
                "              },\n" +
                "              \"description\":\"Information about the time period for which data are available. It requests to report the time window of reference (reporting the starting date and the ending date) even if it presents some lacks.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Information about the time period for which data are available. It requests to report the time window of reference (reporting the starting date and the ending date) even if it presents some lacks.\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjPeriod\"\n" +
                "            },\n" +
                "            \"coverageGeographic\":{\n" +
                "              \"title\":\"Geographic extent\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Geographic extent\"\n" +
                "              },\n" +
                "              \"description\":\"Geographical coverage represented by the resource. It is highly recommended to make reference to officially recognized or easily identifiable macro-areas (e.g. South Saharan Africa, North America, OECD member countries..).\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Geographical coverage represented by the resource. It is highly recommended to make reference to officially recognized or easily identifiable macro-areas (e.g. South Saharan Africa, North America, OECD member countries..).\"\n" +
                "              },\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seCodeList\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"numberOfLevels\":{\n" +
                "              \"type\":\"integer\",\n" +
                "              \"title\":\"Number of levels\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Number of levels\"\n" +
                "              },\n" +
                "              \"description\":\"Hierarchical codelists may have several levels, information about the number of levels must be reported in order to describe the hierchy of the codelist.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Hierarchical codelists may have several levels, information about the number of levels must be reported in order to describe the hierchy of the codelist.\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"typeOfCodeList\":{\n" +
                "              \"type\":\"codelisttype\",\n" +
                "              \"title\":\"Type of codelist\",\n" +
                "              \"title_i18n\":{\n" +
                "                \"en\":\"Type of codelist\"\n" +
                "              },\n" +
                "              \"description\":\"Codelist typology. A codelist can be a simple objects basically composed by a list of couples of code and label or a more complex object whose elements present some hierarchical relationships.\",\n" +
                "              \"description_i18n\":{\n" +
                "                \"en\":\"Codelist typology. A codelist can be a simple objects basically composed by a list of couples of code and label or a more complex object whose elements present some hierarchical relationships.\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meDataQuality\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"qualityManagement\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"qualityAssessment\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"qualityAssurance\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seAccuracy\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"accuracyNonSampling\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"accuracySampling\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seDataRevision\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"revisionPolicy\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"revisionPractice\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seRelevance\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"userNeeds\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"userSatisfaction\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"completeness\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"completenessPercentage\":{\n" +
                "              \"type\":\"double\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seComparability\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"comparabilityGeographical\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"comparabilityTime\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"coherenceIntern\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seTimelinessPunctuality\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"timeliness\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"punctuality\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meMaintenance\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"maintenanceAgency\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"title\":\"Maintenance agency\",\n" +
                "          \"title_i18n\":{\n" +
                "            \"en\":\"Maintenance agency\"\n" +
                "          },\n" +
                "          \"description\":\"Organization or other expert body that maintains the resource.\",\n" +
                "          \"description_i18n\":{\n" +
                "            \"en\":\"Organization or other expert body that maintains the resource.\"\n" +
                "          },\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seUpdate\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"updateDate\":{\n" +
                "              \"type\":\"date-time\"\n" +
                "            },\n" +
                "            \"updatePeriodicity\":{\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seMetadataMaintenance\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"metadataLastCertified\":{\n" +
                "              \"type\":\"date-time\"\n" +
                "            },\n" +
                "            \"metadataLastPosted\":{\n" +
                "              \"type\":\"date-time\"\n" +
                "            },\n" +
                "            \"metadataLastUpdate\":{\n" +
                "              \"type\":\"date-time\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meReferenceSystem\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"referenceSystemIdentifier\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"referenceSystemName\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"patternProperties\":{\n" +
                "            \".{1}\":{\n" +
                "              \"type\":\"string\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"referenceSystemAuthority\":{\n" +
                "          \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "        },\n" +
                "        \"seProjection\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"projection\":{\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"projectionName\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"seProjectionParameters\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"zone\":{\n" +
                "                  \"type\":\"integer\"\n" +
                "                },\n" +
                "                \"standardParallel\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"longitudeOfCentralMeridian\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"latitudeOfProjectionOrigin\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"falseEasting\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"falseNorthing\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"falseEastingNorthingUnits\":{\n" +
                "                  \"$ref\":\"#/definitions/OjMeasure\"\n" +
                "                },\n" +
                "                \"scaleFactorAtEquator\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"heightOfProspectivePointAboveSurface\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"longitudeOfProjectionCenter\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"latitudeOfProjectionCenter\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"scaleFactorAtCenterLine\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"straightVerticalLongitudeFromPole\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"scaleFactorAtProjectionOrigin\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"seObliqueLineAzimuth\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"properties\":{\n" +
                "                    \"azimuthAngle\":{\n" +
                "                      \"type\":\"double\"\n" +
                "                    },\n" +
                "                    \"azimuthMeasurePointLongitude\":{\n" +
                "                      \"type\":\"double\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"seObliqueLinePoint\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"properties\":{\n" +
                "                    \"obliqueLineLatitude\":{\n" +
                "                      \"type\":\"double\"\n" +
                "                    },\n" +
                "                    \"obliqueLineLongitude\":{\n" +
                "                      \"type\":\"double\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seEllipsoid\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"ellipsoid\":{\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"ellipsoidName\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"seEllipsoidParameters\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"semiMajorAxis\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                },\n" +
                "                \"axisUnits\":{\n" +
                "                  \"$ref\":\"#/definitions/OjMeasure\"\n" +
                "                },\n" +
                "                \"denominatorOfFlatteringRatio\":{\n" +
                "                  \"type\":\"double\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seDatum\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"datum\":{\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"datumName\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meResourceStructure\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"seResourceDimensions\":{\n" +
                "          \"type\":\"array\",\n" +
                "          \"items\":{\n" +
                "            \"$ref\":\"#/definitions/SeResourceDimensions\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"seResourceRecords\":{\n" +
                "          \"type\":\"array\",\n" +
                "          \"items\":{\n" +
                "            \"$ref\":\"#/definitions/SeResourceRecords\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meSpatialRepresentation\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"typeOfProduct\":{\n" +
                "          \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "        },\n" +
                "        \"processing\":{\n" +
                "          \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "        },\n" +
                "        \"noDataValue\":{\n" +
                "          \"type\":\"string\"\n" +
                "        },\n" +
                "        \"seBoundingBox\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"xmin\":{\n" +
                "              \"type\":\"double\"\n" +
                "            },\n" +
                "            \"xmax\":{\n" +
                "              \"type\":\"double\"\n" +
                "            },\n" +
                "            \"ymin\":{\n" +
                "              \"type\":\"double\"\n" +
                "            },\n" +
                "            \"ymax\":{\n" +
                "              \"type\":\"double\"\n" +
                "            },\n" +
                "            \"seGridSpatialRepresentation\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"numberOfDimensions\":{\n" +
                "                  \"type\":\"integer\"\n" +
                "                },\n" +
                "                \"axisDimensionProperties\":{\n" +
                "                  \"$ref\":\"#/definitions/OjAxis\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"seVectorSpatialRepresentation\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"topologyLevel\":{\n" +
                "                  \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"meStatisticalProcessing\":{\n" +
                "      \"type\":\"object\",\n" +
                "      \"properties\":{\n" +
                "        \"seDataSource\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"sePrimaryDataCollection\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"dataCollector\":{\n" +
                "                  \"$ref\":\"#/definitions/OjResponsibleParty\"\n" +
                "                },\n" +
                "                \"typeOfCollection\":{\n" +
                "                  \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                },\n" +
                "                \"samplingProcedure\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"dataCollection\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"collectionPeriodicity\":{\n" +
                "                  \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"seSecondaryDataCollection\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"properties\":{\n" +
                "                \"originOfCollectedData\":{\n" +
                "                  \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "                },\n" +
                "                \"organization\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"rawDataDescription\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                },\n" +
                "                \"dataCollection\":{\n" +
                "                  \"type\":\"object\",\n" +
                "                  \"patternProperties\":{\n" +
                "                    \".{1}\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seDataCompilation\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"missingData\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"weights\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"aggregationProcessing\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"aggregationFormula\":{\n" +
                "              \"type\":\"string\"\n" +
                "            },\n" +
                "            \"dataAdjustment\":{\n" +
                "              \"$ref\":\"#/definitions/OjCodeList\"\n" +
                "            },\n" +
                "            \"dataAdjustmentDetails\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"indexType\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"basePeriod\":{\n" +
                "              \"type\":\"date-time\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"seDataValidation\":{\n" +
                "          \"type\":\"object\",\n" +
                "          \"properties\":{\n" +
                "            \"dataValidationIntermediate\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"dataValidationOutput\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            \"dataValidationSource\":{\n" +
                "              \"type\":\"object\",\n" +
                "              \"patternProperties\":{\n" +
                "                \".{1}\":{\n" +
                "                  \"type\":\"string\"\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jp = factory.createParser(result);

        JsonNode actualObj = mapper.readTree(jp);
        actualObj = mapper.readTree(jp);
        dataCreator = new DataCreator();
        dataCreator.initDataFromMDSD(actualObj,meIdentification);


        Map<String,Object> dataCleaned = dataCreator.getMetaDataCleaned();

    }

}
