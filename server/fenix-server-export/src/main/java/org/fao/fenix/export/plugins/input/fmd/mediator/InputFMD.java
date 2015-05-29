package org.fao.fenix.export.plugins.input.fmd.mediator;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreTableData;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.plugins.output.fmd.dto.FMDQuestions;

import java.util.Map;

public class InputFMD extends Input {

    private static MDClientMediator mediator;
    private Resource resource;
    private FMDQuestions questionBean;

    @Override
    public void init(Map<String, Object> config, Resource resource) {

        this.resource = resource;
        mediator = new MDClientMediator();
        try {
            questionBean = mediator.getParsedMDSD();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CoreData getResource() {
        return new CoreTableData(resource.getMetadata(), resource.getData().iterator());
    }

    public FMDQuestions getQuestionBean() {
        return questionBean;
    }
}
