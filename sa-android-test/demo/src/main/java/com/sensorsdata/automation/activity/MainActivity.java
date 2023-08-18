package com.sensorsdata.automation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen;
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreenAndAppClick;
import com.sensorsdata.automation.R;
import com.sensorsdata.automation.adapter.InfoAdapter;
import com.sensorsdata.automation.cases.RemoteConfigCase;
import com.sensorsdata.automation.model.MessageEvent;
import com.sensorsdata.automation.service.ControlService;
import com.sensorsdata.automation.utils.SDK;
import com.sensorsdata.automation.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


import pub.devrel.easypermissions.EasyPermissions;

@SensorsDataIgnoreTrackAppViewScreenAndAppClick
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //需要申请的敏感权限(使用 adb install -g .apk 命令安装 app，会自动授予所有权限)
    private String[] permissions={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private Button btn1,btn2;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //更新分支名称
        updateBranchName();
        applyPermission();
        SDK.initSA(this);
        btn1= findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2= findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        listView=findViewById(R.id.main_lv);
        listView.setAdapter(new InfoAdapter(this));
        //记录启动时间
        Util.setStartTime(this);
        //注册事件
        EventBus.getDefault().register(this);
        //启动测试服务
        mHandler.sendEmptyMessageDelayed(1,5000);
    }

    //敏感权限申请
    private void applyPermission(){
        if(!EasyPermissions.hasPermissions(this,permissions)){
            EasyPermissions.requestPermissions(this,"拒绝相关权限，app无法正常使用",0,permissions);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.btn1){
//            new RemoteConfigCase().execute(this);
            Intent i=new Intent(MainActivity.this, AnnotationActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(i);

        }
        if (view.getId()==R.id.btn2){
           Util.writeReportHtml(this);
        }
        //导出文件到电脑，若文件已存在则直接覆盖
        // adb pull /sdcard/测试报告.html  /Users/weiqiangliu/Downloads
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Intent intent=new Intent(MainActivity.this, ControlService.class);
                startService(intent);
            }
        }
    };

    //此处测试代码需要迁移
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent messageEvent){
//        if (messageEvent.getMsg().equals("AppClick")){
//            SensorsDataAutoTrackHelper.trackViewOnClick(btn1);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().unregister(this);
    }

    private void updateBranchName(){
        Intent intent=getIntent();
        String branchName=intent.getStringExtra("branchName");
        Util.UpdateBranchName(this,branchName);
    }
}