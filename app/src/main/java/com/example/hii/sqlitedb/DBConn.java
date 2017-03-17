package com.example.hii.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owais on 3/17/2017.
 */
public class DBConn extends SQLiteOpenHelper {
    public static String DB_NAME="Practice1";
    public static int DB_VERSION=1;
    String query="CREATE TABLE IF NOT EXISTS country(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,city TEXT,province TEXT)";

    public DBConn(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        // Log.d("DB Path",context.getDatabasePath(DB_NAME).getPath());
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS country");
        sqLiteDatabase.execSQL(query);
    }
}