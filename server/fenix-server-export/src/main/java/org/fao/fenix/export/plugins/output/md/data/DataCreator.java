package org.fao.fenix.export.plugins.output.md.data;


import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String LANG = "EN";
    private final static String TITLE_FIELD = "title_i18n";
    private final static String REF_FIELD = "$ref";
    private final static String TYPE_FIELD = "type";
    private final static String PATTERN_PROPERTIES_FIELD = "patternProperties";
    private final static String FOLLOW_PATTERN_PROPERTIES = ".{1,}";
    private final static String ENUM_FIELD = "enum";
    private final static String PROPERTIES_FIELD = "properties";
    private final static String STRING_TYPE = "string";
    private final static String NUMBER_TYPE = "number";
    private final static String ARRAY_TYPE = "array";
    private final static String OBJECT_TYPE = "object";
    private final static String ITEMS_FIELD = "items";


    public void initDataFromMDSD(JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new HashMap<String, Object>();

        int i = 0;

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();

            // TODO: to delete
            boolean uncoveredFields = key.equals("contacts") || key.equals("characterSet") || key.equals("meContent") ||
                    key.equals("metadataStandardName") || key.equals("metadataStandardVersion");
            Object returnedValue = invokeMethodByReflection(key, meIdentification, false);

            if (returnedValue != null) {
                System.out.println(key);
                if (key.equals("contacts")) {
                    System.out.println("asdsad");
                }
                System.out.println("");
            }

            /*
            String methodString = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method method = MeIdentification.class.getMethod(methodString, null);
            Object returnedValue = method.invoke(meIdentification, null);*/

            if (returnedValue != null) {
                JsonNode tempField = mapDsdTmp.getValue().get(TITLE_FIELD);
                // if has title
                if (uncoveredFields || tempField != null && tempField.get(LANG.toLowerCase()) != null) {

                    // 1) CASE TYPE
                    if (mapDsdTmp.getValue().get(TYPE_FIELD) != null) {


                        String typeField = mapDsdTmp.getValue().get(TYPE_FIELD).asText();
                        String keySimple = (uncoveredFields) ? key : tempField.get(LANG.toLowerCase()).asText();
                        // simple case: string type or number type
                        if (typeField.equals(STRING_TYPE) || typeField.equals(NUMBER_TYPE)) {
                            // TODO: remove
                            this.metaDataCleaned.put(keySimple, returnedValue);
                        } else {
                            this.metaDataCleaned.put(keySimple, fillRecursive(mapDsdTmp.getValue().fields(), returnedValue));
                        }
                    }
                    // 2) REF TYPE
                    else if (mapDsdTmp.getValue().get(REF_FIELD) != null) {
                        JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                        String keySimple = (uncoveredFields) ? key : tempField.get(LANG.toLowerCase()).asText();
                        this.metaDataCleaned.put(keySimple, fillRecursive(msdRef.fields(), returnedValue));
                    }

                }
                   /* if (key.equals("contacts") || mapDsdTmp.getValue().get(TYPE_FIELD) != null && mapDsdTmp.getValue().get(TYPE_FIELD).asText().equals(STRING_TYPE)  ) {
                        if (key.equals("contacts")) {
                            this.metaDataCleaned.put("Contacts", returnedValue);
                        } else {
                            this.metaDataCleaned.put(tempField.get(LANG.toLowerCase()).asText(), returnedValue);
                        }
                    }else {
                        if (mapDsdTmp.getValue().get(REF_FIELD) != null) {
                            JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                            this.metaDataCleaned.put(tempField.get(LANG.toLowerCase()).asText(), fillRecursive(msdRef.fields(), returnedValue));
                        }
                        if (mapDsdTmp.getValue().get() != null) {
                            JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                            this.metaDataCleaned.put(tempField.get(LANG.toLowerCase()).asText(), fillRecursive(msdRef.fields(), returnedValue));
                        }
                    }
                }*/
            }
        }
        System.out.println("finish!");

    }


    private Object fillRecursive(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        HashMap<String, Object> tempMap = new HashMap<String, Object>();


        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> mapFieldDSD = fields.next();
            String key = mapFieldDSD.getKey();

            // different from object reference

            switch (key) {

                case PATTERN_PROPERTIES_FIELD:

                    tempMap.put(LANG, ((Map) (returnedValue)).get(LANG));

                    break;

                case REF_FIELD:

                    String[] path = mapFieldDSD.getValue().asText().substring(2).split("/");

                    String url = null;
                    JsonNode res = null;
                    JsonNode refMdsd = mdsd;
                    for (int i = 0; i < path.length; i++) {
                        res = refMdsd.get(path[i]);
                        refMdsd = res;
                    }

                    if (refMdsd.get(PROPERTIES_FIELD) != null) {
                        Iterator<Map.Entry<String, JsonNode>> properties = refMdsd.get(PROPERTIES_FIELD).fields();
                        while (properties.hasNext()) {
                            Map.Entry<String, JsonNode> tempProperty = properties.next();
                            Object resultInvokation = invokeMethodByReflection(tempProperty.getKey(), returnedValue, false);
                            if (resultInvokation != null) {
                                tempMap.put(tempProperty.getKey(), fillRecursive(tempProperty.getValue().fields(), resultInvokation));
                            }

                        }
                    }
                    break;


                case TYPE_FIELD:


                    // TODO: add type 'MAP' as standard
                    switch (mapFieldDSD.getValue().asText()) {

                        case STRING_TYPE:
                        case NUMBER_TYPE:

                            tempMap.put(LANG, returnedValue);

                            break;

                        case ARRAY_TYPE:
                            /*
                             * Type Array can have as sibling a items field
                             */

                            System.out.println("array");
                            ArrayList<Object> result = null;
                            String arrayKey = mapFieldDSD.getKey();

                            while (fields.hasNext()) {
                                mapFieldDSD = fields.next();
                                if (mapFieldDSD.getKey().equals(ITEMS_FIELD)) {
                                    for (int i = 0; i < ((ArrayList) returnedValue).size(); i++) {
                                        result = new ArrayList<Object>();
                                        result.add(fillRecursive(mapFieldDSD.getValue().fields(), ((ArrayList) returnedValue).get(i)));
                                    }
                                    return result;

                                }
                            }


                            break;

                        case OBJECT_TYPE:
                            /*
                             * Type Object can have as sibling a properties field or patternProperties field
                             */

                            if (fields.hasNext()) {
                                mapFieldDSD = fields.next();
                            } else {
                                break;
                            }

                            String keyObj = mapFieldDSD.getKey();
                            String titleObj = (mapFieldDSD.getValue().get(TITLE_FIELD) != null) ? mapFieldDSD.getValue().get(TITLE_FIELD).get(LANG.toLowerCase()).asText() :
                                    "objectTitleProvv" + 10000 * Math.random() + "";
                            if (keyObj.equals(PROPERTIES_FIELD)) {

                                Iterator<Map.Entry<String, JsonNode>> tempIt = mapFieldDSD.getValue().fields();
                                while (tempIt.hasNext()) {
                                    Map.Entry<String, JsonNode> objectValue = tempIt.next();
                                    Object msdValue = invokeMethodByReflection(objectValue.getKey(), returnedValue, false);
                                    if (msdValue != null)
                                        tempMap.put(objectValue.getKey(), fillRecursive(objectValue.getValue().fields(), msdValue));
                                }

                            } else if (keyObj.equals(REF_FIELD)) {

                                String[] pathRefs = mapFieldDSD.getValue().asText().substring(2).split("/");

                                Object returnedValueRef = invokeMethodByReflection(pathRefs[pathRefs.length - 1], returnedValue, false);
                                if (returnedValueRef != null) {

                                }


                            } else if (keyObj.equals(PATTERN_PROPERTIES_FIELD)) {

                                if (mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES) != null && mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(TYPE_FIELD).asText().equals(STRING_TYPE)) {

                                    return invokeMethodByReflection(null, returnedValue, true);
                                }

                            }
                            break;

                    }

                    break;

                case ENUM_FIELD:

                    System.out.println("enum");

                    break;

             /*   default:

                    Object resultInvoked = invokeMethodByReflection(key, returnedValue, false);
                    // if yes,  continue
                    if (resultInvoked != null && mapFieldDSD.getValue().get(TYPE_FIELD) != null) {

                        if (mapFieldDSD.getValue().get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                            return resultInvoked;
                        } else {
                            fillRecursive(mapFieldDSD.getValue().fields(), resultInvoked);
                        }
                    }

                    break;
*/

            }
        }


        return tempMap;

    }


    public Map<String, Object> getMetaDataCleaned() {
        return metaDataCleaned;
    }


    private JsonNode getMdsdObjectFromReference(String ref) {

        String[] path = ref.substring(2).split("/");

        JsonNode tmp = null;
        JsonNode referenceMdsd = mdsd;
        for (int i = 0; i < path.length; i++) {
            tmp = referenceMdsd.get(path[i]);
            referenceMdsd = tmp;
        }

        return referenceMdsd;
    }


    private Object invokeMethodByReflection(String fieldName, Object instanceToUse, boolean isMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object result = null;
        String methodString = null;
        if (isMap) {
            methodString = "get";
            Method method = instanceToUse.getClass().getMethod(methodString, Object.class);
            result = method.invoke(instanceToUse, (Object) LANG);

        } else {
            methodString = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = instanceToUse.getClass().getMethod(methodString, null);
            result = method.invoke(instanceToUse, null);
        }

        return result;
    }


}








