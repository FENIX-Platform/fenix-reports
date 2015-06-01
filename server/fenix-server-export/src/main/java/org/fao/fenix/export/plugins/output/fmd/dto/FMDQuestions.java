package org.fao.fenix.export.plugins.output.fmd.dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder
public class FMDQuestions  implements Serializable {

    @JsonProperty private String ask1;
    @JsonProperty private Collection<String> ask1_1;

    public FMDQuestions() {}

    public FMDQuestions (String ask1, Collection<String> ask1_1) {
        this.ask1 = ask1;
        this.ask1_1 = ask1_1;
    }

    public Collection<String> getAsk1_1() {
        return ask1_1;
    }

    public void setAsk1_1(Collection<String> ask1_1) {
        this.ask1_1 = ask1_1;
    }

    public String getAsk1() {
        return ask1;
    }

    public void setAsk1(String ask1) {
        this.ask1 = ask1;
    }
}
