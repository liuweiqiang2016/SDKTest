package com.sensorsdata.automation.cases;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SuperPropertiesCase extends BaseCase {

    JSONObject result1,result2,result3;
    com.alibaba.fastjson.JSONObject spObject1,spObject2;

    @Override
    public void Test() {
        super.Test();
        JSONObject superProperties=Util.makeJSONObject(list);
        result1=SensorsDataAPI.sharedInstance().getSuperProperties();
        //注册静态公共属性
        SensorsDataAPI.sharedInstance().registerSuperProperties(superProperties);
        //每个事件均携带自定义属性
        SensorsDataAPI.sharedInstance().track("super1");
        SensorsDataAPI.sharedInstance().track("super2");
        //不加延时立即获取，部分信息为设置前的（已知问题）
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result2=SensorsDataAPI.sharedInstance().getSuperProperties();
        //后续事件只携带一个
        SensorsDataAPI.sharedInstance().unregisterSuperProperty("name");
        SensorsDataAPI.sharedInstance().track("super3");
        //不加延时立即获取，部分信息为设置前的（已知问题）
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //校验本地是否持久化静态公共属性
        SharedPreferences sp=mContext.getSharedPreferences("com.sensorsdata.analytics.android.sdk.SensorsDataAPI",0);
        spObject1= JSON.parseObject(sp.getString("super_properties","{}"));
        //清空静态公共属性
        SensorsDataAPI.sharedInstance().clearSuperProperties();
        SensorsDataAPI.sharedInstance().track("super4");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result3=SensorsDataAPI.sharedInstance().getSuperProperties();
        spObject2= JSON.parseObject(sp.getString("super_properties","{}"));

    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {
                //未设置前静态公共属性为空
                mCaseDao.insert(new TestCase()
                        .setName("校验未设置静态公共属性")
                        .setApi("getSuperProperties()")
                        .setExpect("未设置前静态公共属性为空")
                        .setIsPass(result1!=null&&result1.length()==0)
                );

                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                DataModel model1= Util.dataToModel(jsonArray.getJSONObject(1));
                //校验静态公共属性是否生效
                mCaseDao.insert(new TestCase()
                        .setName("校验静态公共属性是否生效")
                        .setApi("registerSuperProperties(superProperties)")
                        .setExpect("静态公共属性生效")
                        .setIsPass(Util.assertProperties(list,model0)&&Util.assertProperties(list,model1))
                );

                com.alibaba.fastjson.JSONObject parseObject2= JSON.parseObject(result2.toString());
                mCaseDao.insert(new TestCase()
                        .setName("校验获取静态公共属性是否正确")
                        .setApi("getSuperProperties()")
                        .setExpect("正确获取静态公共属性")
                        .setIsPass(result2!=null&&result2.length()==5&&parseObject2.containsKey("age")&&parseObject2.containsKey("isStudent")&&parseObject2.containsKey("reg_time")&&parseObject2.containsKey("cars")&&parseObject2.containsKey("name"))
                );

                //校验移除单个公共属性是否生效
                DataModel model2= Util.dataToModel(jsonArray.getJSONObject(2));
                mCaseDao.insert(new TestCase()
                        .setName("校验移除单个公共属性是否生效")
                        .setApi("unregisterSuperProperty(superPropertyName)")
                        .setExpect("移除 name")
                        .setIsPass(!model2.getProperties().containsKey("name"))
                );

                mCaseDao.insert(new TestCase()
                        .setName("校验本地是否持久化静态公共属性")
                        .setApi("registerSuperProperties(superProperties)")
                        .setExpect("持久化静态公共属性")
                        .setIsPass(spObject1!=null&&spObject1.containsKey("age")&&spObject1.containsKey("isStudent")&&spObject1.containsKey("reg_time")&&spObject1.containsKey("cars")&&!spObject1.containsKey("name"))
                );

                DataModel model3= Util.dataToModel(jsonArray.getJSONObject(3));
                mCaseDao.insert(new TestCase()
                        .setName("校验清空后事件是否包含静态公共属性")
                        .setApi("clearSuperProperties()")
                        .setExpect("清空所有静态公共属性")
                        .setIsPass(!model3.getProperties().containsKey("name")&&!model3.getProperties().containsKey("age")&&!model3.getProperties().containsKey("isStudent")&&!model3.getProperties().containsKey("reg_time")&&!model3.getProperties().containsKey("cars"))
                );

                mCaseDao.insert(new TestCase()
                        .setName("校验清空获取静态公共属性")
                        .setApi("getSuperProperties()")
                        .setExpect("获取值为空")
                        .setIsPass(result3!=null&&result3.length()==0&&spObject2!=null&&spObject2.isEmpty())
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
