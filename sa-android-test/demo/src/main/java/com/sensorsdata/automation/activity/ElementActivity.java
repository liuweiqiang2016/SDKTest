package com.sensorsdata.automation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.sensorsdata.analytics.android.autotrack.aop.SensorsDataAutoTrackHelper;
import com.sensorsdata.analytics.android.sdk.ScreenAutoTracker;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.R;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;
import com.sensorsdata.automation.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ElementActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener, RatingBar.OnRatingBarChangeListener, AdapterView.OnItemSelectedListener, ScreenAutoTracker {

    //onClick的可以触发点击事件的view集合
    List<View> onClickViews=new ArrayList<>();
    private Button button;
    private ImageView imageView;
    private RatingBar ratingBar;
    private TextView textView;
    private MyListView listView;
    private Spinner spinner;
    long timer_result=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("全 View 页面");
        }
        intView();
        initListView();
        //忽略指定类型的view:RatingBar
        SensorsDataAPI.sharedInstance().ignoreViewType(RatingBar.class);
        //忽略指定对象view:spinner
        SensorsDataAPI.sharedInstance().ignoreView(spinner);
        //设置自定义属性
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("v_name","tv");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SensorsDataAPI.sharedInstance().setViewProperties(textView,jsonObject);
        //执行点击事件
        mHandler.sendEmptyMessageDelayed(msg_clickView,3000);
    }

    @Override
    public void clickView() {
        //触发 ViewOnClick 的控件点击事件
        for (View view:onClickViews){
            SensorsDataAutoTrackHelper.trackViewOnClick(view);
        }
        //listview 点击事件
        SensorsDataAutoTrackHelper.trackListView(listView,listView.getChildAt(2) , 2);
        //手动触发页面浏览
        SensorsDataAPI.sharedInstance().trackViewScreen(this);
        //手动触发点击并设置自定义属性
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("v_name","iv");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SensorsDataAPI.sharedInstance().trackViewAppClick(imageView,jsonObject);
        mHandler.sendEmptyMessageDelayed(msg_goBackGround,1000);
    }

    @Override
    public void goBackGround() {
        super.goBackGround();
        timer_result=System.currentTimeMillis()-Util.getStartTime(this);
        //忽略 ElementActivity 页面浏览
        SensorsDataAPI.sharedInstance().disableAutoTrack(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
        //11*1000 回到前台,确保 $AppEnd 触发（session 生效）
        mHandler.sendEmptyMessageDelayed(msg_goForeGround,11*1000);
    }

    @Override
    public void goForeGround() {
        super.goForeGround();
        //3000ms 后检验数据
        mHandler.sendEmptyMessageDelayed(msg_assertData,3000);
    }

    @Override
    public void assertData() {
        super.assertData();
        String eventName,url,screen_name,title,element_content,element_type,lib_method;
        if (jsonArray!=null){
            try {
                DataModel model0=Util.dataToModel(jsonArray.getJSONObject(0));
                eventName=model0.getEventName();
                url=model0.getProperties().getString("$url");
                screen_name=model0.getProperties().getString("$screen_name");
                title=model0.getProperties().getString("$title");
                lib_method=model0.getProperties().getString("$lib_method");
                //校验第一条事件是 $AppViewScreen 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第一条事件是 $AppViewScreen 且自定义属性正常")
                        .setApi("ScreenAutoTracker")
                        .setExpect("title:ele_title,url:ele_url,screen_name:ele_scr")
                        .setIsPass(eventName.equals("$AppViewScreen")
                                &&url.equals("ele_url")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                        ));

                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                eventName=model1.getEventName();
                screen_name=model1.getProperties().getString("$screen_name");
                title=model1.getProperties().getString("$title");
                lib_method=model1.getProperties().getString("$lib_method");
                element_content=model1.getProperties().getString("$element_content");
                element_type=model1.getProperties().getString("$element_type");
                //校验第二条事件是 Button $AppClick 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第二条事件是 Button $AppClick 且自定义属性正常")
                        .setApi("setAutoTrackEventType")
                        .setExpect("title:ele_title,url:ele_url,screen_name:ele_scr")
                        .setIsPass(eventName.equals("$AppClick")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                                &&element_content.equals("123456")
                                &&element_type.equals("Button")
                        ));

                DataModel model2=Util.dataToModel(jsonArray.getJSONObject(2));
                eventName=model2.getEventName();
                screen_name=model2.getProperties().getString("$screen_name");
                title=model2.getProperties().getString("$title");
                lib_method=model2.getProperties().getString("$lib_method");
                element_content=model2.getProperties().getString("$element_content");
                element_type=model2.getProperties().getString("$element_type");
                //校验第三条事件是 TextView $AppClick 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第三条事件是 TextView $AppClick 且自定义属性正常")
                        .setApi("setAutoTrackEventType")
                        .setExpect("v_name:tv")
                        .setIsPass(eventName.equals("$AppClick")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                                &&element_content.equals("TextView点击")
                                &&element_type.equals("TextView")
                                &&model2.getProperties().containsKey("v_name")
                                &&model2.getProperties().getString("v_name").equals("tv")
                        ));


                DataModel model3=Util.dataToModel(jsonArray.getJSONObject(3));
                eventName=model3.getEventName();
                screen_name=model3.getProperties().getString("$screen_name");
                title=model3.getProperties().getString("$title");
                lib_method=model3.getProperties().getString("$lib_method");
                element_content=model3.getProperties().getString("$element_content");
                element_type=model3.getProperties().getString("$element_type");
                //校验第四条事件是 ListView $AppClick 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第四条事件是 ListView $AppClick 且自定义属性正常")
                        .setApi("setAutoTrackEventType")
                        .setExpect("$element_position:2")
                        .setIsPass(eventName.equals("$AppClick")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                                &&element_content.equals("samsung")
                                &&element_type.equals("ListView")
                                &&model3.getProperties().getString("$element_position").equals("2")
                        ));

                DataModel model4=Util.dataToModel(jsonArray.getJSONObject(4));
                eventName=model4.getEventName();
                url=model4.getProperties().getString("$url");
                screen_name=model4.getProperties().getString("$screen_name");
                title=model4.getProperties().getString("$title");
                lib_method=model4.getProperties().getString("$lib_method");
                //校验第一条事件是 $AppViewScreen 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第五条事件是手动 $AppViewScreen")
                        .setApi("trackViewScreen")
                        .setExpect("$lib_method:code")
                        .setIsPass(eventName.equals("$AppViewScreen")
                                &&url.equals("ele_url")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("code")
                        ));


                DataModel model5=Util.dataToModel(jsonArray.getJSONObject(5));
                eventName=model5.getEventName();
                screen_name=model5.getProperties().getString("$screen_name");
                title=model5.getProperties().getString("$title");
                lib_method=model5.getProperties().getString("$lib_method");
                element_content=model5.getProperties().getString("$element_content");
                element_type=model5.getProperties().getString("$element_type");
                //校验第六条事件是 $AppClick 且自定义属性正常
                mCaseDao.insert(new TestCase()
                        .setName("校验第六条事件是手动 $AppClick")
                        .setApi("trackViewAppClick")
                        .setExpect("$lib_method:code")
                        .setIsPass(eventName.equals("$AppClick")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("code")
                                &&element_content.equals("imageView点击")
                                &&element_type.equals("ImageView")
                                &&model5.getProperties().containsKey("v_name")
                                &&model5.getProperties().getString("v_name").equals("iv")
                        ));


                DataModel model6=Util.dataToModel(jsonArray.getJSONObject(6));
                eventName=model6.getEventName();
                screen_name=model6.getProperties().getString("$screen_name");
                title=model6.getProperties().getString("$title");
                lib_method=model6.getProperties().getString("$lib_method");
                float duration=-10000;
                if (model6.getProperties().containsKey("event_duration")){
                    duration=model6.getProperties().getFloat("event_duration");
                }
                //校验第七条事件是 $AppEnd 且时长为误差不超过 2000ms
                mCaseDao.insert(new TestCase()
                        .setName("校验第七条事件是 $AppEnd")
                        .setApi("setAutoTrackEventType")
                        .setExpect("时长偏差在2000ms内")
                        .setIsPass(eventName.equals("$AppEnd")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                                &&Math.abs(timer_result-duration*1000)<2000
                        ));

                DataModel model7=Util.dataToModel(jsonArray.getJSONObject(7));
                eventName=model7.getEventName();
                screen_name=model7.getProperties().getString("$screen_name");
                title=model7.getProperties().getString("$title");
                lib_method=model7.getProperties().getString("$lib_method");

                mCaseDao.insert(new TestCase()
                        .setName("校验页面浏览忽略是否成功")
                        .setApi("disableAutoTrack")
                        .setExpect("页面浏览忽略成功")
                        .setIsPass(eventName.equals("$AppStart")
                                &&screen_name.equals("ele_scr")
                                &&title.equals("ele_title")
                                &&lib_method.equals("autoTrack")
                        ));
                //校验第八条事件是 $AppStart 且从后台启动
                mCaseDao.insert(new TestCase()
                        .setName("校验第八条事件是 $AppStart 且从后台启动")
                        .setApi("setAutoTrackEventType")
                        .setExpect("$AppStart")
                        .setIsPass(eventName.equals("$AppStart")
                                &&model7.getProperties().getBoolean("$resume_from_background")
                                &&!model7.getProperties().getBoolean("$is_first_time")
                                &&model7.getProperties().getBoolean("$is_first_day")
                        ));


                mHandler.sendEmptyMessageDelayed(msg_jumpActivity,3000);


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

    @Override
    public void jumpActivity() {
        super.jumpActivity();
        //恢复页面浏览采集
        List<SensorsDataAPI.AutoTrackEventType> eventTypeList=new ArrayList<>();
        eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
        SensorsDataAPI.sharedInstance().enableAutoTrack(eventTypeList);
        Intent i=new Intent(ElementActivity.this, ExposureActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);

    }

    void intView(){

        button=findViewById(R.id.ele_btn);
        button.setOnClickListener(
                (View v)-> {}
        );
        imageView=findViewById(R.id.ele_iv);
        imageView.setOnClickListener(this);
        textView=findViewById(R.id.ele_tv);
        textView.setOnClickListener(this);
        ratingBar=findViewById(R.id.ele_rbar);
        ratingBar.setOnRatingBarChangeListener(this);
        spinner=findViewById(R.id.ele_sp);
        spinner.setOnItemSelectedListener(this);
        //将符合条件的view加入list中
        addViewToList();
    }

    @Override
    public void onClick(View view) {
        Log.e("SA.ElementActivity", "onClick: "+view.toString());

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void initListView() {
        listView = findViewById(R.id.ele_lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public String getScreenUrl() {
        return "ele_url";
    }

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("$title","ele_title");
        jsonObject.put("$screen_name","ele_scr");
        return jsonObject;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //actionbar navigation up 按钮
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    void addViewToList(){
        onClickViews.add(button);
        onClickViews.add(ratingBar);
        onClickViews.add(textView);
    }
}