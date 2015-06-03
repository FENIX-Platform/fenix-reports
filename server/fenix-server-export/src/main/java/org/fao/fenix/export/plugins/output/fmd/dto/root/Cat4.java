package org.fao.fenix.export.plugins.output.fmd.dto.root;

import org.fao.fenix.export.plugins.output.fmd.dto.ask4.Ask4;

public class Cat4 {

    private Ask4 ask4;

    public Cat4(Ask4 ask4) {
        this.ask4 = ask4;
    }

    public Cat4() {
    }

    public Ask4 getAsk4() {
        return ask4;
    }

    public void setAsk4(Ask4 ask4) {
        this.ask4 = ask4;
    }
}
