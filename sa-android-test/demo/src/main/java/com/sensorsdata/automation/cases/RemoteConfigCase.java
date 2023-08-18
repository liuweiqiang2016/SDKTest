package com.sensorsdata.automation.cases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sensorsdata.automation.BaseCase;
import com.sensorsdata.automation.greendao.TestCase;

public class RemoteConfigCase extends BaseCase {

    int random_value=-1;
    @Override
    public void beforeTest() {
        super.beforeTest();
    }

    @Override
    public void Test() {
        super.Test();
        SharedPreferences sp=mContext.getSharedPreferences("sensorsdata", Context.MODE_PRIVATE);
        random_value=sp.getInt("sensorsdata.request.time.random",-1);
    }

    @Override
    public void afterTest() {
        super.afterTest();
        boolean result= random_value >= 6 && random_value <= 10;
        //校验事件名
        mCaseDao.insert(new TestCase()
                .setName("校验远程配置请求间隔设置是否生效")
                .setApi("setMinRequestInterval&setMaxRequestInterval")
                .setExpect("间隔在 [6,10] 之间")
                .setActuality("请求间隔为"+random_value)
                .setIsPass(result));
    }
}
