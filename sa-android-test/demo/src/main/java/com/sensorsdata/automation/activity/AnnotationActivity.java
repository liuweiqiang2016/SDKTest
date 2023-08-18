package com.sensorsdata.automation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick;
import com.sensorsdata.automation.R;
import com.sensorsdata.automation.fragment.AnnotationFragment;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;


public class AnnotationActivity extends BaseActivity  {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        button=findViewById(R.id.an_btn);
        //其他自定义操作
        mHandler.sendEmptyMessageDelayed(msg_otherAction,2000);

    }
    private void initFragment(){
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.an_fra,new AnnotationFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }

    @Override
    public void otherAction() {
        super.otherAction();
        //更新时间戳，忽略上一个页面的停留时长及当Activity的页面浏览
        beforeSize=mDatabaseHelper.getDataSize();
        Log.e("SA.AnnotationActivity", "beforeSize: "+beforeSize );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //这里验证 disable/enable SDK
        //关闭 SDK
        SensorsDataAPI.disableSDK();
        //验证事件是否可以触发
        SensorsDataAPI.sharedInstance().track("disable");
        //开启 SDK
        SensorsDataAPI.enableSDK();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //这里触发注解点击
        onMyClick(button);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Fragment 采集注解页面浏览及注解手动事件
        initFragment();

        mHandler.sendEmptyMessageDelayed(msg_assertData,3000);
    }


    @Override
    public void assertData() {
        super.assertData();
        String eventName,scr_name,url,title,element_content;
        if (jsonArray!=null){
            try {

                //校验第二条事件是 $AppDataTrackingClose
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(0));
                eventName=model1.getEventName();
                mCaseDao.insert(new TestCase()
                        .setName("disableSDK 是否生效")
                        .setApi("disableSDK")
                        .setExpect("触发事件$AppDataTrackingClose")
                        .setActuality("事件名为:"+eventName)
                        .setIsPass(eventName.equals("$AppDataTrackingClose")));

                //校验第三条事件是 $AppClick
                DataModel model2=Util.dataToModel(jsonArray.getJSONObject(1));
                eventName=model2.getEventName();
                element_content=model2.getProperties().getString("$element_content");
                mCaseDao.insert(new TestCase()
                        .setName("注解采集点击事件生效")
                        .setApi("@SensorsDataTrackViewOnClick")
                        .setExpect("事件名为$AppClick且属性为传入的button")
                        .setIsPass(eventName.equals("$AppClick")&&element_content.equals("注解测试")));

                //校验第四条事件是 $AppViewScreen
                DataModel model3=Util.dataToModel(jsonArray.getJSONObject(2));
                eventName=model3.getEventName();
                url=model3.getProperties().getString("$url");
                title=model3.getProperties().getString("$title");
                scr_name=model3.getProperties().getString("$screen_name");
                mCaseDao.insert(new TestCase()
                        .setName("注解设置title、url")
                        .setExpect("title、url生效")
                        .setIsPass(eventName.equals("$AppViewScreen")
                                &&url.equals("url_a")
                                &&title.equals("title_a")
                                &&scr_name.contains("AnnotationFragment")
                        ));

                //校验第五条事件是 getInfo
                DataModel model4=Util.dataToModel(jsonArray.getJSONObject(3));
                eventName=model4.getEventName();
                mCaseDao.insert(new TestCase()
                        .setName("注解自定义事件正常")
                        .setApi("@SensorsDataTrackEvent")
                        .setExpect("自定义事件正常触发")
                        .setIsPass(eventName.equals("getInfo")
                                &&model4.getProperties().containsKey("city")
                                &&model4.getProperties().getString("city").equals("hefei")
                        ));

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
        mHandler.sendEmptyMessageDelayed(msg_jumpActivity,1000);
    }

    @Override
    public void jumpActivity() {
        Intent i=new Intent(AnnotationActivity.this, WebActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
        //生成报告
//        Util.writeReportHtml(this);
        // adb pull /sdcard/测试报告.html  /Users/weiqiangliu/Downloads
    }

    @SensorsDataTrackViewOnClick
    public void onMyClick(View view) {

    }
}