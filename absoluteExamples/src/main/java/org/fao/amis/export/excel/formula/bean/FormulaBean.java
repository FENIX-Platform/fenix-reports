package org.fao.amis.export.excel.formula.bean;

import java.util.LinkedList;

/**
 * Created by fabrizio on 11/12/14.
 */
public class FormulaBean {

    private String operand;

    private LinkedList<String> addendums;

    private String operator;

    public LinkedList<String> getAddendums() {
        return addendums;
    }

    public void setAddendums(LinkedList<String> addendums) {
        this.addendums = addendums;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "FormulaBean{" +
                "addendums=" + addendums +
                ", operator='" + operator + '\'' +
                '}';
    }
}
