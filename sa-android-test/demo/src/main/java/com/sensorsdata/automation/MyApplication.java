package com.sensorsdata.automation;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.sensorsdata.automation.greendao.DaoMaster;
import com.sensorsdata.automation.greendao.DaoSession;

public class MyApplication extends Application {

    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        setupDatabase();
    }

    /**
     * 配置数据库
     */

    private void setupDatabase() {
        //创建数据库shop.db 创建SQLite数据库的SQLiteOpenHelper的具体实现
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "testcase", null);
        //获取SQLiteDatabase对象
        SQLiteDatabase db = helper.getReadableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取 DaoSession 外部调用
     * */

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

}
