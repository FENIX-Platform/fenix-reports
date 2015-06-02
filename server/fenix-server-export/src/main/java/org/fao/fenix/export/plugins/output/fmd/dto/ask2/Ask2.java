package org.fao.fenix.export.plugins.output.fmd.dto.ask2;


import org.fao.fenix.export.plugins.output.fmd.dto.ask2.ask2_1.Ask2_1;
import org.fao.fenix.export.plugins.output.fmd.dto.ask2.ask2_2.Ask2_2;

public class Ask2 {

    private Ask2_1 ask2_1;
    private Ask2_2 ask2_2;

    public Ask2(){}

    public Ask2(Ask2_1 ask2_1, Ask2_2 ask2_2) {
        this.ask2_1 = ask2_1;
        this.ask2_2 = ask2_2;
    }

    public Ask2_1 getAsk2_1() {
        return ask2_1;
    }

    public void setAsk2_1(Ask2_1 ask2_1) {
        this.ask2_1 = ask2_1;
    }

    public Ask2_2 getAsk2_2() {
        return ask2_2;
    }

    public void setAsk2_2(Ask2_2 ask2_2) {
        this.ask2_2 = ask2_2;
    }
}
