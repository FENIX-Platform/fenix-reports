package org.fao.fenix.export.plugins.output.fmd.dto.root;


import java.util.Collection;

public class Cat1 {

    private String ask1;
    private Collection<String> ask1_1;

    public Cat1(){}

    public Cat1(String ask1, Collection<String> ask1_1) {
        this.ask1 = ask1;
        this.ask1_1 = ask1_1;
    }

    public String getAsk1() {
        return ask1;
    }

    public void setAsk1(String ask1) {
        this.ask1 = ask1;
    }

    public Collection<String> getAsk1_1() {
        return ask1_1;
    }

    public void setAsk1_1(Collection<String> ask1_1) {
        this.ask1_1 = ask1_1;
    }
}
