package com.sensorsdata.automation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreenAndAppClick;
import com.sensorsdata.analytics.android.sdk.core.business.exposure.SAExposureData;
import com.sensorsdata.automation.R;
import com.sensorsdata.automation.adapter.SimpleAdapter;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@SensorsDataIgnoreTrackAppViewScreenAndAppClick
public class ExposureActivity extends BaseActivity  {

    Button button;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exposure);
        button=findViewById(R.id.exposure_btn);
        //对 button 添加曝光监听，全局曝光
        SAExposureData exposureData=new SAExposureData("exposure_btn");
        SensorsDataAPI.sharedInstance().addExposureView(button,exposureData);
        //listview 添加自定义曝光配置
        listView=findViewById(R.id.exposure_lv);
        List<String> list=new ArrayList<>();
        list.add("apple");
        list.add("huawei");
        list.add("xiaomi");
        list.add("oppo");
        listView.setAdapter(new SimpleAdapter(this,list));
        //数据校验时间间隔调长些，避免曝光事件没有触发,进入后台
        mHandler.sendEmptyMessageDelayed(msg_goBackGround,5000);
    }

    @Override
    public void goBackGround() {
        super.goBackGround();
        //移除 button 监听
        SensorsDataAPI.sharedInstance().removeExposureView(button);
        mHandler.sendEmptyMessageDelayed(msg_goForeGround,2000);
    }

    @Override
    public void goForeGround() {
        super.goForeGround();
        //进入前台，检验数据
        mHandler.sendEmptyMessageDelayed(msg_assertData,5000);
    }

    @Override
    public void assertData() {
        String eventName;
        if (jsonArray!=null){
            try {
                DataModel model0=Util.dataToModel(jsonArray.getJSONObject(0));
                eventName=model0.getEventName();
                //校验第一条事件是 exposure_btn 证明全局曝光配置生效
                mCaseDao.insert(new TestCase()
                        .setName("校验全局曝光配置是否生效")
                        .setApi("setExposureConfig")
                        .setExpect("全局曝光配置生效,事件名为:exposure_btn")
                        .setActuality("事件名为:"+eventName)
                        .setIsPass(eventName.equals("exposure_btn")));
                //校验第一条事件的属性完整、准确
                mCaseDao.insert(new TestCase()
                        .setName("校验全局曝光配置事件属性是否完整")
                        .setApi("setExposureConfig")
                        .setExpect("包含属性$screen_name、$title、$element_path、$element_content、$element_type")
                        .setActuality("属性为:"+model0.getProperties())
                        .setIsPass(model0.getProperties().containsKey("$screen_name")
                                &&model0.getProperties().containsKey("$title")
                                &&model0.getProperties().containsKey("$element_path")
                                &&model0.getProperties().containsKey("$element_content")
                                &&model0.getProperties().containsKey("$element_type")
                        ));
                //校验第二条事件是 exposure_item 证明自定义曝光配置生效
                DataModel model1=Util.dataToModel(jsonArray.getJSONObject(1));
                eventName=model1.getEventName();
                mCaseDao.insert(new TestCase()
                        .setName("校验自定义曝光配置是否生效")
                        .setApi("addExposureView")
                        .setExpect("自定义曝光配置生效,事件名为:exposure_item")
                        .setActuality("事件名为:"+eventName)
                        .setIsPass(eventName.equals("exposure_item")));
                //校验第二条事件的属性完整、准确
                mCaseDao.insert(new TestCase()
                        .setName("校验自定义曝光配置事件属性是否完整")
                        .setApi("addExposureView")
                        .setExpect("包含属性$screen_name、$title、$element_path、$element_content、$element_type、$element_position")
                        .setActuality("属性为:"+model1.getProperties())
                        .setIsPass(model1.getProperties().containsKey("$screen_name")
                                &&model1.getProperties().containsKey("$title")
                                &&model1.getProperties().containsKey("$element_path")
                                &&model1.getProperties().containsKey("$element_content")
                                &&model1.getProperties().containsKey("$element_type")
                                &&model1.getProperties().containsKey("$element_position")
                        ));
                //校验第二条事件的曝光自定义属性生效
                mCaseDao.insert(new TestCase()
                        .setName("校验曝光配置事件自定义属性")
                        .setExpect("item_pos")
                        .setApi("addExposureView")
                        .setActuality("属性item_pos为:"+model1.getProperties().getIntValue("item_pos"))
                        .setIsPass(model1.getProperties().containsKey("item_pos")
                                &&model1.getProperties().getIntValue("item_pos")==2));

                //校验第三条事件是 exposure_item 证明重复曝光生效
                DataModel model2=Util.dataToModel(jsonArray.getJSONObject(2));
                eventName=model2.getEventName();
                mCaseDao.insert(new TestCase()
                        .setName("校验自定义曝光配置是否生效")
                        .setApi("addExposureView")
                        .setExpect("自定义曝光配置生效,事件名为:exposure_item")
                        .setActuality("事件名为:"+eventName)
                        .setIsPass(eventName.equals("exposure_item")));

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
        super.jumpActivity();
        Intent i=new Intent(ExposureActivity.this, PageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }
}