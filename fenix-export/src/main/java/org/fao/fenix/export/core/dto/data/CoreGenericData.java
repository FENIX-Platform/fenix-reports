package org.fao.fenix.export.core.dto.data;


import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Iterator;
import java.util.Map;

public class CoreGenericData extends CoreData<Object[]> {

    private Iterator<Object[]> data;
    private Map<String, Object> dataStructure;


    public CoreGenericData() {
    }

    public CoreGenericData(Iterator<Object[]> data, Map<String, Object> dataStructure) {
        this.dataStructure = dataStructure;
        this.data = data;
    }

    public CoreGenericData( Map<String, Object> dataStructure) {
        this.dataStructure = dataStructure;
    }


    @Override
    public MeIdentification getMetadata() {
        return null;
    }

    @Override
    public Iterator<Object[]> getData() {
        return this.data;
    }

    public void setData(Iterator<Object[]> data) {
        this.data = data;
    }

    public Map<String, Object> getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(Map<String, Object> dataStructure) {
        this.dataStructure = dataStructure;
    }

}
