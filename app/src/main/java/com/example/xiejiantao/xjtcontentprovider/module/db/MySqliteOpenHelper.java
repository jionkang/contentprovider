package com.example.xiejiantao.xjtcontentprovider.module.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by xiejiantao on 2017/4/5.
 */

public class MySqliteOpenHelper  extends SQLiteOpenHelper{

    public final static int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "test.db";

    public static final String AUTHORITY = "com.netease.test.provider";


    public static final boolean DELETE_ROOM_DANMU = true;

    public static final String ROOM_DANMU = "room_danmu";// 视频上传信息

    public static final class DanmuColumns {
        public static final Uri DANMU_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ROOM_DANMU);

        public static final String ID = "id";

        public static final String CONTENT = "content";

        public static final String AVATAR = "avatar";

        public static final String TIME = "time";

        public static final String CREATER = "creater";

        public static final String ROOM_ID = "room_id";

        public static final String TITLE_TIME = "title_time";


    }


    public MySqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + ROOM_DANMU + " ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DanmuColumns.ID + " TEXT,"
                + DanmuColumns.AVATAR + " TEXT,"
                + DanmuColumns.TIME + " INTEGER NOT NULL DEFAULT 0,"
                + DanmuColumns.CONTENT + " TEXT,"
                + DanmuColumns.CREATER + " TEXT,"
                + DanmuColumns.TITLE_TIME + " INTEGER NOT NULL DEFAULT 0,"
                + DanmuColumns.ROOM_ID + " TEXT" + ");");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DELETE_ROOM_DANMU)
            db.execSQL("DROP TABLE IF EXISTS "
                    + ROOM_DANMU);
        onCreate(db);
    }
}
