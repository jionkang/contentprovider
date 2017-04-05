package com.example.xiejiantao.xjtcontentprovider.module.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by xiejiantao on 2017/4/5.
 */

public class DanmuHelper {
    public static long danmuTitleTime(Context context,String roomId,long currentTime) {
        String where = MySqliteOpenHelper.DanmuColumns.ROOM_ID + "=?";
        String[] args = {roomId};
        Cursor cursor = context.getContentResolver().query(MySqliteOpenHelper.DanmuColumns.DANMU_CONTENT_URI, null, where, args, null);
        if (cursor==null){
            return currentTime;
        }
        if (cursor != null&&cursor.getCount()<=0){
            cursor.close();
            return currentTime;
        }
        if (cursor != null && cursor.moveToLast()) {
            long preTime=cursor.getLong(cursor.getColumnIndex(MySqliteOpenHelper.DanmuColumns.TIME));
            cursor.close();
//            if (!Utils.isSameDay(preTime,currentTime)){
                return currentTime;
//            }
        }
        return 0;
    }
}
