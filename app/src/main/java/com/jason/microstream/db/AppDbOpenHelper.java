package com.jason.microstream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.jason.microstream.db.entity.generator.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class AppDbOpenHelper extends DaoMaster.OpenHelper {

    public AppDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

    }
}