package com.sensorsdata.automation.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amitshekhar.DebugDB;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.MyApplication;
import com.sensorsdata.automation.activity.AnnotationActivity;
import com.sensorsdata.automation.activity.ElementActivity;
import com.sensorsdata.automation.activity.ExposureActivity;
import com.sensorsdata.automation.activity.MainActivity;
import com.sensorsdata.automation.activity.PageActivity;
import com.sensorsdata.automation.activity.WebActivity;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.greendao.TestCaseDao;
import com.sensorsdata.automation.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ControlService extends IntentService {

    public ControlService() {
        super("ControlService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //执行用例前，需要清空测试用例 DB
        Util.clearCaseDB();
        //获取测试用例集，并执行测试工作
        for(String name: Util.getClassName(this,Util.CASES_PACKAGENAME)){
            //通过类名反射获取用例对象
            BaseCase baseCase=(BaseCase) Util.newInstance(name);
            try {
                baseCase.execute(ControlService.this);
            }catch (Throwable throwable){
                //若用例出现崩溃，存储到测试用例 DB 信息
                TestCaseDao mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
                TestCase testCase=new TestCase();
                testCase.setName("校验调用 SDK 接口是否出现崩溃");
                testCase.setDescribe("校验调用 SDK 接口是否出现崩溃");
                testCase.setExpect("SDK 正常运行");
                testCase.setActuality("SDK 出现崩溃");
                testCase.setIsPass(false);
                testCase.setFailReason("SDK 出现崩溃");
                testCase.setRemark("崩溃信息："+Util.getThrowableInfo(throwable));
                mCaseDao.insert(testCase);
            }
        }
//        Log.e("DB", "DBAddress:"+ DebugDB.getAddressLog());
        //执行一些与 Activity 强相关的用例,其中 ElementActivity 为这些 Activity 入口
        Intent i=new Intent(ControlService.this, ElementActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }

}