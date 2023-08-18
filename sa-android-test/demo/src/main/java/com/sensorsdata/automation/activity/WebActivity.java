package com.sensorsdata.automation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreenAndAppClick;
import com.sensorsdata.automation.R;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

import java.util.Map;
import java.util.Set;


@SensorsDataIgnoreTrackAppViewScreenAndAppClick
public class WebActivity extends BaseActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        webView=findViewById(R.id.h5_web);
        mWebViewSetting();
        beforeSize=mDatabaseHelper.getDataSize();
        mHandler.sendEmptyMessageDelayed(msg_otherAction,1000);
    }

    @Override
    public void otherAction() {
        super.otherAction();
        webView.loadUrl("file:///android_asset/test.html");
        //数据校验时间间隔调长些，防止 H5 页面加载缓慢导致数据校验失败的问题
        mHandler.sendEmptyMessageDelayed(msg_assertData,5000);
    }

    //webview 设置
    private void mWebViewSetting(){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //打印Console日志
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("ConsoleMessage",consoleMessage.message());
                return true;
            }
        });

//        webSettings.setAllowFileAccess(true);// 设置允许访问文件数据
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setDatabaseEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("url:"+url);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        //修复中文乱码
        webSettings.setDefaultTextEncodingName("UTF-8");

//        //设置true,才能让Webivew支持<meta>标签的viewport属性
//        webSettings.setUseWideViewPort(true);
//        //设置可以支持缩放
//        webSettings.setSupportZoom(true);
//        //设置出现缩放工具
//        webSettings.setBuiltInZoomControls(true);
    }

    @Override
    public void assertData() {
        super.assertData();
        // jsonArray 为 null,说明没有打通
        if (jsonArray==null){
            //校验事件类型
            mCaseDao.insert(new TestCase()
                    .setName("校验 H5 是否正常打通")
                    .setApi("enableJavaScriptBridge(true)")
                    .setExpect("H5 正常打通")
                    .setActuality("H5 打通失败")
                    .setFailReason("H5 打通失败,没有数据入库")
                    .setIsPass(false)
            );
        }else{
            try {
                //$pageview
                DataModel model0= Util.dataToModel(jsonArray.getJSONObject(0));
                //校验打通事件 identities 及 distinct_id
                mCaseDao.insert(new TestCase()
                        .setName("校验打通事件 identities 及 distinct_id")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("distinct_id 为 AndroidID")
                        //包含 $identity_cookie_id，distinct_id 为 AndroidID
                        .setIsPass(model0.getIdentities().containsKey("$identity_cookie_id")&&
                                model0.getIdentities().getString("$identity_android_id").equals(Util.getValueFromData(jsonArray.getJSONObject(0),"distinct_id"))&&
                                Util.assertType("track",model0)&&
                                Util.assertEventName("$pageview",model0))
                );

                //校验打通事件属性是否正确
                mCaseDao.insert(new TestCase()
                        .setName("校验打通事件属性是否正确")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("打通事件属性正确")
                        .setIsPass(Util.isH5Data(jsonArray.getJSONObject(0)))
                );

                //"$first_visit_time": "2023-05-11 15:10:42.404",
                //"$first_referrer": "",
                //"$first_referrer_host": "",
                //"$first_browser_language": "zh-cn",
                //"$first_browser_charset": "UTF-8",
                //"$first_traffic_source_type": "直接流量",
                //"$first_search_keyword": "未取到值_直接打开"

                //校验打通数据 profile_set_once
                DataModel model1= Util.dataToModel(jsonArray.getJSONObject(1));
                mCaseDao.insert(new TestCase()
                        .setName("校验打通数据 profile_set_once")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("profile_set_once 正常触发")
                        .setIsPass(Util.assertType("profile_set_once",model1)&&
                                Util.hasValueFromData(jsonArray.getJSONObject(1),"_hybrid_h5")&&
                                model1.getProperties().size()==6
                        )
                );

                //校验打通数据 trackJS
                DataModel model2= Util.dataToModel(jsonArray.getJSONObject(2));
                mCaseDao.insert(new TestCase()
                        .setName("校验打通数据 trackJS")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("打通事件事件名及属性正确")
                        .setIsPass(Util.isH5Data(jsonArray.getJSONObject(2))
                                &&Util.assertEventName("trackJS",model2))
                );

                //校验打通后绑定业务 ID
                DataModel model3= Util.dataToModel(jsonArray.getJSONObject(3));
                mCaseDao.insert(new TestCase()
                        .setName("校验打通后绑定业务 ID")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("事件名、类型、业务 ID 正常")
                        .setIsPass(Util.isH5Data(jsonArray.getJSONObject(3))
                                &&Util.assertEventName("$BindID",model3)
                                &&Util.assertType("track_id_bind",model3)
                                &&model3.getIdentities().getString("jsBind").equals("666"))
                );

                //校验打通后解绑业务 ID
                DataModel model4= Util.dataToModel(jsonArray.getJSONObject(4));
                mCaseDao.insert(new TestCase()
                        .setName("校验打通后解绑业务 ID")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("事件名、类型、业务 ID 正常")
                        .setIsPass(Util.isH5Data(jsonArray.getJSONObject(4))
                                &&Util.assertEventName("$UnbindID",model4)
                                &&Util.assertType("track_id_unbind",model4)
                                &&model4.getIdentities().getString("jsBind").equals("666"))
                );

                //校验打通后登陆事件
                DataModel model5= Util.dataToModel(jsonArray.getJSONObject(5));
                mCaseDao.insert(new TestCase()
                        .setName("校验打通后登陆事件")
                        .setApi("enableJavaScriptBridge(true)")
                        .setExpect("事件名、类型、业务 ID 正常")
                        .setIsPass(Util.isH5Data(jsonArray.getJSONObject(5))
                                &&Util.assertEventName("$SignUp",model5)
                                &&Util.assertType("track_signup",model5)
                                &&Util.getValueFromData(jsonArray.getJSONObject(5),"login_id").equals("jsID"))
                );

                //注销登陆
                SensorsDataAPI.sharedInstance().logout();
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
        //校验完数据，开始跳转
//        mHandler.sendEmptyMessageDelayed(msg_jumpActivity,1000);
        //写发布测试报告
        mHandler.sendEmptyMessageDelayed(msg_postReport,1000);
    }

    @Override
    public void jumpActivity() {
        super.jumpActivity();
    }

    @Override
    public void postReport() {
        //验证所有 API 是否正常
        Util.apiTest(this);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.postReport();
    }
}