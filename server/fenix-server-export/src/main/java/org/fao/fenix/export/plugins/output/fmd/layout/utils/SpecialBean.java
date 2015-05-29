package org.fao.fenix.export.plugins.output.fmd.layout.utils;


public enum SpecialBean {

    metadataLanguage,
    language,
    characterSet,
    disseminationPeriodicity,
    confidentialityStatus,
    referencePeriod,
    referenceArea,
    coverageSectors,
    coverageGeographic,
    updatePeriodicity,
    projection,
    ellipsoid,
    datum,
    typeOfProduct,
    processing,
    topologyLevel,
    typeOfCollection,
    collectionPeriodicity,
    originOfCollectedData,
    dataAdjustment;


    public static boolean isSpecialBean(String beanName) {

        for (org.fao.fenix.export.plugins.output.md.layout.utils.SpecialBean c : org.fao.fenix.export.plugins.output.md.layout.utils.SpecialBean.values()) {
            if (c.name().equals(beanName)) {
                return true;
            }
        }

        return false;
    }

    }
