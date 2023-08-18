package com.sensorsdata.automation.cases;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.model.PropertyModel;
import com.sensorsdata.automation.model.PropertyType;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileCase extends BaseCase {

    @Override
    public void Test() {
        super.Test();

        //profileSetOnce 的两种写法
        SensorsDataAPI.sharedInstance().profileSetOnce(property);
        SensorsDataAPI.sharedInstance().profileSetOnce("key","value");
        //profileSet 的两种写法
        SensorsDataAPI.sharedInstance().profileSet(property);
        SensorsDataAPI.sharedInstance().profileSet("key","value");
        //profileIncrement 的两种写法
        SensorsDataAPI.sharedInstance().profileIncrement("age",20);
        Map<String,Number> map=new HashMap<>();
        map.put("age",20);
        SensorsDataAPI.sharedInstance().profileIncrement(map);
        //profileAppend 的两种写法
        SensorsDataAPI.sharedInstance().profileAppend("books","狂人日记");
        Set<String> set=new HashSet<>();
        set.add("狂人日记");
        SensorsDataAPI.sharedInstance().profileAppend("books",set);
        //删除某个属性
        SensorsDataAPI.sharedInstance().profileUnset("books");
        //删除所有属性
        SensorsDataAPI.sharedInstance().profileDelete();
        //绑定推送 ID
        SensorsDataAPI.sharedInstance().profilePushId("pushKey","id123");
        //删除推送 ID
        SensorsDataAPI.sharedInstance().profileUnsetPushId("pushKey");

    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {
                //第一个 profileSetOnce
                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileSetOnce:校验数据类型及自定义属性是否正确")
                        .setApi("profileSetOnce(property)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model0.getType().equals("profile_set_once")&&model0.getProperties().size()==5&&Util.assertProperties(list,model0))
                );
                //第二个 profileSetOnce
                DataModel model1= Util.dataToModel(jsonArray.getJSONObject(1));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileSetOnce:校验数据类型及自定义属性是否正确")
                        .setApi("profileSetOnce(key,value)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model1.getType().equals("profile_set_once")&&model1.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.STRING,"key","value"),model1))
                );

                //第一个 profileSet
                DataModel model2= Util.dataToModel(jsonArray.getJSONObject(2));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileSet:校验数据类型及自定义属性是否正确")
                        .setApi("profileSet(property)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model2.getType().equals("profile_set")&&model2.getProperties().size()==5&&Util.assertProperties(list,model2))
                );
                //第二个 profileSet
                DataModel model3= Util.dataToModel(jsonArray.getJSONObject(3));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileSet:校验数据类型及自定义属性是否正确")
                        .setApi("profileSet(key,value)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model3.getType().equals("profile_set")&&model3.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.STRING,"key","value"),model3))
                );

                //第一个 profileIncrement
                DataModel model4= Util.dataToModel(jsonArray.getJSONObject(4));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileIncrement:校验数据类型及自定义属性是否正确")
                        .setApi("profileIncrement(key,value)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model4.getType().equals("profile_increment")&&model4.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.NUMBER,"age",20),model4))
                );
                //第二个 profileIncrement
                DataModel model5= Util.dataToModel(jsonArray.getJSONObject(5));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileIncrement:校验数据类型及自定义属性是否正确")
                        .setApi("profileIncrement(Map<String,Number>)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model5.getType().equals("profile_increment")&&model5.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.NUMBER,"age",20),model5))
                );

                //第一个 profileAppend
                DataModel model6= Util.dataToModel(jsonArray.getJSONObject(6));
                List<String> books=new ArrayList<>();
                books.add("狂人日记");
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileAppend:校验数据类型及自定义属性是否正确")
                        .setApi("profileAppend(key,value)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model6.getType().equals("profile_append")&&model6.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.LIST,"books",books),model6))
                );
                //第二个 profileAppend
                DataModel model7= Util.dataToModel(jsonArray.getJSONObject(7));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileAppend:校验数据类型及自定义属性是否正确")
                        .setApi("profileAppend(Set<String>)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model7.getType().equals("profile_append")&&model7.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.LIST,"books",books),model7))
                );
                //profileUnset:删除单个属性
                DataModel model8= Util.dataToModel(jsonArray.getJSONObject(8));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileUnset:校验数据类型及自定义属性是否正确")
                        .setApi("profileUnset(key)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model8.getType().equals("profile_unset")&&model8.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.BOOL,"books",true),model8))
                );
                //profileDelete:删除所有属性
                DataModel model9= Util.dataToModel(jsonArray.getJSONObject(9));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileDelete:校验数据类型及自定义属性是否正确")
                        .setApi("profileDelete()")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model9.getType().equals("profile_delete")&&model9.getProperties().size()==0)
                );
                //profilePushId:设置推送 ID
                DataModel model10= Util.dataToModel(jsonArray.getJSONObject(10));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profilePushId:校验数据类型及自定义属性是否正确")
                        .setApi("profilePushId(key,value)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model10.getType().equals("profile_set")&&model10.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.STRING,"pushKey","id123"),model10))
                );
                //profileUnsetPushId:删除设置推送 ID
                DataModel model11= Util.dataToModel(jsonArray.getJSONObject(11));
                //校验数据类型及自定义属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("profileUnsetPushId:校验数据类型及自定义属性是否正确")
                        .setApi("profileUnsetPushId(key)")
                        .setExpect("数据类型及自定义属性正确")
                        .setIsPass(model11.getType().equals("profile_unset")&&model11.getProperties().size()==1&&Util.assertProperty(new PropertyModel(PropertyType.BOOL,"pushKey",true),model11))
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
