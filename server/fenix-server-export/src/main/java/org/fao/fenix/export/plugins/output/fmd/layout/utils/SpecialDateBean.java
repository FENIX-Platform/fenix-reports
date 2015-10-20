package org.fao.fenix.export.plugins.output.fmd.layout.utils;


public enum SpecialDateBean {
    to,
    from,
    creationDate;

    public static boolean isSpecialDateBean(String beanName) {

        if (beanName != null && !beanName.equals("")) {
            for (org.fao.fenix.export.plugins.output.md.layout.utils.SpecialDateBean c : org.fao.fenix.export.plugins.output.md.layout.utils.SpecialDateBean.values()) {
                if (c.name().equals(beanName)) {
                    return true;
                }
            }
        }
        return false;

    }

}
