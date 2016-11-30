package org.fao.fenix.export.plugins.output.table.utilsMetadata;


public enum Language {

    EN,
    FR,
    PT,
    ES,
    IT;


    public static boolean contains(String test) {

        for (Language c : Language.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
