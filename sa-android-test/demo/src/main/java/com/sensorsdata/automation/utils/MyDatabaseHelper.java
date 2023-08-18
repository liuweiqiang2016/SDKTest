package com.sensorsdata.automation.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sensorsdata.analytics.android.sdk.SALog;

import org.json.JSONArray;
import org.json.JSONObject;

/*
* 构建 SDK 所有事件的数据库 SQLiteOpenHelper，由 SDK 源码复制而来，方便后续查找数据
* */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SA.SQLiteOpenHelper";
    private static final String CREATE_EVENTS_TABLE = String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER NOT NULL);", "events", "data", "created_at");
    private static final String EVENTS_TIME_INDEX = String.format("CREATE INDEX IF NOT EXISTS time_idx ON %s (%s);", "events", "created_at");

    private Context mContext;
    public MyDatabaseHelper(Context context) {
        super(context, "sensorsdata", (SQLiteDatabase.CursorFactory)null, 6);
        mContext=context;
    }

    public void onCreate(SQLiteDatabase db) {
        SALog.i("SA.SQLiteOpenHelper", "Creating a new Sensors Analytics DB");
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(EVENTS_TIME_INDEX);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SALog.i("SA.SQLiteOpenHelper", "Upgrading app, replacing Sensors Analytics DB");
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", "events"));
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(EVENTS_TIME_INDEX);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("events",null,null);
        db.close();
    }

    public  JSONArray getAllData() {
        JSONArray arr = new JSONArray();
        Cursor c = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            c = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s", "events", "created_at"), (String[])null);
            while(c.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("created_at", c.getString(c.getColumnIndex("created_at")));
                jsonObject.put("data", c.getString(c.getColumnIndex("data")));
                jsonObject.put("_id", c.getString(c.getColumnIndex("_id")));
                arr.put(jsonObject);
            }
        } catch (Exception var8) {
            SALog.printStackTrace(var8);
        } finally {
            this.close();
            if (c != null) {
                c.close();
            }
        }

        return arr;
    }


    /**
     * 根据首尾，查找数据，得到的结果包含尾,不包含首
     * @param start 数据库开始位置
     * @param end 数据库结束位置
     * @return 符合该范围内的数据
     */
    public  JSONArray getData(long start,long end) {
        JSONArray arr = new JSONArray();
        Cursor c = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            c = db.rawQuery(String.format("SELECT * FROM %s WHERE %s > %s AND %s <= %s ORDER BY %s", "events","_id",start,"_id",end,"created_at"), (String[])null);
            while(c.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("_id", c.getString(c.getColumnIndex("_id")));
                jsonObject.put("created_at", c.getString(c.getColumnIndex("created_at")));
                jsonObject.put("data", c.getString(c.getColumnIndex("data")));
                arr.put(jsonObject);
            }
        } catch (Exception var8) {
            SALog.printStackTrace(var8);
        } finally {
            this.close();
            if (c != null) {
                c.close();
            }
        }

        return arr;
    }


    public long getDataSize(){

        JSONArray arr = new JSONArray();
        Cursor c = null;
        long count=0;

        //select count(*) from info
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            c = db.rawQuery(String.format("SELECT COUNT(*) FROM %s ORDER BY %s", "events", "created_at"), (String[])null);
            c.moveToFirst();
            count = c.getLong(0);

        } catch (Exception var8) {
            SALog.printStackTrace(var8);
        } finally {
            this.close();
            if (c != null) {
                c.close();
            }

        }
        return count;
    }
}
