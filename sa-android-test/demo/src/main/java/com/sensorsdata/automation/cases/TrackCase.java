package com.sensorsdata.automation.cases;


import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.model.PropertyModel;
import com.sensorsdata.automation.model.PropertyType;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackCase extends BaseCase {

    //事件名
    private String eventName;
    @Override
    public void beforeTest() {
        super.beforeTest();
    }

    @Override
    public void Test() {
        super.Test();
        //事件名
        eventName="hello";
        SensorsDataAPI.sharedInstance().track(eventName);

        SensorsDataAPI.sharedInstance().track("news",property);
    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {

                DataModel model0=Util.dataToModel(jsonArray.getJSONObject(0));
                //校验事件名
                mCaseDao.insert(new TestCase()
                        .setName("校验事件名是否正确")
                        .setApi("track(eventName)")
                        .setExpect(eventName)
                        .setActuality(model0.getEventName())
                        .setIsPass(Util.assertEventName(eventName,model0)));

                //校验事件类型
                mCaseDao.insert(new TestCase()
                        .setName("校验事件类型是否正确")
                        .setApi("track(eventName)")
                        .setExpect("track")
                        .setActuality(model0.getType())
                        .setIsPass(Util.assertType("track",model0))
                );

                //第二条数据
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                //校验自定义属性
                mCaseDao.insert(new TestCase()
                        .setName("校验自定义属性")
                        .setApi("track(eventName,properties)")
                        .setExpect(property.toString())
                        .setActuality(model1.getProperties().toString())
                        .setIsPass(Util.assertProperties(list,model1))
                );
            } catch (JSONException e) {
                e.printStackTrace();
                mCaseDao.insert(new TestCase()
                        .setName("数据及属性正常获取")
                        .setDescribe("SDK 入库的数据或属性可以正常获取")
                        .setExpect("数据及属性正常获取")
                        .setActuality("数据及属性未能获取")
                        .setFailReason("JSON 解析异常")
                        .setIsPass(false)
                        .setRemark("崩溃信息："+Util.getExceptionInfo(e)));

            }
        }
    }
}
