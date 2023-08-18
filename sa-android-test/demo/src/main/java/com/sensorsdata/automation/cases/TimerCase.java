package com.sensorsdata.automation.cases;

import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

public class TimerCase extends BaseCase {

    @Override
    public void Test() {
        super.Test();
        SensorsDataAPI.sharedInstance().trackTimerStart("timer");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SensorsDataAPI.sharedInstance().trackTimerPause("timer");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SensorsDataAPI.sharedInstance().trackTimerResume("timer");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SensorsDataAPI.sharedInstance().trackTimerEnd("timer",property);
    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {
                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                //校验事件名及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("校验事件名及自定义属性是否正确")
                        .setApi("trackTimerEnd(eventName,property)")
                        .setExpect("事件名及自定义属性正确")
                        .setIsPass(Util.assertEventName("timer",model0)&&Util.assertProperties(list,model0)));

                //校验特殊属性 event_duration 是否正确
                mCaseDao.insert(new TestCase()
                        .setName("校验特殊属性 event_duration 是否正确")
                        .setApi("trackTimerEnd(eventName,property)")
                        .setExpect("4000ms,偏差 50 ms 内")
                        .setActuality(model0.getProperties().getFloat("event_duration")+"")
                        .setIsPass(model0.getProperties().getFloat("event_duration")-4000<50)
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
