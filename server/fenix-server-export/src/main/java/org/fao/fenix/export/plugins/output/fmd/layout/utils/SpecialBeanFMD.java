package org.fao.fenix.export.plugins.output.fmd.layout.utils;


public enum SpecialBeanFMD {

     ask1_1;


    public static boolean isSpecialBean(String beanName) {

        for (SpecialBeanFMD c: SpecialBeanFMD.values()) {
            if (c.name().equals(beanName)) {
                return true;
            }
        }

        return false;
    }

    }
