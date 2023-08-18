package com.sensorsdata.automation.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sensorsdata.automation.MyApplication;
import com.sensorsdata.automation.greendao.TestCaseDao;
import com.sensorsdata.automation.utils.MyDatabaseHelper;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONArray;

import java.util.List;

public class BaseActivity extends AppCompatActivity {


    public MyDatabaseHelper mDatabaseHelper;
    //测试执行前数据库的数据条数、测试执行后数据库的数据条数
    public long beforeSize,afterSize;
    public TestCaseDao mCaseDao;
    public JSONArray jsonArray=null;
    public static final int msg_assertData=1,msg_jumpActivity=2,msg_goBackGround=3,msg_goForeGround=4,msg_postReport=5,msg_clickView=6,msg_otherAction=7;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper=new MyDatabaseHelper(this);
        //记录执行前数据条数
        beforeSize=mDatabaseHelper.getDataSize();
        Log.e("SA.BaseActivity", "beforeSize: "+beforeSize );
        mCaseDao= MyApplication.getDaoInstant().getTestCaseDao();
    }

    @SuppressLint("HandlerLeak")
    public final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==msg_assertData){
                assertData();
            }
            if (msg.what==msg_jumpActivity){
                jumpActivity();
            }
            if (msg.what==msg_goBackGround){
                goBackGround();
            }
            if (msg.what==msg_goForeGround){
                goForeGround();
            }
            if (msg.what==msg_postReport){
                postReport();
            }
            if (msg.what==msg_clickView){
                clickView();
            }
            if (msg.what==msg_otherAction){
                otherAction();
            }
        }
    };

    public void assertData(){
        //记录测试场景完成后，数据库条数，两者相差即为本次测试过程中生成的埋点数据
        afterSize=mDatabaseHelper.getDataSize();
        Log.e("SA.BaseActivity", "afterSize: "+afterSize );
        if (afterSize>beforeSize){
            //校验本次产生的数据是否正常
            jsonArray=mDatabaseHelper.getData(beforeSize,afterSize);
            if (jsonArray!=null){
                Util.assertJsonArray(jsonArray);
            }
        }
    }

    public void jumpActivity(){

    }

    //App 进入后台
    public void goBackGround(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }

    //App 回到前台
    public void goForeGround(){
        ActivityManager mAm = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                return;
            }
        }
    }

    public void postReport(){
        //生成报告
        Util.writeReportHtml(this);
        Log.e("SA.S", "================================writeReportHtml================================ " );
        //发布报告
        Util.postReport(this);
    }

    public void clickView(){

    }

    public void otherAction(){

    }

}
