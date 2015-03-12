package org.fao.fenix.export.plugins.input.metadata.mediator;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.commons.msd.dto.data.codelist.MeIdentification;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;


public class MDClientMediator {

    public MeIdentification getMetadata (String url)  {

        MeIdentification result=null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  new ObjectMapper().readValue(response.readEntity(String.class), MeIdentification.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  result;


/*
        MeIdentification client = ProxyFactory.create(SimpleClient.class, "http://localhost:8081");


        MeIdentification client = null;

        ClientConnectionManager cm = new ThreadSafeClientConnManager();
        HttpClient httpClient = new DefaultHttpClient(cm);
        ClientExecutor executor = new ApacheHttpClient4Executor(httpClient);
        client = ProxyFactory.create(MeIdentification.class, url, executor);
        return client.toString();


        Invocation.Builder builder = (Invocation.Builder) ClientBuilder.newClient().target(url);
        return   builder.get(String.class);
*/


        //    return builder.get(MeIdentification.class);
    }



}
