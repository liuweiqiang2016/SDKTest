package com.sensorsdata.automation.cases;


import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;
import com.sensorsdata.automation.model.DataModel;
import com.sensorsdata.automation.utils.Util;

import org.json.JSONException;

public class SetGetCase extends BaseCase {

    long maxSize;
    int flush_Interval,flush_size,session_time;
    String cookie,serverUrl;
    boolean isEnable;


    @Override
    public void beforeTest() {
        super.beforeTest();
    }

    @Override
    public void Test() {
        super.Test();
        //数据库最大缓存大小
        SensorsDataAPI.sharedInstance().setMaxCacheSize(60*1024*1024);
        maxSize=SensorsDataAPI.sharedInstance().getMaxCacheSize();
        //数据上报间隔
        SensorsDataAPI.sharedInstance().setFlushInterval(6*1000);
        flush_Interval=SensorsDataAPI.sharedInstance().getFlushInterval();
        //数据上报条数
        SensorsDataAPI.sharedInstance().setFlushBulkSize(60);
        flush_size=SensorsDataAPI.sharedInstance().getFlushBulkSize();
        //session 时长设置
        SensorsDataAPI.sharedInstance().setSessionIntervalTime(10*1000);
        session_time=SensorsDataAPI.sharedInstance().getSessionIntervalTime();
        //数据上报 http cookie
        SensorsDataAPI.sharedInstance().setCookie("testId=1234",true);
        cookie=SensorsDataAPI.sharedInstance().getCookie(true);
        //设置数据接收地址
        SensorsDataAPI.sharedInstance().setServerUrl("http://10.120.111.143:8106/sa?project=default");
        serverUrl=SensorsDataAPI.sharedInstance().getServerUrl();
        //SDK 是否允许网络请求
        SensorsDataAPI.sharedInstance().enableNetworkRequest(true);
        isEnable=SensorsDataAPI.sharedInstance().isNetworkRequestEnable();
    }

    @Override
    public void afterTest() {
        super.afterTest();

        //校验数据库最大缓存设置
        mCaseDao.insert(new TestCase()
                .setName("校验数据库最大缓存设置")
                .setApi("setMaxCacheSize&getMaxCacheSize")
                .setExpect("60*1024*1024")
                .setActuality(maxSize+"")
                .setIsPass(maxSize==60*1024*1024)
        );

        //校验数据上报间隔设置
        mCaseDao.insert(new TestCase()
                .setName("校验数据上报间隔设置")
                .setApi("setFlushInterval&getFlushInterval")
                .setExpect("6000")
                .setActuality(flush_Interval+"")
                .setIsPass(flush_Interval==6000)
        );

        //校验数据上报条数设置
        mCaseDao.insert(new TestCase()
                .setName("校验数据上报条数设置")
                .setApi("setFlushBulkSize&getFlushBulkSize")
                .setExpect("60")
                .setActuality(flush_size+"")
                .setIsPass(flush_size==60)
        );

        //校验 session 时长设置
        mCaseDao.insert(new TestCase()
                .setName("校验 session 时长设置")
                .setApi("setSessionIntervalTime&getSessionIntervalTime")
                .setExpect("10*1000")
                .setActuality(session_time+"")
                .setIsPass(session_time==10*1000)
        );

        //校验 cookie 设置
        mCaseDao.insert(new TestCase()
                .setName("校验 cookie 设置")
                .setApi("setCookie&getCookie")
                .setExpect("testId=1234")
                .setActuality(cookie)
                .setIsPass(cookie.equals("testId=1234"))
                .setRemark(cookie)
        );

        //校验数据接收地址设置
        mCaseDao.insert(new TestCase()
                .setName("校验数据接收地址设置")
                .setApi("setServerUrl&getServerUrl")
                .setExpect("http://10.120.111.143:8106/sa?project=default")
                .setActuality(serverUrl)
                .setIsPass(serverUrl.equals("http://10.120.111.143:8106/sa?project=default"))
        );

        //校验 SDK 网络请求设置
        mCaseDao.insert(new TestCase()
                .setName("校验 SDK 网络请求设置")
                .setApi("enableNetworkRequest&isNetworkRequestEnable")
                .setExpect("true")
                .setActuality(isEnable+"")
                .setIsPass(isEnable)
        );


    }
}
