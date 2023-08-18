package com.sensorsdata.automation.cases;


import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.util.SensorsDataUtils;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class IdentityCase extends BaseCase {


    JSONObject identities1,identities2,identities3;
    String distinct_id1,distinct_id2,distinct_id3,anonymous_id1,anonymous_id2,anonymous_id3,login_id1,login_id2,login_id3;
    @Override
    public void Test() {
        super.Test();
        //未做处理前
        identities1= SensorsDataAPI.sharedInstance().getIdentities();
        distinct_id1=SensorsDataAPI.sharedInstance().getDistinctId();
        anonymous_id1=SensorsDataAPI.sharedInstance().getAnonymousId();
        login_id1=SensorsDataAPI.sharedInstance().getLoginId();

        SensorsDataAPI.sharedInstance().login("aaa",property);
        SensorsDataAPI.sharedInstance().track("login_1");
        SensorsDataAPI.sharedInstance().loginWithKey("wxId","bbb",property);
        SensorsDataAPI.sharedInstance().track("login_2");
        //验证匿名 ID
        SensorsDataAPI.sharedInstance().identify("fff");
        //未绑定业务 id 前
        SensorsDataAPI.sharedInstance().track("beforeBind");
        //绑定业务 id
        SensorsDataAPI.sharedInstance().bind("carId","car1024");
        //绑定后
        SensorsDataAPI.sharedInstance().track("afterBind");
        //不加延时立即获取，部分标识信息为设置前的（已知问题）
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //登陆、修改匿名 ID、绑定业务 ID 后
        identities2= SensorsDataAPI.sharedInstance().getIdentities();
        Log.e(TAG, "identities2:"+identities2.toString() );
        distinct_id2=SensorsDataAPI.sharedInstance().getDistinctId();
        anonymous_id2=SensorsDataAPI.sharedInstance().getAnonymousId();
        login_id2=SensorsDataAPI.sharedInstance().getLoginId();
        //解绑业务 id
        SensorsDataAPI.sharedInstance().unbind("carId","car1024");
        //解绑后
        SensorsDataAPI.sharedInstance().track("afterUnBind");
        //重置匿名 ID
        SensorsDataAPI.sharedInstance().resetAnonymousId();
        SensorsDataAPI.sharedInstance().track("reset");
        //注销登录
        SensorsDataAPI.sharedInstance().logout();
        SensorsDataAPI.sharedInstance().track("logout");
        //不加延时立即获取，部分标识信息为设置前的（已知问题）
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //注销、重置匿名 ID、解绑绑定业务 ID 后
        identities3= SensorsDataAPI.sharedInstance().getIdentities();
        Log.e(TAG, "identities3:"+identities3.toString() );
        distinct_id3=SensorsDataAPI.sharedInstance().getDistinctId();
        anonymous_id3=SensorsDataAPI.sharedInstance().getAnonymousId();
        login_id3=SensorsDataAPI.sharedInstance().getLoginId();

    }

    @Override
    public void afterTest() {
        super.afterTest();
        if (jsonArray!=null){
            try {
                //校验用户标识操作前的 identities
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作前的 identities")
                        .setApi("getIdentities()")
                        .setExpect("只包含 $identity_android_id")
                        .setActuality(identities1.toString())
                        .setIsPass(identities1!=null&&identities1.length()==1&&identities1.has("$identity_android_id"))
                );

                //校验用户标识操作前的 distinct_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作前的 distinct_id")
                        .setApi("getDistinctId()")
                        .setExpect("distinct_id 为 AndroidID")
                        .setActuality(distinct_id1)
                        .setIsPass(distinct_id1!=null&&distinct_id1.equals(SensorsDataUtils.getIdentifier(mContext)))
                );

                //校验用户标识操作前的 anonymous_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作前的 anonymous_id")
                        .setApi("getAnonymousId()")
                        .setExpect("anonymous_id 为 AndroidID")
                        .setActuality(anonymous_id1)
                        .setIsPass(anonymous_id1!=null&&anonymous_id1.equals(SensorsDataUtils.getIdentifier(mContext)))
                );

                //校验用户标识操作前的 login_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作前的 login_id")
                        .setApi("getLoginId()")
                        .setExpect("login_id 为 null")
                        .setActuality(login_id1)
                        .setIsPass(login_id1==null)
                );


                //校验第一个事件的登陆 ID
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆 ID")
                        .setApi("login(id,property)")
                        .setExpect("aaa")
                        .setActuality(Util.getValueFromData(jsonArray.getJSONObject(0),"login_id"))
                        .setIsPass(Util.getValueFromData(jsonArray.getJSONObject(0),"login_id").equals("aaa"))

                );
                //校验事件名
                DataModel model0=Util.dataToModel(jsonArray.getJSONObject(0));
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆事件名")
                        .setApi("login(id,property)")
                        .setExpect("$SignUp")
                        .setActuality(model0.getEventName())
                        .setIsPass(Util.assertEventName("$SignUp",model0))
                );
                //校验事件类型
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆事件类型")
                        .setApi("login(id,property)")
                        .setExpect("track_signup")
                        .setActuality(model0.getType())
                        .setIsPass(model0.getType().equals("track_signup"))
                );


                //校验登陆设置的自定义属性
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆自定义属性")
                        .setApi("login(id,property)")
                        .setExpect(property.toString())
                        .setActuality(model0.getProperties().toString())
                        .setIsPass(Util.assertProperties(list,model0))
                );

                //校验登陆后事件登陆 id 及 identities
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆后 login_id 及 identities 是否正确")
                        .setApi("login(id,property)")
                        .setExpect("aaa")
                        .setActuality(Util.getValueFromData(jsonArray.getJSONObject(1),"login_id"))
                        .setIsPass(Util.getValueFromData(jsonArray.getJSONObject(1),"login_id").equals("aaa")&&model1.getIdentities().getString("$identity_login_id").equals("aaa"))

                );

                //校验第二个事件的登陆 ID
                String login_id=Util.getValueFromData(jsonArray.getJSONObject(2),"login_id");
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆 ID")
                        .setApi("loginWithKey(id,property)")
                        .setExpect("wxId+bbb")
                        .setActuality(login_id)
                        .setIsPass(login_id.equals("wxId+bbb"))

                );
                //校验事件名
                DataModel model2=Util.dataToModel(jsonArray.getJSONObject(2));
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆事件名")
                        .setApi("loginWithKey(id,property)")
                        .setExpect("$SignUp")
                        .setActuality(model2.getEventName())
                        .setIsPass(Util.assertEventName("$SignUp",model2))
                );

                //校验事件类型
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆事件类型")
                        .setApi("loginWithKey(id,property)")
                        .setExpect("track_signup")
                        .setActuality(model2.getType())
                        .setIsPass(model2.getType().equals("track_signup"))
                );

                //校验登陆设置的自定义属性
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆自定义属性")
                        .setApi("loginWithKey(id,property)")
                        .setExpect(property.toString())
                        .setActuality(model2.getProperties().toString())
                        .setIsPass(Util.assertProperties(list,model2))
                );


                //校验登陆后事件登陆 id 及 identities
                DataModel model3=Util.dataToModel(jsonArray.getJSONObject(3));
                mCaseDao.insert(new TestCase()
                        .setName("校验登陆后 login_id 及 identities 是否正确")
                        .setApi("loginWithKey(id,property)")
                        .setExpect("wxId+bbb")
                        .setActuality(Util.getValueFromData(jsonArray.getJSONObject(3),"login_id"))
                        .setIsPass(Util.getValueFromData(jsonArray.getJSONObject(3),"login_id").equals("wxId+bbb")&&model3.getIdentities().getString("wxId").equals("bbb"))

                );


                //验证设置匿名 ID 是否生效
                mCaseDao.insert(new TestCase()
                        .setName("验证设置匿名 ID 是否生效")
                        .setApi("identify(fff)")
                        .setExpect("fff")
                        .setActuality(Util.getValueFromData(jsonArray.getJSONObject(4),"anonymous_id"))
                        .setIsPass(Util.getValueFromData(jsonArray.getJSONObject(4),"anonymous_id").equals("fff"))
                );


                //校验绑定前业务 ID 是否存在
                DataModel model4= Util.dataToModel(jsonArray.getJSONObject(4));
                mCaseDao.insert(new TestCase()
                        .setName("校验绑定前业务 ID 是否存在")
                        .setApi("bind(key,value)")
                        .setExpect("业务 ID 不存在")
                        .setActuality("业务 ID 状态:"+model4.getIdentities().containsKey("carId"))
                        .setIsPass(!model4.getIdentities().containsKey("carId"))
                );

                //校验绑定事件名
                DataModel model5= Util.dataToModel(jsonArray.getJSONObject(5));
                mCaseDao.insert(new TestCase()
                        .setName("校验绑定事件名")
                        .setApi("bind(key,value)")
                        .setExpect("$BindID")
                        .setActuality(model5.getEventName())
                        .setIsPass(model5.getEventName().equals("$BindID"))
                );

                //校验绑定事件类型
                mCaseDao.insert(new TestCase()
                        .setName("校验绑定事件类型")
                        .setApi("bind(key,value)")
                        .setExpect("track_id_bind")
                        .setActuality(model5.getType())
                        .setIsPass(model5.getType().equals("track_id_bind"))
                );

                //校验 identities
                mCaseDao.insert(new TestCase()
                        .setName("校验 identities 是否包含业务 ID")
                        .setApi("bind(key,value)")
                        .setExpect("carId:car1024")
                        .setActuality("carId:"+model5.getIdentities().getString("carId"))
                        .setIsPass(model5.getIdentities().containsKey("carId")&&model5.getIdentities().getString("carId").equals("car1024"))
                );

                //校验绑定后事件的 identities
                DataModel model6= Util.dataToModel(jsonArray.getJSONObject(6));
                mCaseDao.insert(new TestCase()
                        .setName("校验绑定后事件的 identities")
                        .setApi("bind(key,value)")
                        .setExpect("carId:car1024")
                        .setActuality("carId:"+model6.getIdentities().getString("carId"))
                        .setIsPass(model6.getIdentities().containsKey("carId")&&model6.getIdentities().getString("carId").equals("car1024"))
                );

                //校验用户标识操作后的 identities
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作后的 identities")
                        .setApi("getIdentities()")
                        .setExpect("包含 android_id、登陆 id、匿名 id、业务 id")
                        .setActuality(identities2.toString())
                        .setIsPass(identities2!=null&&identities2.length()==3&&identities2.has("$identity_android_id")&&identities2.has("wxId")&&identities2.has("carId"))
                );

                //校验用户标识操作后的 distinct_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作后的 distinct_id")
                        .setApi("getDistinctId()")
                        .setExpect("distinct_id 为 wxId+bbb")
                        .setActuality(distinct_id2)
                        .setIsPass(distinct_id2!=null&&distinct_id2.equals("wxId+bbb"))
                );

                //校验用户标识操作后的 anonymous_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作后的 anonymous_id")
                        .setApi("getAnonymousId()")
                        .setExpect("anonymous_id 为 fff")
                        .setActuality(anonymous_id2)
                        .setIsPass(anonymous_id2!=null&&anonymous_id2.equals("fff"))
                );

                //校验用户标识操作后的 login_id
                mCaseDao.insert(new TestCase()
                        .setName("校验用户标识操作后的 login_id")
                        .setApi("getLoginId()")
                        .setExpect("login_id 为 wxId+bbb")
                        .setActuality(login_id2)
                        .setIsPass(login_id2!=null&&login_id2.equals("wxId+bbb"))
                );


                //校验解绑事件名
                DataModel model7= Util.dataToModel(jsonArray.getJSONObject(7));
                mCaseDao.insert(new TestCase()
                        .setName("校验解绑事件名")
                        .setApi("unbind(key,value)")
                        .setExpect("$UnbindID")
                        .setActuality(model7.getEventName())
                        .setIsPass(model7.getEventName().equals("$UnbindID"))
                );

                //校验解绑事件类型
                mCaseDao.insert(new TestCase()
                        .setName("校验解绑事件类型")
                        .setApi("unbind(key,value)")
                        .setExpect("track_id_unbind")
                        .setActuality(model7.getType())
                        .setIsPass(model7.getType().equals("track_id_unbind"))
                );

                //校验 identities
                mCaseDao.insert(new TestCase()
                        .setName("校验 identities 是只包含业务 ID")
                        .setApi("unbind(key,value)")
                        .setExpect("carId:car1024")
                        .setActuality("carId:"+model7.getIdentities().getString("carId"))
                        .setIsPass(model7.getIdentities().containsKey("carId")&&model7.getIdentities().getString("carId").equals("car1024")&&model7.getIdentities().size()==1)
                );

                //校验解绑后事件的 identities
                DataModel model8= Util.dataToModel(jsonArray.getJSONObject(8));
                mCaseDao.insert(new TestCase()
                        .setName("校验解绑后事件的 identities")
                        .setApi("unbind(key,value)")
                        .setExpect("没有业务 carId")
                        .setActuality("carId 状态:"+model8.getIdentities().containsKey("carId"))
                        .setIsPass(!model8.getIdentities().containsKey("carId"))
                );

                //验证重置匿名 ID 是否生效
                mCaseDao.insert(new TestCase()
                        .setName("验证重置匿名 ID 是否生效")
                        .setApi("resetAnonymousId()")
                        .setExpect("重置后 anonymous_id 为 AndroidId")
                        .setIsPass(Util.getValueFromData(jsonArray.getJSONObject(9),"anonymous_id").equals(SensorsDataUtils.getIdentifier(mContext)))
                );


                //校验退出后，login_id 是否清除
                DataModel model10=Util.dataToModel(jsonArray.getJSONObject(10));
                mCaseDao.insert(new TestCase()
                        .setName("校验退出后 login_id 是否清除")
                        .setApi("logout()")
                        .setExpect("login_id 不存在")
                        .setActuality("login_id 状态:"+Util.hasValueFromData(jsonArray.getJSONObject(10),"login_id"))
                        .setIsPass(!Util.hasValueFromData(jsonArray.getJSONObject(10),"login_id")&&!model10.getIdentities().containsKey("wxId"))
                );

                //校验注销、解绑、重置后的 identities
                mCaseDao.insert(new TestCase()
                        .setName("校验注销、解绑、重置后的 identities")
                        .setApi("getIdentities()")
                        .setExpect("只包含 $identity_android_id")
                        .setActuality(identities3.toString())
                        .setIsPass(identities3!=null&&identities3.length()==1&&identities3.has("$identity_android_id"))
                );

                //校验注销后的 distinct_id
                mCaseDao.insert(new TestCase()
                        .setName("校验注销后的 distinct_id")
                        .setApi("getDistinctId()")
                        .setExpect("distinct_id 为 AndroidID")
                        .setActuality(distinct_id3)
                        .setIsPass(distinct_id3!=null&&distinct_id3.equals(SensorsDataUtils.getIdentifier(mContext)))
                );

                //校验重置后的 anonymous_id
                mCaseDao.insert(new TestCase()
                        .setName("校验重置后的 anonymous_id")
                        .setApi("getAnonymousId()")
                        .setExpect("anonymous_id 为 AndroidID")
                        .setActuality(anonymous_id3)
                        .setIsPass(anonymous_id3!=null&&anonymous_id3.equals(SensorsDataUtils.getIdentifier(mContext)))
                );

                //校验注销后的 login_id
                mCaseDao.insert(new TestCase()
                        .setName("校验注销后的 login_id")
                        .setApi("getLoginId()")
                        .setExpect("login_id 为 null")
                        .setActuality(login_id3)
                        .setIsPass(login_id3==null)
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
