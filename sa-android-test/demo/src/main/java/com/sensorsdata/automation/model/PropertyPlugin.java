package com.sensorsdata.automation.model;


import com.sensorsdata.analytics.android.sdk.plugin.property.SAPropertyPlugin;
import com.sensorsdata.analytics.android.sdk.plugin.property.SAPropertyPluginPriority;
import com.sensorsdata.analytics.android.sdk.plugin.property.beans.SAPropertiesFetcher;
import com.sensorsdata.analytics.android.sdk.plugin.property.beans.SAPropertyFilter;

import org.json.JSONException;


public class PropertyPlugin extends SAPropertyPlugin {

    @Override
    public void properties(SAPropertiesFetcher fetcher) {
//        fetcher.getProperties().remove("$app");
        try {
            fetcher.getProperties().put("$app_name","test_property");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isMatchedWithFilter(SAPropertyFilter filter) {
        //仅对  track 类型数据处理
        return filter.getType().getEventType().equals("track");
        //对所有事件调整属性
//        return true;
    }

    @Override
    public SAPropertyPluginPriority priority() {
        return super.priority();
//        return SAPropertyPluginPriority.HIGH;
    }
}
