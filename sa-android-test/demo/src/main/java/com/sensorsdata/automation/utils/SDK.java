package com.sensorsdata.automation.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Spinner;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataDynamicSuperProperties;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackEventCallBack;
import com.sensorsdata.analytics.android.sdk.SensorsNetworkType;
import com.sensorsdata.analytics.android.sdk.core.business.exposure.SAExposureConfig;
import com.sensorsdata.analytics.android.sdk.deeplink.SensorsDataDeepLinkCallback;
import com.sensorsdata.analytics.android.sdk.internal.beans.LimitKey;
import com.sensorsdata.automation.activity.AnnotationActivity;
import com.sensorsdata.automation.activity.ElementActivity;
import com.sensorsdata.automation.activity.ExposureActivity;
import com.sensorsdata.automation.activity.MainActivity;
import com.sensorsdata.automation.activity.PageActivity;
import com.sensorsdata.automation.activity.WebActivity;
import com.sensorsdata.automation.fragment.AnnotationFragment;
import com.sensorsdata.automation.model.PropertyPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDK {

    static String TAG="SA.S:App";


    public static void initSA(Context context){
        SAConfigOptions saConfigOptions=new SAConfigOptions("http://10.129.20.62:8106/sa?project=liuweiqiang");
        saConfigOptions.enableLog(true);
        //设置匿名 id
//        saConfigOptions.setAnonymousId("anonymous_123");
        //控制敏感属性采集
        Map<String, String> limitKeys =new HashMap<>();
        limitKeys.put(LimitKey.CARRIER,"合肥电信");
        saConfigOptions.registerLimitKeys(limitKeys);
        //设置为false,将不支持 API 17（Android 4.2)以下的版本
        saConfigOptions.enableJavaScriptBridge(true);
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_START|SensorsAnalyticsAutoTrackEventType.APP_CLICK|SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN|SensorsAnalyticsAutoTrackEventType.APP_END);
        saConfigOptions.setNetworkTypePolicy(SensorsNetworkType.TYPE_NONE);
        //远程配置请求间隔
        saConfigOptions.setMinRequestInterval(6);
        saConfigOptions.setMaxRequestInterval(10);
        //开启屏幕方向采集
        saConfigOptions.enableTrackScreenOrientation(true);
        //开启 $event_session_id 采集
        saConfigOptions.enableSession(true);
        //注册自定义插件
        saConfigOptions.registerPropertyPlugin(new PropertyPlugin());
        //开启页面停留时常统计
        saConfigOptions.enableTrackPageLeave(true,true);
        List<Class<?>> ignoreList=new ArrayList<>();
        ignoreList.add(MainActivity.class);
        ignoreList.add(WebActivity.class);
        ignoreList.add(ExposureActivity.class);
        ignoreList.add(ElementActivity.class);
        ignoreList.add(AnnotationActivity.class);
        ignoreList.add(AnnotationFragment.class);
        saConfigOptions.ignorePageLeave(ignoreList);
        //全局曝光配置
        SAExposureConfig exposureConfig=new SAExposureConfig(1.0f,0,true);
        exposureConfig.setDelayTime(0);
        saConfigOptions.setExposureConfig(exposureConfig);
        SensorsDataAPI.startWithConfigOptions(context,saConfigOptions);
        //设置 session 时间间隔
        SensorsDataAPI.sharedInstance().setSessionIntervalTime(10*1000);
        //开启 Fragment 页面浏览采集
        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
        //不方便单独验证，因此放入到初始化中作为通用属性验证
        SensorsDataAPI.sharedInstance().setGPSLocation(31.52,117.17,"GPS");
        //动态公共属性
        SensorsDataAPI.sharedInstance().registerDynamicSuperProperties(new SensorsDataDynamicSuperProperties() {
            @Override
            public JSONObject getDynamicSuperProperties() {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("dynamic","testDynamic");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        });
        //忽略 Activity 及所属 Fragment 点击及页面浏览
        SensorsDataAPI.sharedInstance().ignoreAutoTrackActivity(PageActivity.class);
        //session 时间设置为 10000ms
        SensorsDataAPI.sharedInstance().setSessionIntervalTime(10*1000);
    }

}
