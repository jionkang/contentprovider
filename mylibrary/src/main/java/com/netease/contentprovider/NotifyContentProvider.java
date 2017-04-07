package com.netease.contentprovider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by xiejiantao on 2017/4/5.
 */

public abstract class NotifyContentProvider extends ContentProvider {
    public static final String PARAMETER_NOTIFY = "notify";

    private static final String PARAMETER_TYPE = "type";

    protected SQLiteOpenHelper mOpenHelper;

    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = null;
        Cursor result = null;
        try {
            db = mOpenHelper.getWritableDatabase();
            result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
            if (result != null) {
                result.setNotificationUri(getContext().getContentResolver(), uri);
            }
        } catch (SQLiteException e) {
            if (result != null) {
                result.close();
                result = null;
            }
        }
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, initialValues);
        if (rowId <= 0)
            return null;

        uri = ContentUris.withAppendedId(uri, rowId);

        if (!db.inTransaction()) {
            sendNotify(uri);
        }

        return uri;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        Uri uri = null;
        try {
            if (operations.size() > 0) {
                ContentProviderOperation operation = operations.get(0);
                uri = operation.getUri();
            }
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
            if (uri != null) {
                sendNotify(uri);
            }
        }

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int nums = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            nums = super.bulkInsert(uri, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        sendNotify(uri);
        return nums;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        try {
            count = db.delete(args.table, args.where, args.args);
        } catch (SQLiteException e) {
        }
        sendNotify(uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count = 0;
        try {
            count = db.update(args.table, values, args.where, args.args);
        } catch (SQLiteException e) {
        }

        sendNotify(uri);
        return count;
    }

    private void sendNotify(final Uri uri) {
        try {
            Thread.currentThread().sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    public static class SqlArguments {
        public final String table;

        public final String where;

        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            int size = url.getPathSegments().size();
            if (size == 1 || size == 2) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

    public static ContentValues getContentValues(String[] keys, String[] values) {
        if (keys == null || values == null)
            return null;
        if (keys.length == 0 || values.length == 0)
            return null;
        int klen = keys.length;
        int vlen = values.length;
        int max = (klen < vlen) ? klen : vlen;
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < max; i++) {
            String key = keys[i];
            String value = values[i];
            contentValues.put(key, value);
        }
        return contentValues;
    }

    public static ContentValues getContentValues(ContentValues contentValues, String[] keys, String[] values) {
        if (keys == null || values == null)
            return null;
        if (keys.length == 0 || values.length == 0)
            return null;
        int klen = keys.length;
        int vlen = values.length;
        int max = (klen < vlen) ? klen : vlen;
        if (contentValues == null)
            contentValues = new ContentValues();
        for (int i = 0; i < max; i++) {
            String key = keys[i];
            String value = values[i];
            contentValues.put(key, value);
        }
        return contentValues;
    }

    public static ContentProviderOperation.Builder getBuilder(String[] keys, Object[] values, ContentProviderOperation.Builder builder) {
        if (keys == null || values == null || builder == null)
            return null;
        if (keys.length == 0 || values.length == 0)
            return null;
        int klen = keys.length;
        int vlen = values.length;
        int max = (klen < vlen) ? klen : vlen;
        for (int i = 0; i < max; i++) {
            String key = keys[i];
            Object value = values[i];
            builder.withValue(key, value);
        }
        return builder;
    }

    public static boolean getBoolean(Cursor cursor, String columnStr) {
        try {
            String str = cursor.getString(cursor.getColumnIndex(columnStr));
            return Boolean.valueOf(str);
        } catch (Exception e) {
        }
        return false;
    }
}
