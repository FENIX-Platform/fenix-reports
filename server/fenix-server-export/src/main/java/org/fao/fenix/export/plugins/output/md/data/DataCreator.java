package org.fao.fenix.export.plugins.output.md.data;


import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String LANG = "EN";
    private final static String REQUIRED_FIELD = "required";
    private final static String ORDER_FIELD = "order";
    private final static String TITLE_FIELD = "title_i18n";
    private final static String DESCRIPTION_FIELD = "description_i18n";
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
    private static int COUNTER = 1;


    public void initDataFromMDSD(JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new TreeMap<String, Object>();

        int i = 0;

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();

            // TODO: to delete
            boolean uncoveredFields = key.equals("contacts") || key.equals("characterSet") || key.equals("meContent") ||
                    key.equals("metadataStandardName") || key.equals("metadataStandardVersion") ||  key.equals("title");

            if(key.equals("title")){
                System.out.println("stop!");
            }
            Object returnedValue = invokeMethodByReflection(key, meIdentification, false);
            if (returnedValue != null) {
                JsonNode tempField = mapDsdTmp.getValue().get(TITLE_FIELD);
                JsonNode tempDescription = mapDsdTmp.getValue().get(DESCRIPTION_FIELD);
                // if has title (TODO: also need to  be required and needs to have an order)
                if (uncoveredFields || tempField != null && tempField.get(LANG.toLowerCase()) != null) {

                    /**
                     *  String order = mapDsdTmp.getValue().get(ORDER_FIELD).asText();
                     */

                    // 1) IF THERE IS A TYPE
                    if (mapDsdTmp.getValue().get(TYPE_FIELD) != null) {

                        String typeField = mapDsdTmp.getValue().get(TYPE_FIELD).asText();
                        MDSDescriptor value =initMDSD(mapDsdTmp.getValue(),key);
                        String order = getOrderFromEntity(mapDsdTmp.getValue());

                        String titleSimple = (uncoveredFields) ? key : tempField.get(LANG.toLowerCase()).asText();
                        // simple case: string type or number type
                        if (typeField.equals(STRING_TYPE) || typeField.equals(NUMBER_TYPE)) {
                            value.setValue(returnedValue);
                            // TODO: remove
                            this.metaDataCleaned.put(order, value);
                        } else {
                            this.metaDataCleaned.put(order,value.setValue(fillRecursive(mapDsdTmp.getValue().fields(), returnedValue)));
                        }
                    }
                    // 2) REF TYPE
                    else if (mapDsdTmp.getValue().get(REF_FIELD) != null) {

                        JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                        String order = getOrderFromEntity(mapDsdTmp.getValue());

                        MDSDescriptor tempVal = initMDSD(mapDsdTmp.getValue(),key);
                        this.metaDataCleaned.put(order, tempVal.setValue(fillRecursive(msdRef.fields(), returnedValue)));
                    }
                }
            }
        }
    }


    private Object fillRecursive(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Map<String, Object> tempMap = new TreeMap<String, Object>();

        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> mapFieldDSD = fields.next();
            String key = mapFieldDSD.getKey();

            // different from object reference

            switch (key) {

                case PATTERN_PROPERTIES_FIELD:

                    JsonNode order = mapFieldDSD.getValue().get(ORDER_FIELD);
                    if(order!= null) {
                        String orderString = order.get(LANG).asText();
                    }

                    if(mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES) != null && mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(FOLLOW_PATTERN_PROPERTIES) != null){
                        JsonNode temp = mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(FOLLOW_PATTERN_PROPERTIES);

                        if(temp.get(TYPE_FIELD)!= null && temp.get(TYPE_FIELD).asText().equals(STRING_TYPE)){
                            return ((LinkedHashMap) returnedValue).get("EN");
                        }else if(temp.get(REF_FIELD)!= null || temp.get(TYPE_FIELD).asText().equals(OBJECT_TYPE)){
                            System.out.println("vediamo!");
                        }
                    }


                    break;

                case REF_FIELD:

                    processReferenceField(mapFieldDSD, returnedValue, (TreeMap<String, Object>) tempMap);

                    break;


                case TYPE_FIELD:


                    // TODO: add type 'MAP' as standard
                    switch (mapFieldDSD.getValue().asText()) {

                        case STRING_TYPE:
                        case NUMBER_TYPE:

                            return returnedValue;


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
                                    result = new ArrayList<Object>();
                                    for (int i = 0; i < ((ArrayList) returnedValue).size(); i++) {

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
                            String description = (mapFieldDSD.getValue().get(DESCRIPTION_FIELD) != null) ? mapFieldDSD.getValue().get(DESCRIPTION_FIELD).get(LANG.toLowerCase()).asText() :
                                    "objectTitleProvv" + 10000 * Math.random() + "";

                            // Three cases: properties_field, ref_field and pattern_properties
                            // First one: properties
                            if (keyObj.equals(PROPERTIES_FIELD ) && titleObj!= null) {
                                Iterator<Map.Entry<String, JsonNode>> tempIt = mapFieldDSD.getValue().fields();
                                while (tempIt.hasNext()) {
                                    Map.Entry<String, JsonNode> objectValue = tempIt.next();
                                    Object msdValue = invokeMethodByReflection(objectValue.getKey(), returnedValue, false);
                                    if (msdValue != null) {
                                        MDSDescriptor objectTypeMdsDescriptor = initMDSD(objectValue.getValue(),objectValue.getKey());
                                         String orderObject = getOrderFromEntity(objectValue.getValue());
                                        tempMap.put(orderObject, objectTypeMdsDescriptor.setValue(fillRecursive(objectValue.getValue().fields(), msdValue)));
                                    }
                                }

                            } else if (keyObj.equals(REF_FIELD)) {

                                String[] pathRefs = mapFieldDSD.getValue().asText().substring(2).split("/");

                                Object returnedValueRef = invokeMethodByReflection(pathRefs[pathRefs.length - 1], returnedValue, false);
                                if (returnedValueRef != null) {

                                }


                            } else if (keyObj.equals(PATTERN_PROPERTIES_FIELD)) {

                                if (mapFieldDSD.getValue().get(".{1}") != null && mapFieldDSD.getValue().get(".{1}").get(TYPE_FIELD).asText().equals(STRING_TYPE)) {

                                    return invokeMethodByReflection(null, returnedValue, true);
                                }

                            }
                            break;
                    }

                    break;

                case ENUM_FIELD:

                    System.out.println("enum");

                    break;

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


    private void processReferenceField(Map.Entry<String, JsonNode> mapFieldDSD, Object returnedValue, TreeMap<String, Object> tempMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String[] path = mapFieldDSD.getValue().asText().substring(2).split("/");

        String titleBean = path[path.length-1];
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
                    MDSDescriptor tempObject = initMDSD(tempProperty.getValue(),tempProperty.getKey());
                    tempMap.put(getOrderFromEntity(tempProperty.getValue()), tempObject.setValue(fillRecursive(tempProperty.getValue().fields(), resultInvokation)));
                }

            }
        }

    }


    private MDSDescriptor initMDSD (JsonNode entity, String beanName){
        String title = (entity.get(TITLE_FIELD)!= null)? entity.get(TITLE_FIELD).get(LANG.toLowerCase()).asText(): null;
        String description = (entity.get(DESCRIPTION_FIELD)!= null)? entity.get(DESCRIPTION_FIELD).get(LANG.toLowerCase()).asText(): null;
        return  new MDSDescriptor(beanName,title,description);
    }

    private String getOrderFromEntity (JsonNode entity){

        String result = (entity.get(ORDER_FIELD)!= null)? entity.get(ORDER_FIELD).asText(): 10000+COUNTER+"";
        COUNTER++;

        return result;
    }


    private boolean isFieldRequired (JsonNode entity) {
        return ((entity.get(REQUIRED_FIELD)!= null) && (entity.get(REQUIRED_FIELD).asBoolean()));
    }
}









