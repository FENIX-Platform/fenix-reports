package org.fao.fenix.export.plugins.output.fmd.dto.root;


import org.fao.fenix.export.plugins.output.fmd.dto.ask2.Ask2;

public class Cat2 {

    private Ask2 ask2;

    public Cat2(){}

    public Cat2(Ask2 ask2) {
        this.ask2 = ask2;
    }

    public Ask2 getAsk2() {
        return ask2;
    }

    public void setAsk2(Ask2 ask2) {
        this.ask2 = ask2;
    }
}
