package com.cvberry.berrypim.data;

/**
 * [CB 12/3/2015] can be used for things like emails and addresses which have an associated "type".
 */
public class TypeAnnotatedString {

    protected String value;
    protected String type;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String value) {
        this.type = value;
    }

}
