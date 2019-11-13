package com.xinhuo.ocrdemo.entity;

public class KeyBean {

    private String key;
    private String value;

    public String getKey() {


            return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
