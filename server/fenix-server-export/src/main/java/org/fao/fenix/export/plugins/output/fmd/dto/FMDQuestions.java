package org.fao.fenix.export.plugins.output.fmd.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat1;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat2;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat3;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat4;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder
public class FMDQuestions  implements Serializable {

    @JsonProperty private Cat1 cat1;
    @JsonProperty private Cat2 cat2;
    @JsonProperty private Cat3 cat3;
    @JsonProperty private Cat4 cat4;

    public FMDQuestions(Cat1 cat1, Cat2 cat2, Cat3 cat3, Cat4 cat4) {
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.cat4 = cat4;
    }

    public FMDQuestions() {
    }

    public Cat1 getCat1() {
        return cat1;
    }

    public void setCat1(Cat1 cat1) {
        this.cat1 = cat1;
    }

    public Cat2 getCat2() {
        return cat2;
    }

    public void setCat2(Cat2 cat2) {
        this.cat2 = cat2;
    }

    public Cat3 getCat3() {
        return cat3;
    }

    public void setCat3(Cat3 cat3) {
        this.cat3 = cat3;
    }

    public Cat4 getCat4() {
        return cat4;
    }

    public void setCat4(Cat4 cat4) {
        this.cat4 = cat4;
    }
}
