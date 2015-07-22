package org.fao.fenix.export.plugins.input.fmd.dataModel;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.google.common.collect.Lists;
import org.fao.fenix.commons.msd.dto.full.OjCode;
import org.fao.fenix.export.plugins.input.fmd.dataModel.utils.FMDProperty;
import org.fao.fenix.export.plugins.input.fmd.dataModel.utils.FMDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FMDDataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String OJCODE_TYPE = "OjCode";
    private final static String DEFAULT_LANG = "EN";
    private static String LANG;
    private final static String REQUIRED_FIELD = "required";
    private final static String ORDER_FIELD = "propertyOrder";
    private final static String TITLE_FIELD = "title_i18n";
    private final static String TITLE_FIELD_GENERIC = "title";

    private final static String DESCRIPTION_FIELD = "description_i18n";
    private final static String GET_LABEL = "getLabel";
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


    public void initDataFromMDSD(JsonNode mdsdNode, JsonNode fmdBeanRoot, String language) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        LANG = language;
        this.mdsd = mdsdNode;
        JsonNode fmdBean = fmdBeanRoot.get(0);

        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new TreeMap<String, Object>();

        if (fmdBean != null) {
            while (properties.hasNext()) {

                Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
                String key = mapDsdTmp.getKey();

                FMDProperty objectProperty = fillObjectProperty(Lists.newArrayList(mapDsdTmp.getValue().fields()).listIterator());
                Object returnedValue = getReturnedValueFromObject(objectProperty, fmdBean, key);

                if (returnedValue != null) {

                    if (objectProperty.getTitleToVisualize() != null) {

                        // 1) IF THERE IS A TYPE
                        if (mapDsdTmp.getValue().get(TYPE_FIELD) != null) {

                            FMDescriptor value = new FMDescriptor(key, objectProperty.getTitleToVisualize(), objectProperty.getDescription());
                            String order = getOrderFromEntity(mapDsdTmp.getValue());

                            // simple case: string type or number type
                            if (isReadyToPut(objectProperty)) {
                                this.metaDataCleaned.put(order, value.setValue(((TextNode) returnedValue).asText()));
                            } else {
                                this.metaDataCleaned.put(order, value.setValue(fillRecursive2(mapDsdTmp.getValue().fields(), returnedValue)));
                            }
                        }
                        // 2) REF TYPE
                        else if (mapDsdTmp.getValue().get(REF_FIELD) != null) {

                            JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                            String order = getOrderFromEntity(mapDsdTmp.getValue());

                            FMDescriptor tempVal = initDtoMDSD(mapDsdTmp.getValue(), key);
                            this.metaDataCleaned.put(order, tempVal.setValue(fillRecursive2(msdRef.fields(), returnedValue)));
                        }
                    }
                }
            }
        }
    }


    private Object fillRecursive2(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Map<String, Object> tempMap = new TreeMap<String, Object>();
        ListIterator<Map.Entry<String, JsonNode>> listBack = Lists.newArrayList(fields).listIterator();
        FMDProperty resultObj = fillObjectProperty(listBack);
        String type = resultObj.getType();

        if (type != null) {
            /** STRING OR NUMBER**/

            if (type.equals(STRING_TYPE) || type.equals(NUMBER_TYPE)) {
                Object valueStringType = invokeMethodByReflection(resultObj.getTitleBean(), returnedValue, false);
                if (valueStringType != null && !valueStringType.toString().equals("")) {
                    tempMap.put(resultObj.getOrder(), new FMDescriptor(resultObj.getTitleBean(), resultObj.getTitleToVisualize(), resultObj.getDescription(), valueStringType));
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
                                    new FMDescriptor(resultObj.getTitleBean(),
                                            resultObj.getTitleToVisualize(),
                                            resultObj.getDescription(),
                                            valueObjPatternType));
                        } else {
                            // TODO(FREEZED): there is not a type different from string
                            System.out.println("here!");

                        }
                    }
                }

                // 3) REF PROPERTY
                else if (resultObj.getProperties() == null && resultObj.getPatternProperties() == null && resultObj.getReference() != null) {
                    handleReferences(resultObj.getReference(), resultObj, tempMap, returnedValue, false);
                    return tempMap;
                }
            }

            /** ARRAY**/

            else if (type.equals(ARRAY_TYPE)) {

                ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                ArrayList<Object> values = (ArrayList<Object>) returnedValue;

                JsonNode items = ((ObjectNode) resultObj.getItems()).deepCopy();
                if (items.get(TYPE_FIELD) != null) {
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
                } else if (items.get(REF_FIELD) != null) {
                    String orderGlobal = getOrderFromEntity(resultObj.getOrder());

                    String ref = items.get(REF_FIELD).asText();
                    String[] refSplitted = ref.substring(2).split("/");

                    // OJCODE case
                    if (refSplitted[refSplitted.length - 1].equals(OJCODE_TYPE)) {
                        tempMap.put(orderGlobal, new FMDescriptor(
                                refSplitted[refSplitted.length - 1],
                                resultObj.getTitleToVisualize(),
                                resultObj.getDescription(),
                                fillOjCode((ArrayList<OjCode>) returnedValue)));
                    } else {
                        handleReferences(items.get(REF_FIELD).asText(), null, tempMap, returnedValue, true);
                        return tempMap;
                    }
                }
            }
        } else {
            if (resultObj.getReference() != null) {
                handleReferences(resultObj.getReference(), resultObj, tempMap, returnedValue, false);
            }
        }

        return tempMap;

    }

    private void handleReferences(String reference, FMDProperty objectReference, Map<String, Object> mapToFill, Object returnedValue, boolean isArray) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        JsonNode mdsdNode = getMdsdObjectFromReference(reference);
        if (mdsdNode.get(TYPE_FIELD) != null && mdsdNode.get(TYPE_FIELD).asText().equals(OBJECT_TYPE)) {
            if (mdsdNode.get(PROPERTIES_FIELD) != null) {
                ArrayList<Map.Entry<String, JsonNode>> itProperties = Lists.newArrayList(mdsdNode.get(PROPERTIES_FIELD).fields());

                if (isArray) {
                    ArrayList<Object> values = (ArrayList<Object>) returnedValue;
                    for (int z = 0; z < values.size(); z++) {
                        handleProperties(mapToFill, itProperties, values.get(z));
                    }
                } else {
                    if (objectReference != null) {
                        objectReference.setOrder(getOrderFromEntity(objectReference.getOrder()));
                        Map<String, Object> mapToFill2 = new TreeMap<String, Object>();
                        handleProperties(mapToFill2, itProperties, returnedValue);
                        mapToFill.put(objectReference.getOrder(), new FMDescriptor(objectReference.getTitleBean(), objectReference.getTitleToVisualize(),
                                objectReference.getDescription(), mapToFill2));
                    }

/*
                    handleProperties(mapToFill, itProperties, returnedValue);
*/
                }
            }
        } else if (mdsdNode.get(ENUM_FIELD) != null) {
            if (objectReference != null) {
                handleEnum(mapToFill, returnedValue, objectReference);
            } else {
                System.out.println("let's see");
            }
        }
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
            if (result == null) {
                result = method.invoke(instanceToUse, (Object) DEFAULT_LANG);
            }

        } else {
            result = ((ObjectNode) instanceToUse).get(fieldName);
          /*  methodString = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = instanceToUse.getClass().getMethod(methodString, null);
            result = method.invoke(instanceToUse, null);*/

        }

        return result;
    }


    private FMDescriptor initDtoMDSD(JsonNode entity, String beanName) {
        String title = (entity.get(TITLE_FIELD) != null) ? entity.get(TITLE_FIELD).get(LANG.toLowerCase()).asText() : null;
        String description = (entity.get(DESCRIPTION_FIELD) != null) ? entity.get(DESCRIPTION_FIELD).get(LANG.toLowerCase()).asText() : null;
        return new FMDescriptor(beanName, title, description);
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


    private FMDProperty fillObjectProperty(ListIterator<Map.Entry<String, JsonNode>> fields) {

        FMDProperty result = new FMDProperty();
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
                    result.setItems(tmp.getValue());
                    break;

                case TITLE_FIELD_GENERIC:
                    if (result.getTitleToVisualize() == null) {
                        result.setTitleToVisualize(tmp.getValue().asText());
                    }
                    break;
            }

        }
        return result;
    }


    private Object getReturnedValueFromObject(FMDProperty fmdProperty, Object value, String titleBean) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Object returnedValue = null;
        boolean isMap = fmdProperty.getPatternProperties() != null;

        if (isMap) {
            Object first = invokeMethodByReflection(titleBean, value, false);
            returnedValue = (first != null) ? invokeMethodByReflection(titleBean, first, true) : first;

        } else {
            returnedValue = invokeMethodByReflection(titleBean, value, isMap);
        }
        return returnedValue;
    }


    private boolean isReadyToPut(FMDProperty objectProperty) {

        return (objectProperty.getType() != null && (
                (objectProperty.getType().equals(STRING_TYPE) || objectProperty.getType().equals(NUMBER_TYPE)))) ||
                (objectProperty.getPatternProperties() != null && objectProperty.getPatternProperties().equals(STRING_TYPE));
    }

    private void handleProperties(Map<String, Object> mapToFill, ArrayList<Map.Entry<String, JsonNode>> itProperties, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (int k = 0; k < itProperties.size(); k++) {
            String titleBean = itProperties.get(k).getKey();

            FMDProperty objectProperty = fillObjectProperty(Lists.newArrayList(itProperties.get(k).getValue().fields()).listIterator());
            if (objectProperty.getTitleBean() == null) {
                objectProperty.setTitleBean(titleBean);
            }

            Object mdsdValue = getReturnedValueFromObject(objectProperty, returnedValue, titleBean);

            if (mdsdValue != null) {

                // if there is not a reference
                if (objectProperty.getReference() == null) {
                    String orderObj = getOrderFromEntity(objectProperty.getOrder());
                    if (isReadyToPut(objectProperty)) {
                        Object valueToPut  =((((ValueNode) mdsdValue).getNodeType().toString()).equals("NUMBER"))? mdsdValue: ((TextNode) mdsdValue).asText();
                        mapToFill.put(orderObj,
                                new FMDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(),valueToPut));
                    } else if (objectProperty.getType() != null &&
                            objectProperty.getType().equals(ARRAY_TYPE) &&
                            objectProperty.getItems().get(TYPE_FIELD) != null &&
                            objectProperty.getItems().get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                        ArrayNode values = ((ArrayNode) mdsdValue);
                        for (int i = 0, size = values.size(); i < size; i++) {
                            values.add(values.get(i).textValue());
                        }
                        mapToFill.put(orderObj, new FMDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), values));
                    } else {
                        mapToFill.put(orderObj, new FMDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), fillRecursive2(itProperties.get(k).getValue().fields(), mdsdValue)));
                    }
                }
                // if it is a reference
                else {
                    handleReferences(objectProperty.getReference(), objectProperty, mapToFill, mdsdValue, false);
                }
            }
        }
    }

    private ArrayList<String> fillOjCode(ArrayList<OjCode> values) {
        ArrayList<String> mapToFill = new ArrayList<String>();
        for (int h = 0; h < values.size(); h++) {
            String value = (values.get(h).getLabel() != null) ? values.get(h).getLabel().get(LANG.toUpperCase()) : "";

            if (value == null) {
                value = (values.get(h).getLabel() != null) ? values.get(h).getLabel().get(DEFAULT_LANG.toUpperCase()) : "";
            }
            mapToFill.add(values.get(h).getCode() + " - " + value);
        }
        return mapToFill;
    }

    private void handleEnum(Map<String, Object> mapToFill, Object returnedValue, FMDProperty reference) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        String value = (String) invokeEnumByReflection(returnedValue);
        String order = (reference != null) ? getOrderFromEntity(reference.getOrder()) : "2000";
        mapToFill.put(order, createMDSDDescriptor(value, reference));
    }


    private Object invokeEnumByReflection(Object instanceToUse) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object resultTemp = null;
        Object result = null;
        String methodString = null;
        String methodStringMAp = null;
        Method method = instanceToUse.getClass().getMethod(GET_LABEL, null);
        resultTemp = method.invoke(instanceToUse, null);
        // now the map


        methodStringMAp = "get";
        Method methodMap = resultTemp.getClass().getMethod(methodStringMAp, Object.class);
        result = methodMap.invoke(resultTemp, (Object) LANG);

        if (result == null) {
            result = methodMap.invoke(resultTemp, (Object) DEFAULT_LANG);
        }

        return result.toString();
    }


    private FMDescriptor createMDSDDescriptor(String value, FMDProperty property) {
        if (property != null) {
            return new FMDescriptor(property.getTitleBean(), property.getTitleToVisualize(), property.getDescription(), value);
        } else {
            return new FMDescriptor(null, null, null, value);
        }
    }


}








