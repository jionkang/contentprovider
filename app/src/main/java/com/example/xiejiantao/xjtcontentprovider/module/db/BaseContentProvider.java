package com.example.xiejiantao.xjtcontentprovider.module.db;

import com.netease.contentprovider.XjtContentProvider;

/**
 * Created by xiejiantao on 2017/4/5.
 */

public class BaseContentProvider extends XjtContentProvider {

    @Override
    public boolean onCreate() {
        mOpenHelper = new MySqliteOpenHelper(getContext());
        return true;
    }
}
