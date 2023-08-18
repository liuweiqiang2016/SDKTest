package com.sensorsdata.automation.cases;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

public class InstallCase extends BaseCase {
    @Override
    public void Test() {
        super.Test();
        //触发线索事件
        SensorsDataAPI.sharedInstance().trackChannelEvent("channel1", property);
        SensorsDataAPI.sharedInstance().trackChannelEvent("channel1");
        //触发激活事件
        SensorsDataAPI.sharedInstance().trackAppInstall(property);
        //再次触发激活事件
        SensorsDataAPI.sharedInstance().trackAppInstall();
    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {
                //校验第一个线索事件名及自定义属性
                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                mCaseDao.insert(new TestCase()
                        .setName("校验线索事件名及自定义属性")
                        .setApi("trackChannelEvent(name,property)")
                        .setExpect("事件名及自定义属性通过")
                        .setIsPass(model0.getEventName().equals("channel1")&&Util.assertProperties(list,model0))
                );

                //校验特殊属性 $is_channel_callback_event 和 $channel_device_info
                mCaseDao.insert(new TestCase()
                        .setName("校验特殊属性 $is_channel_callback_event 和 $channel_device_info")
                        .setApi("trackChannelEvent(name,property)")
                        .setExpect("$is_channel_callback_event 为真,$channel_device_info 有值")
                        .setIsPass(model0.getProperties().containsKey("$channel_device_info")&&model0.getProperties().getBooleanValue("$is_channel_callback_event"))
                );

                //校验第二个线索事件
                DataModel model1= Util.dataToModel(jsonArray.getJSONObject(1));
                mCaseDao.insert(new TestCase()
                        .setName("校验非首次线索事件特殊属性")
                        .setApi("trackChannelEvent(name)")
                        .setExpect("$is_channel_callback_event 为假,$channel_device_info 有值")
                        .setIsPass(model1.getProperties().containsKey("$channel_device_info")&&!model1.getProperties().getBooleanValue("$is_channel_callback_event"))
                );

                //校验激活事件名及自定义属性
                DataModel model2= Util.dataToModel(jsonArray.getJSONObject(2));
                mCaseDao.insert(new TestCase()
                        .setName("校验激活事件名及自定义属性")
                        .setApi("trackAppInstall(property)")
                        .setExpect("$AppInstall")
                        .setIsPass(model2.getEventName().equals("$AppInstall")&&Util.assertProperties(list,model2))
                );

                //校验特殊属性 $ios_install_source
                mCaseDao.insert(new TestCase()
                        .setName("校验特殊属性 $ios_install_source")
                        .setApi("trackAppInstall(property)")
                        .setExpect("$ios_install_source 有值")
                        .setActuality("$ios_install_source："+model2.getProperties().getString("$ios_install_source"))
                        .setIsPass(model2.getProperties().containsKey("$ios_install_source"))
                );

                //校验 profile_set_once 类型
                DataModel model3= Util.dataToModel(jsonArray.getJSONObject(3));
                mCaseDao.insert(new TestCase()
                        .setName("校验 profile_set_once 类型")
                        .setApi("trackAppInstall()")
                        .setExpect("profile_set_once")
                        .setActuality("type:"+model3.getType())
                        .setIsPass(model3.getType().equals("profile_set_once"))
                );
                //校验特殊属性 $ios_install_source、$first_visit_time 有值
                mCaseDao.insert(new TestCase()
                        .setName("校验特殊属性 $ios_install_source、$first_visit_time 有值")
                        .setApi("trackAppInstall()")
                        .setExpect("特殊属性有值")
                        .setIsPass(model3.getProperties().containsKey("$ios_install_source")&&model3.getProperties().containsKey("$first_visit_time"))
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
