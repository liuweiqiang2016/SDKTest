package com.sensorsdata.automation.model;


import com.alibaba.fastjson.JSONObject;

//可以处理明文数据，无法处理加密数据
public class DataModel {

    private String type;
    private JSONObject identities;
    private JSONObject lib;
    private String eventName;
    private JSONObject properties;
    private String itemType;
    private String itemId;

    public JSONObject getIdentities() {
        return identities;
    }

    public void setIdentities(JSONObject identities) {
        this.identities = identities;
    }

    public JSONObject getLib() {
        return lib;
    }

    public void setLib(JSONObject lib) {
        this.lib = lib;
    }

    public JSONObject getProperties() {
        return properties;
    }

    public void setProperties(JSONObject properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
