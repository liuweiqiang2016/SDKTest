package com.sensorsdata.automation.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TEST_CASE".
*/
public class TestCaseDao extends AbstractDao<TestCase, Long> {

    public static final String TABLENAME = "TEST_CASE";

    /**
     * Properties of entity TestCase.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, String.class, "time", false, "TIME");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Api = new Property(3, String.class, "api", false, "API");
        public final static Property Describe = new Property(4, String.class, "describe", false, "DESCRIBE");
        public final static Property Expect = new Property(5, String.class, "expect", false, "EXPECT");
        public final static Property Actuality = new Property(6, String.class, "actuality", false, "ACTUALITY");
        public final static Property IsPass = new Property(7, boolean.class, "isPass", false, "IS_PASS");
        public final static Property FailReason = new Property(8, String.class, "failReason", false, "FAIL_REASON");
        public final static Property Remark = new Property(9, String.class, "remark", false, "REMARK");
    }


    public TestCaseDao(DaoConfig config) {
        super(config);
    }
    
    public TestCaseDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TEST_CASE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME\" TEXT," + // 1: time
                "\"NAME\" TEXT," + // 2: name
                "\"API\" TEXT," + // 3: api
                "\"DESCRIBE\" TEXT," + // 4: describe
                "\"EXPECT\" TEXT," + // 5: expect
                "\"ACTUALITY\" TEXT," + // 6: actuality
                "\"IS_PASS\" INTEGER NOT NULL ," + // 7: isPass
                "\"FAIL_REASON\" TEXT," + // 8: failReason
                "\"REMARK\" TEXT);"); // 9: remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TEST_CASE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TestCase entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String api = entity.getApi();
        if (api != null) {
            stmt.bindString(4, api);
        }
 
        String describe = entity.getDescribe();
        if (describe != null) {
            stmt.bindString(5, describe);
        }
 
        String expect = entity.getExpect();
        if (expect != null) {
            stmt.bindString(6, expect);
        }
 
        String actuality = entity.getActuality();
        if (actuality != null) {
            stmt.bindString(7, actuality);
        }
        stmt.bindLong(8, entity.getIsPass() ? 1L: 0L);
 
        String failReason = entity.getFailReason();
        if (failReason != null) {
            stmt.bindString(9, failReason);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(10, remark);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TestCase entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(2, time);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String api = entity.getApi();
        if (api != null) {
            stmt.bindString(4, api);
        }
 
        String describe = entity.getDescribe();
        if (describe != null) {
            stmt.bindString(5, describe);
        }
 
        String expect = entity.getExpect();
        if (expect != null) {
            stmt.bindString(6, expect);
        }
 
        String actuality = entity.getActuality();
        if (actuality != null) {
            stmt.bindString(7, actuality);
        }
        stmt.bindLong(8, entity.getIsPass() ? 1L: 0L);
 
        String failReason = entity.getFailReason();
        if (failReason != null) {
            stmt.bindString(9, failReason);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(10, remark);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TestCase readEntity(Cursor cursor, int offset) {
        TestCase entity = new TestCase( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // api
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // describe
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // expect
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // actuality
            cursor.getShort(offset + 7) != 0, // isPass
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // failReason
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // remark
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TestCase entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setApi(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDescribe(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setExpect(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setActuality(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIsPass(cursor.getShort(offset + 7) != 0);
        entity.setFailReason(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setRemark(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TestCase entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TestCase entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TestCase entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}