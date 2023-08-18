package com.sensorsdata.automation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.sensorsdata.automation.R;
import com.sensorsdata.automation.fragment.TestFragment;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;


public class PageActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        Spinner spinner=findViewById(R.id.page_sp);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initFragment();
        //停留 3000 ms，然后进入后台
        mHandler.sendEmptyMessageDelayed(msg_goBackGround,3000);

    }

    private void initFragment(){
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.page_fra,new TestFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }

    @Override
    public void goBackGround() {
        super.goBackGround();
        //1000 ms 后回到前台
        mHandler.sendEmptyMessageDelayed(msg_goForeGround,1000);
    }

    @Override
    public void goForeGround() {
        super.goForeGround();
        Log.e("SA.PageActivity", "goForeGround: =======================");
        //1000 ms 校验数据
        mHandler.sendEmptyMessageDelayed(msg_assertData,1000);
    }

    @Override
    public void assertData() {
        super.assertData();
        String eventName,scr_name;
        float duration;
        if (jsonArray!=null){
            try {
                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                eventName=model0.getEventName();
                //时长误差在 100ms 内
                duration=model0.getProperties().getFloat("event_duration");
                scr_name=model0.getProperties().getString("$screen_name");
                //校验 PageActivity 的页面停留事件被正常采集，事件名、属性（时长、页面名称）
                mCaseDao.insert(new TestCase()
                        .setName("校验 PageActivity 页面停留事件")
                        .setApi("enableTrackPageLeave(true,true)")
                        .setExpect("PageActivity 页面停留事件正常采集")
                        .setIsPass(Math.abs(duration*1000-3000)<300&&eventName.equals("$AppPageLeave")&&scr_name.contains("PageActivity")));


                //第二条数据
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                eventName=model1.getEventName();
                //时长误差在 100ms 内
                duration=model1.getProperties().getFloat("event_duration");
                scr_name=model1.getProperties().getString("$screen_name");

                //校验 TestFragment 的页面停留事件被正常采集
                mCaseDao.insert(new TestCase()
                        .setName("校验 TestFragment 页面停留事件")
                        .setApi("enableTrackPageLeave(true,true)")
                        .setExpect("TestFragment 页面停留事件正常采集")
                        .setIsPass(Math.abs(duration*1000-3000)<300&&eventName.equals("$AppPageLeave")&&scr_name.contains("TestFragment")));

                //校验一系列忽略方法是否生效
                if (jsonArray.length()!=2){
                    mCaseDao.insert(new TestCase()
                            .setName("校验忽略方法是否生效")
                            .setApi("页面浏览、点击、页面停留")
                            .setExpect("均正常生效")
                            .setActuality("至少一个忽略未生效")
                            .setIsPass(false));

                }else {
                    mCaseDao.insert(new TestCase()
                            .setName("校验注解@SensorsDataIgnoreTrackAppViewScreen是否生效")
                            .setApi("注解@SensorsDataIgnoreTrackAppViewScreen")
                            .setExpect("注解生效")
                            .setActuality("MainActivity 不触发页面浏览")
                            .setIsPass(true));

                    mCaseDao.insert(new TestCase()
                            .setName("校验ignorePageLeave是否生效")
                            .setApi("ignorePageLeave")
                            .setExpect("忽略生效")
                            .setActuality("MainActivity 不触发页面停留")
                            .setIsPass(true));

                    mCaseDao.insert(new TestCase()
                            .setName("校验ignoreAutoTrackActivity是否生效")
                            .setApi("ignoreAutoTrackActivity")
                            .setExpect("忽略生效")
                            .setActuality("PageActivity及所属TestFragment均不触发页面浏览及点击事件")
                            .setIsPass(true));
                }


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
        }else{
            //校验事件类型
            mCaseDao.insert(new TestCase()
                    .setName("校验页面停留时长是否正常采集")
                    .setApi("enableTrackPageLeave(true,true)")
                    .setExpect("正常打通采集")
                    .setFailReason("没有事件采集到")
                    .setIsPass(false)
            );
        }

        mHandler.sendEmptyMessageDelayed(msg_jumpActivity,1000);

    }

    @Override
    public void jumpActivity() {
        super.jumpActivity();
        Intent i=new Intent(PageActivity.this, AnnotationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }
}