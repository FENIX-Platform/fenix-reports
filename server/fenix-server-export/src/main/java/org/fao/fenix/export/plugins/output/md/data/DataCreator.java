package org.fao.fenix.export.plugins.output.md.data;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.full.OjCode;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDOProperty;
import org.fao.fenix.export.plugins.output.md.data.dto.MDSDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String OJCODE_TYPE = "OjCode";
    private final static String LANG = "EN";
    private final static String REQUIRED_FIELD = "required";
    private final static String ORDER_FIELD = "propertyOrder";
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
    private final static String DELETE_TITLE = "title";
    private final static String DELETE_DESCRIPTION = "description";
    private final static String DELETE_FORMAT = "format";

    private static int COUNTER = 1;


    public void initDataFromMDSD(JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new TreeMap<String, Object>();

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();

            System.out.println(key);

            MDSDOProperty objectProperty = fillObjectProperty(Lists.newArrayList(mapDsdTmp.getValue().fields()).listIterator());
            Object returnedValue = getReturnedValueFromObject(objectProperty, meIdentification, key);

            if (returnedValue != null) {

                if (objectProperty.getTitleToVisualize() != null) {

                    // 1) IF THERE IS A TYPE
                    if (mapDsdTmp.getValue().get(TYPE_FIELD) != null) {

                        MDSDescriptor value = new MDSDescriptor(key, objectProperty.getTitleToVisualize(), objectProperty.getDescription());
                        String order =  getOrderFromEntity(mapDsdTmp.getValue());

                        // simple case: string type or number type
                        if (isReadyToPut(objectProperty)) {
                            this.metaDataCleaned.put(order, value.setValue(returnedValue));
                        } else {
                            this.metaDataCleaned.put(order, value.setValue(fillRecursive2(mapDsdTmp.getValue().fields(), returnedValue)));
                        }
                    }
                    // 2) REF TYPE
                    else if (mapDsdTmp.getValue().get(REF_FIELD) != null) {

                        JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                        String order = getOrderFromEntity(mapDsdTmp.getValue());

                        MDSDescriptor tempVal = initDtoMDSD(mapDsdTmp.getValue(), key);
                        this.metaDataCleaned.put(order, tempVal.setValue(fillRecursive2(msdRef.fields(), returnedValue)));
                    }
                }
            }
        }
    }


    private Object fillRecursive2(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Map<String, Object> tempMap = new TreeMap<String, Object>();

        ListIterator<Map.Entry<String, JsonNode>> listBack = Lists.newArrayList(fields).listIterator();
        MDSDOProperty resultObj = fillObjectProperty(listBack);

        String type = resultObj.getType();


        /** STRING OR NUMBER**/

        if (type.equals(STRING_TYPE) || type.equals(NUMBER_TYPE)) {
            Object valueStringType = invokeMethodByReflection(resultObj.getTitleBean(), returnedValue, false);
            if (valueStringType != null) {
                tempMap.put(resultObj.getOrder(), new MDSDescriptor(resultObj.getTitleBean(), resultObj.getTitleToVisualize(), resultObj.getDescription(), valueStringType));
                return tempMap;
            }
        }

        /** OBJECT **/

        else if (type.equals(OBJECT_TYPE)) {

            // 1) PROPERTIES
            if (resultObj.getProperties() != null) {

                ArrayList<Map.Entry<String, JsonNode>> itProperties = (ArrayList<Map.Entry<String, JsonNode>>) resultObj.getProperties();

                handleProperties(tempMap, itProperties, returnedValue);

            }

            // 2) PATTERN PROPERTIES
            else if (resultObj.getProperties() == null && resultObj.getPatternProperties() != null) {


                Object valueObjPatternType = invokeMethodByReflection(resultObj.getTitleBean(), returnedValue, true);
                if (valueObjPatternType != null) {

                    if (resultObj.getPatternProperties().equals(STRING_TYPE)) {
                        String orderObj = getOrderFromEntity(resultObj.getOrder());
                        tempMap.put(
                                orderObj,
                                new MDSDescriptor(resultObj.getTitleBean(),
                                        resultObj.getTitleToVisualize(),
                                        resultObj.getDescription(),
                                        valueObjPatternType)
                        );
                    } else {
                        // TODO(FREEZED): there is not a type different from string
                    }
                }
            }

            // 3) REF PROPERTY
            else if (resultObj.getProperties() == null && resultObj.getPatternProperties() == null && resultObj.getReference() != null) {

                JsonNode mdsdNode = getMdsdObjectFromReference(resultObj.getReference());
                if (mdsdNode.get(TYPE_FIELD).asText().equals(OBJECT_TYPE)) {
                    if (mdsdNode.get(PROPERTIES_FIELD) != null) {
                        ArrayList<Map.Entry<String, JsonNode>> itProperties = Lists.newArrayList(mdsdNode.get(PROPERTIES_FIELD).fields());
                        handleProperties(tempMap, itProperties, returnedValue);
                        return tempMap;
                    }
                } else if (mdsdNode.get(ENUM_FIELD) != null) {


                    // TODO: handle enumeration
                }
            }
        }

        /** ARRAY**/

        else if (type.equals(ARRAY_TYPE)) {

            //TODO

            ArrayList<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
            ArrayList<Object> values = (ArrayList<Object>)returnedValue;

            JsonNode items = ((ObjectNode) resultObj.getItems()).deepCopy();
            if(items.get(TYPE_FIELD) != null) {
                if (items.get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                    String orderGlobal = getOrderFromEntity(resultObj.getOrder());

                    for (int arrCounter = 0; arrCounter < values.size(); arrCounter++) {
                        Map<String, Object> tmp = new TreeMap<String, Object>();
                        String order = getOrderFromEntity(resultObj.getOrder());
                        tmp.put(order, values.get(arrCounter));
                        result.add(tmp);
                    }
                    tempMap.put(orderGlobal, result);
                }
            }
            else if(items.get(REF_FIELD)!= null){
                String orderGlobal = getOrderFromEntity(resultObj.getOrder());

                String ref = items.get(REF_FIELD).asText();
                String[] refSplitted = ref.substring(2).split("/");

                // OJCODE case
                if(refSplitted[refSplitted.length-1].equals(OJCODE_TYPE)){
                    tempMap.put(orderGlobal,new MDSDescriptor(
                            refSplitted[refSplitted.length-1],
                            "Code(S)",
                            null,
                            fillOjCode((ArrayList<OjCode>) returnedValue)));
                }else{

                }
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

        String titleBean = path[path.length - 1];
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
                    MDSDescriptor tempObject = initDtoMDSD(tempProperty.getValue(), tempProperty.getKey());
                    tempMap.put(getOrderFromEntity(tempProperty.getValue()), tempObject.setValue(fillRecursive2(tempProperty.getValue().fields(), resultInvokation)));
                }

            }
        } else if (refMdsd.get(ENUM_FIELD) != null) {

            System.out.println("TODO!");

        }

    }


    private MDSDescriptor initDtoMDSD(String title, String description, String beanName) {
        return new MDSDescriptor(beanName, title, description);
    }


    private MDSDescriptor initDtoMDSD(JsonNode entity, String beanName) {
        String title = (entity.get(TITLE_FIELD) != null) ? entity.get(TITLE_FIELD).get(LANG.toLowerCase()).asText() : null;
        String description = (entity.get(DESCRIPTION_FIELD) != null) ? entity.get(DESCRIPTION_FIELD).get(LANG.toLowerCase()).asText() : null;
        return new MDSDescriptor(beanName, title, description);
    }


    private String getOrderFromEntity(JsonNode entity) {

        String result = (entity.get(ORDER_FIELD) != null) ? entity.get(ORDER_FIELD).asText() : 10000 + COUNTER + "";
        COUNTER++;

        return result;
    }

    private String getOrderFromEntity(String entity) {

        String result = (entity != null) ? entity : 10000 + COUNTER + "";
        COUNTER++;

        return result;
    }


    private boolean isFieldRequired(JsonNode entity) {
        return ((entity.get(REQUIRED_FIELD) != null) && (entity.get(REQUIRED_FIELD).asBoolean()));
    }


    private Object invokeMethodEnumType(Object bean) {
        return null;
    }


    private MDSDescriptor findTitleAndDescriptionOnObj(ListIterator<Map.Entry<String, JsonNode>> fields) {
        String titleRes = null;
        String descRes = null;
        MDSDescriptor result = new MDSDescriptor();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> tmp = fields.next();
            if (tmp.getKey().equals(TITLE_FIELD) && !(tmp.getValue().get(LANG.toLowerCase()).asText().equals(null))) {
                titleRes = tmp.getValue().get(LANG.toLowerCase()).asText();
            } else if (tmp.getKey().equals(DESCRIPTION_FIELD) && !(tmp.getValue().get(LANG.toLowerCase()).asText().equals(null))) {
                descRes = tmp.getValue().get(LANG.toLowerCase()).asText();
            }
        }
        if (titleRes != null && descRes != null) {
            result.setTitleToVisualize(titleRes);
            result.setDescription(descRes);
        } else if (titleRes != null && descRes == null) {
            result.setTitleToVisualize(titleRes);
        }

        return result;
    }


    private MDSDOProperty fillObjectProperty(ListIterator<Map.Entry<String, JsonNode>> fields) {

        MDSDOProperty result = new MDSDOProperty();
        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> tmp = fields.next();

            switch (tmp.getKey()) {

                case TITLE_FIELD:
                    result.setTitleToVisualize(tmp.getValue().get(LANG.toLowerCase()).asText());
                    break;

                case DESCRIPTION_FIELD:
                    result.setDescription(tmp.getValue().get(LANG.toLowerCase()).asText());
                    break;

                case PATTERN_PROPERTIES_FIELD:
                    result.setPatternProperties(tmp.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(TYPE_FIELD).asText());
                    break;

                case ORDER_FIELD:
                    result.setOrder(tmp.getValue().asText());
                    break;

                case TYPE_FIELD:
                    result.setType(tmp.getValue().asText());
                    break;

                case PROPERTIES_FIELD:
                    result.setProperties(Lists.newArrayList(tmp.getValue().fields()));
                    break;

                case REF_FIELD:
                    result.setReference(tmp.getValue().asText());
                    break;

                case REQUIRED_FIELD:
                    result.setRequired(tmp.getValue().asText());
                    break;

                case ITEMS_FIELD:
                    System.out.println("here");
                    result.setItems(tmp.getValue());
                    break;
            }

        }
        return result;
    }


    private Object getReturnedValueFromObject(MDSDOProperty mdsdoProperty, Object value, String titleBean) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Object returnedValue = null;
        boolean isMap = mdsdoProperty.getPatternProperties() != null;

        if (isMap) {
            Object first = invokeMethodByReflection(titleBean, value, false);
            returnedValue = (first != null) ? invokeMethodByReflection(titleBean, first, true) : first;

        } else {
            returnedValue = invokeMethodByReflection(titleBean, value, isMap);
        }
        return returnedValue;
    }


    private boolean isReadyToPut(MDSDOProperty objectProperty) {

        return objectProperty.getType().equals(STRING_TYPE) ||
                objectProperty.getType().equals(NUMBER_TYPE) ||
                (objectProperty.getPatternProperties() != null && objectProperty.getPatternProperties().equals(STRING_TYPE));
    }

    private void handleProperties(Map<String, Object> mapToFill, ArrayList<Map.Entry<String, JsonNode>> itProperties, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (int k = 0; k < itProperties.size(); k++) {
            String titleBean = itProperties.get(k).getKey();

            MDSDOProperty objectProperty = fillObjectProperty(Lists.newArrayList(itProperties.get(k).getValue().fields()).listIterator());
            Object mdsdValue = getReturnedValueFromObject(objectProperty, returnedValue, titleBean);

            if (mdsdValue != null) {
                String orderObj = getOrderFromEntity(objectProperty.getOrder());
                if (isReadyToPut(objectProperty)) {
                    mapToFill.put(orderObj,
                            new MDSDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), mdsdValue));
                } else {
                    mapToFill.put(orderObj, new MDSDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), fillRecursive2(itProperties.get(k).getValue().fields(), mdsdValue)));
                }
            }
        }
    }

    private ArrayList<String>  fillOjCode (ArrayList<OjCode> values ) {
        ArrayList<String> mapToFill = new ArrayList<String>();
        for (int h = 0; h < values.size(); h++) {
            mapToFill.add(values.get(h).getCode() + " - " + values.get(h).getLabel().get(LANG.toUpperCase()));
        }
        return mapToFill;
    }
}








