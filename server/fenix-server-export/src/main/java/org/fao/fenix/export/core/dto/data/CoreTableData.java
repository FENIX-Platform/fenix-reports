package org.fao.fenix.export.core.dto.data;


import org.fao.fenix.commons.msd.dto.data.dataset.MeIdentification;

import java.util.Iterator;

public class CoreTableData extends CoreData<Object[]> {

    private MeIdentification metadata;
    private Iterator<Object[]> data;


    public CoreTableData() {
    }

    public CoreTableData(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata, Iterator<Object[]> data) {
        this.metadata = (MeIdentification)metadata;
        this.data = data;
    }

    @Override
    public MeIdentification getMetadata() {
        return this.metadata;
    }

    @Override
    public Iterator<Object[]> getData() {
        return this.data;
    }

    public void setMetadata(MeIdentification metadata) {
        this.metadata = metadata;
    }

    public void setData(Iterator<Object[]> data) {
        this.data = data;
    }
}
