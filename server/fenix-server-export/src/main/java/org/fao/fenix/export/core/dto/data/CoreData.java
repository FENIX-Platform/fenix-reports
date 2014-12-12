package org.fao.fenix.export.core.dto.data;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Iterator;

public abstract class CoreData <T> {

    public abstract MeIdentification getMetadata();
    public abstract Iterator<T> getData();
}
