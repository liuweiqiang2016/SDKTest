package com.sensorsdata.automation.model;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class PropertyModel {
    private PropertyType type;
    private String key;
    private Object value;

    public PropertyModel(PropertyType type, String key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
