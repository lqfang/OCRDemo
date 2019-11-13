package com.xinhuo.ocrdemo.entity;

import java.util.List;

public class ListKeyValue {

    private List<KeyBean> list;

    public List<KeyBean> getList() {
        return list;
    }

    public void setList(List<KeyBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ListKeyValue{" +
                "list=" + list +
                '}';
    }
}
