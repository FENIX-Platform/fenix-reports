package org.fao.fenix.export.plugins.input.table.bridge;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.JSONUtils;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class D3SBridge {

    private final String DEFAULT_URL = "http://fenix.fao.org";
    private final String BODY_URL = "/d3s/msd/resources/uid/";
    private final String QUERY_URL = "?dsd=true&language=EN";


    public Resource getData (Map<String, Object> config) {

        Resource<DSDDataset, Object[]> result = null;

        String prefix_url = (config.get("environment_url")!= null && !config.get("environment_url").toString().equals(""))?
                config.get("environment_url").toString(): DEFAULT_URL ;
        String url =prefix_url + BODY_URL+ config.get("uid").toString()+QUERY_URL;

        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  JSONUtils.decode(response.readEntity(String.class), Resource.class, DSDDataset.class, Object[].class);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




}
