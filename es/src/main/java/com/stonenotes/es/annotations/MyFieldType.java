package com.stonenotes.es.annotations;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
public enum MyFieldType {
    Text("text"),
    Integer("integer"),
    Long("long"),
    Date("date"),
    Float("float"),
    Double("double"),
    Boolean("boolean"),
    Object("object"),
    Auto("text"),
    Nested("nested"),
    Ip("id"),
    Attachment("attachment"),
    Keyword("keyword");
    private String value;

    MyFieldType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
