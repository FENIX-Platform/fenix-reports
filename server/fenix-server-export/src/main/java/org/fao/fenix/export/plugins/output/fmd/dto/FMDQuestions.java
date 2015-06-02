package org.fao.fenix.export.plugins.output.fmd.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat1;
import org.fao.fenix.export.plugins.output.fmd.dto.root.Cat2;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder
public class FMDQuestions  implements Serializable {

    @JsonProperty private Cat1 cat1;
    @JsonProperty private Cat2 cat2;

    public FMDQuestions() {}

    public FMDQuestions(Cat1 cat1, Cat2 cat2) {
        this.cat1 = cat1;
        this.cat2 = cat2;
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
}
