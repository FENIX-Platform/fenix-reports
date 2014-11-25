/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fenix.faostat.gateway;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.collections.IteratorUtils;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.record.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.hssf.usermodel.*;
/*import org.apache.poi.xssf.usermodel.XSSFPivotTable;
 import org.apache.poi.xssf.usermodel.XSSFRow;
 import org.apache.poi.xssf.usermodel.XSSFSheet;
 import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 */
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author joyeuxroccaserra
 */
@Path("/ExportPOI")
public class ExportPOI {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ExportPOI
     */
    public ExportPOI() {
    }

    /**
     * Retrieves representation of an instance of
     * org.fao.fenix.faostat.gateway.ExportPOI
     *
     * @return an instance of java.lang.String
     */
    @POST
    @Produces("application/vnd.ms-excel")
    public Response getHtml(
            @FormParam("myJson") String myJson, //  @PathParam("myJson") String myJson
            @FormParam("myFlags") String myFlags
            ) throws IOException {



        //String myJson2='{"<span class=\"ordre\">009</span>Armenia||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[463.515,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[476.283,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[499.264,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[519.341,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[544.396,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[546.36,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[564.178,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[592.082,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">009</span>Armenia||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[463.515,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[476.283,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[499.264,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[519.341,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[544.396,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[546.36,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[564.178,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[592.082,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">001</span>Afghanistan||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[4787.294,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[4053.238,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[4616.57,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[4740.636,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[4732.909,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[4806.951,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[4801.12,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[4594.889,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">001</span>Afghanistan||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[4787.294,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[4053.238,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[4616.57,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[4740.636,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[4732.909,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[4806.951,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[4801.12,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[4594.889,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">002</span>Albania||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[723.368,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[703.325,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[666.663,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[689.495,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[648.853,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[638.512,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[633.109,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[599.062,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">002</span>Albania||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[723.368,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[703.325,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[666.663,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[689.495,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[648.853,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[638.512,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[633.109,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[599.062,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">003</span>Algeria||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[3713.987,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[3711.115,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[3770.088,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[3778.617,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[3909.626,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[3977.336,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[4095.713,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[4185.47,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">003</span>Algeria||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[3713.987,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[3711.115,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[3770.088,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[3778.617,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[3909.626,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[3977.336,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[4095.713,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[4185.47,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">004</span>American Samoa||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[3.966,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[3.97,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">004</span>American Samoa||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[3.963,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[3.966,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[3.97,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">005</span>Andorra||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">005</span>Andorra||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[0,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">006</span>Angola||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2732.512,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2763.191,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2767.319,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2767.492,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2510.385,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2767.839,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2798.197,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2832.503,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">006</span>Angola||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2732.512,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2763.191,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2767.319,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2767.492,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2510.385,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2767.839,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2798.197,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2832.503,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">007</span>Antigua and Barbuda||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[20.233,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[20.81,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[21.107,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[21.448,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[21.877,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[21.889,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[21.889,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[22.017,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">007</span>Antigua and Barbuda||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[20.233,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[20.81,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[21.107,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[21.448,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[21.877,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[21.889,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[21.889,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[22.017,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">008</span>Argentina||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[40306.591,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[40368.596,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[39696.008,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[41395.903,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[41343.226,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[41086.223,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[41503.585,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[41615.7,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">008</span>Argentina||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[40306.591,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[40368.596,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[39696.008,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[41395.903,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[41343.226,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[41086.223,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[41503.585,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[41615.7,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">010</span>Australia||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[47195.422,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[46708.829,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[46520.2,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[44338.99,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[45462.688,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[45924.181,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[46111.645,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[45172.817,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">010</span>Australia||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[47195.422,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[46708.829,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[46520.2,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[44338.99,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[45462.688,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[45924.181,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[46111.645,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[45172.817,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">011</span>Austria||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2238.633,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2203.098,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2169.807,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2134.772,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2100.988,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2079.93,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2074.845,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2068.667,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">011</span>Austria||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2238.633,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2203.098,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2169.807,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2134.772,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2100.988,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2079.93,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2074.845,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2068.667,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">013</span>Bahamas||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[6.744,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[6.95,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[6.903,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[7.703,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">013</span>Bahamas||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[6.744,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[6.95,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[6.903,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[7.703,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[7.847,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">012</span>Azerbaijan||<span class=\"ordre\">1</span>Gross Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2198.61,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2299.159,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2441.976,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2576.24,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2674.829,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2747.886,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2821.918,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2883.904,"USD million","&nbsp;"],"label":"Sum of Value"}},"<span class=\"ordre\">012</span>Azerbaijan||<span class=\"ordre\">3</span>Net Capital Stock (constant 2005 prices)||<span class=\"ordre\">2</span>Livestock (Fixed Assets)":{"2000":{"sum":[2198.61,"USD million","&nbsp;"],"label":"Sum of Value"},"2001":{"sum":[2299.159,"USD million","&nbsp;"],"label":"Sum of Value"},"2002":{"sum":[2441.976,"USD million","&nbsp;"],"label":"Sum of Value"},"2003":{"sum":[2576.24,"USD million","&nbsp;"],"label":"Sum of Value"},"2004":{"sum":[2674.829,"USD million","&nbsp;"],"label":"Sum of Value"},"2005":{"sum":[2747.886,"USD million","&nbsp;"],"label":"Sum of Value"},"2006":{"sum":[2821.918,"USD million","&nbsp;"],"label":"Sum of Value"},"2007":{"sum":[2883.904,"USD million","&nbsp;"],"label":"Sum of Value"}}}';



        String patternStr = "<span class=\"ordre\">.*</span><table class=\"innerCol\"><th>(.*)</th><th>(.*)</th></table>";
        Pattern pattern = Pattern.compile(patternStr);






        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
  /*      System.out.println("myJson");
        System.out.println(myJson);*/
        //  XSSFSheet sheet2 = wb.createSheet("sheet2");

        /*BEGIN JACKSON*/

        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(new String(myJson.getBytes(), Charset.forName("UTF-8")).replaceAll("\\?", ""));

        ObjectMapper mapperFlag = new ObjectMapper();

        JsonNode nodeFlag = mapperFlag.readTree(new String(myFlags.getBytes(), Charset.forName("UTF-8")).replaceAll("\\?", ""));

        int i = 0;
        String head[];
        String header[];
        String Oldhead[] = null;
        int headLength = 0;
        Iterator<Entry<String, JsonNode>> nodeIterator = node.get("data").getFields();
        String swUnit=node.get("swUnit").asText();
        String swFlag=node.get("swFlag").asText();
        int swflagindex=1;
   if(swUnit.equals( "true")){swflagindex=2;}
        
        /* List nodeList=IteratorUtils.toList(nodeIterator);
         System.out.println("INANA");
         //Collections.sort(nodeList);
         System.out.println(nodeList);
         for(Object a:nodeList )
         {System.out.println(a);}*/
      /*  Iterator<Entry<String, JsonNode>> headerIterator = node.get("header").getFields();
         Iterator<Entry<String, JsonNode>> colsIterator = node.get("cols").getFields();
*/


        while (nodeIterator.hasNext()) {
         
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();


            head = entry.getKey().split("\\|\\|");
            headLength = head.length;


            if (i == 0) {
                //   Matcher matcherinit = pattern.matcher(head[0]);
                HSSFRow row = sheet.createRow(0);
                int iii = 0;
                //boolean bCode=matcherinit.find();
                /*for (int ii = 0; ii < headLength; ii++) {
                    row.createCell((short) iii).setCellValue(" ");
                    if (pattern.matcher(head[ii]).find()) {
                        iii++;
                        row.createCell((short) iii).setCellValue(" ");
                    }

                    iii++;
                }*/
                
                  for (final JsonNode objNode : node.get("cols")) { row.createCell((short) iii).setCellValue(objNode.asText());iii++;}
                
                
                for (final JsonNode objNode : node.get("header")) {



                    String[] tpheadcell = objNode.asText().split("\\|\\|");
                    String retHeadTmp = "";
                    for (String k : tpheadcell) {
                        Matcher matcher = pattern.matcher(k);
                        if (matcher.matches()) {
                            retHeadTmp += " " + matcher.group(2)+"("+matcher.group(1)+")";
                        } else {
                            retHeadTmp += " " +k.replaceAll("<span class=\"ordre\">.*</span>", "");
                        }
                    }
                    row.createCell((short) iii).setCellValue(retHeadTmp);
                    iii++;
if(swUnit.equals( "true")){                 
                    row.createCell((short) iii).setCellValue("unit");
                    iii++;}
if(swFlag.equals( "true")){ 
                    row.createCell((short) iii).setCellValue("flag");
                    iii++;
}
                }

            }
            HSSFRow row = sheet.createRow(i + 1);
            boolean stop = true;
              int j = 0;
              int jj=0;
            for (String k : head) {
               
                String ret1 = k.replaceAll("<span class=\"ordre\">.*</span>", "");
                Matcher matcher = pattern.matcher(k);
         try{  
                    if (stop
                            && i > 0
                            && Oldhead[j].replaceAll("<span class=\"ordre\">.*</span>", "").equals(ret1)) {
                      
                               try {
                        sheet.addMergedRegion(new CellRangeAddress(i, i + 1, jj, jj));
                        if (matcher.find()) {
                           
                            jj++;
                            sheet.addMergedRegion(new CellRangeAddress(i, i + 1, jj, jj));
                        }
                        } catch (Exception ex) { }
                        
                    }    else {
                      
                        try{
                        if (matcher.find()) {
                            
                            row.createCell((short) jj).setCellValue(matcher.group(1));
                            jj++;
                            row.createCell((short) jj).setCellValue(matcher.group(2));
                        } else {
                             
                            row.createCell((short) jj).setCellValue(ret1);
                        }
                        stop = false;
                        }
                        catch(Exception ex){   }
                    }

}catch(Exception ex){
   // System.out.println("ET O "+stop+" "+i+"  "+Oldhead.length+" "+j);
}

                j++;jj++;
            }

            Oldhead = head;


            for (final JsonNode objNode : node.get("header")) {
                try {
                    //  entry.getValue();
                    row.createCell((short) jj).setCellValue(Double.parseDouble(entry.getValue().get(objNode.asText()).get("sum").get(0).toString()));
                    jj++;
                    if(swUnit.equals( "true")){ 
                    row.createCell((short) jj).setCellValue(entry.getValue().get(objNode.asText()).get("sum").get(1).toString().replaceAll("&nbsp;", "").replaceAll("\"", ""));
                    jj++;}
                    if(swFlag.equals( "true")){ 
                    row.createCell((short) jj).setCellValue(entry.getValue().get(objNode.asText()).get("sum").get(swflagindex).toString().replaceAll("&nbsp;", "").replaceAll("\"", ""));
                    jj++;}
                } catch (Exception e) {
                    row.createCell((short) jj).setCellValue(" ");
                    jj++;
                    if(swUnit.equals( "true")){ 
                    row.createCell((short) jj).setCellValue(" ");
                    jj++;}
                    if(swFlag.equals( "true")){ 
                    row.createCell((short) jj).setCellValue(" ");
                    jj++;}
                }
            }
            i++;
        }


        HSSFRow row = sheet.createRow(++i);

        for (final JsonNode objNode : nodeFlag.get("data")) {
            row = sheet.createRow(i++);
            row.createCell((short) 0).setCellValue(objNode.get("title").asText());
            row.createCell((short) 1).setCellValue(objNode.get("label").asText());
        }
        /*Iterator<Entry<String, JsonNode>> nodeFlagIterator = nodeFlag.get("data").getElements();
         while (nodeFlagIterator.hasNext()) {
         Map.Entry<String, JsonNode> entryf = (Map.Entry<String, JsonNode>) nodeFlagIterator.next();
         System.out.println(entryf);
         row = sheet.createRow(i++);
         row.createCell((short) 0).setCellValue(myFlags);
         }*/
        row = sheet.createRow(i++);

        row = sheet.createRow(i++);
        row.createCell((short) 0).setCellValue("FAOSTAT");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd");
        String monHeure = sdf.format(new Date());
        row.createCell((short) 1).setCellValue("Date :");
        row.createCell((short) 2).setCellValue(monHeure);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);

        ResponseBuilder response = Response.ok(baos.toByteArray());

        response.header("Content-Disposition",
                "attachment; filename=Export.xls");
        return response.build();
    }
}
