package com.sensorsdata.automation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.greendao.TestCaseDao;
import com.sensorsdata.automation.model.PropertyModel;
import com.sensorsdata.automation.model.PropertyType;
import com.sensorsdata.automation.utils.MyDatabaseHelper;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseCase {

    public Context mContext;
    public MyDatabaseHelper mDatabaseHelper;
    //测试执行前数据库的数据条数、测试执行后数据库的数据条数
    public long beforeSize,afterSize;
    public String TAG="SA.S:BaseCase";
    public TestCaseDao mCaseDao;
    public JSONArray jsonArray=null;
    public long wait_time=3000;
    //自定义属性
    public List<PropertyModel> list;
    public JSONObject property;

    public void execute(Context context){
        this.mContext=context;
        mDatabaseHelper=new MyDatabaseHelper(mContext);
        mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
        beforeTest();
        Test();
        afterTest();
    }

    /**
     * 测试前要做的事情
     */
    public void beforeTest(){
        //记录执行前数据条数
        beforeSize=mDatabaseHelper.getDataSize();
        Log.e(TAG, "beforeTest: "+beforeSize );
        //自定义属性
        //自定义属性
        list=new ArrayList<>();
        list.add(new PropertyModel(PropertyType.STRING,"name","tom"));
        list.add(new PropertyModel(PropertyType.NUMBER,"age",30));
        list.add(new PropertyModel(PropertyType.BOOL,"isStudent",true));
        list.add(new PropertyModel(PropertyType.STRING,"reg_time","2022-06-18 18:00:00"));
        List<String> cars=new ArrayList<>();
        cars.add("BMW");
        cars.add("BYD");
        cars.add("AUDI");
        list.add(new PropertyModel(PropertyType.LIST,"cars",cars));
        property = Util.makeJSONObject(list);
        //执行测试前，缓冲时间
        try {
            Thread.sleep(wait_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试中的逻辑,主要是完成采集数据的行为操作，例如页面跳转，采集全埋点数据；调用接口，采集手动埋点数据等
     */
    public void Test(){

    }

    /**
     * 埋点数据采集完成后的逻辑
     */
    public void afterTest(){
        //数据校验前，缓冲时间
        try {
            Thread.sleep(wait_time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //记录测试场景完成后，数据库条数，两者相差即为本次测试过程中生成的埋点数据
        afterSize=mDatabaseHelper.getDataSize();
        Log.e(TAG, "afterSize: "+afterSize );
        if (afterSize>beforeSize){
            //校验本次产生的数据是否正常
            jsonArray= mDatabaseHelper.getData(beforeSize,afterSize);
            Util.assertJsonArray(jsonArray);
        }

    }



}
