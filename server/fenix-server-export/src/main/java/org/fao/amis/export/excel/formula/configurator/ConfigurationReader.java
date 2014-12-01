package org.fao.amis.export.excel.formula.configurator;


import org.fao.amis.export.excel.formula.bean.FormulaBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by fabrizio on 11/12/14.
 */
public class ConfigurationReader {

    private Properties prop;

    public ConfigurationReader(String URL_PROPERTIES){

        this.prop = new Properties();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(URL_PROPERTIES);
            this.prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<FormulaBean> getFormulas(String[] codes){

        LinkedList<FormulaBean> formulaBeans = new LinkedList<FormulaBean>();


        for(String code: codes){

            FormulaBean formulaBean = new FormulaBean();
            String operand = code;
            LinkedList<String> addendums = new LinkedList<String>();
            String[] valueNotSplitted = this.prop.getProperty(code).split(",");
            for(String addendum: valueNotSplitted){
                addendums.add(addendum);
            }

            formulaBean.setAddendums(addendums);
            formulaBean.setOperator(this.prop.getProperty(code + "Operators"));
            formulaBean.setOperand(operand);

            formulaBeans.add(formulaBean);

        }
        return formulaBeans;
    }



}
