package com.example.hii.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by hii on 2/5/2017.
 */

public class DAL {
    DBConnect dbConnect;

    public DAL(Context context) {
        dbConnect=new DBConnect(context);
    }

    //add Country
    public Long insertCountry(DataBean dataBean){
        SQLiteDatabase sqLiteDatabase=dbConnect.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("city",dataBean.getCity());
        contentValues.put("province",dataBean.getProvince());
        sqLiteDatabase.insert("country",null,contentValues);
        sqLiteDatabase.close();
        return (long)0;
    }

    //get All countries
    public ArrayList<DataBean> getAllCountries(){
        ArrayList<DataBean> arrayList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=dbConnect.getWritableDatabase();
        String query="SELECT * FROM country";
        Cursor cursor=sqLiteDatabase.rawQuery(query,null);
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                DataBean dataBean=new DataBean();
                dataBean.setID(cursor.getInt(0));
                dataBean.setCity(cursor.getString(1));
                dataBean.setProvince(cursor.getString(2));
                arrayList.add(dataBean);
            }
        }
        sqLiteDatabase.close();
        return arrayList;
    }

    // Get single Country
    public DataBean getCountry(long id) {
        SQLiteDatabase sqLiteDatabase=dbConnect.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM country WHERE ID="+id,null);
        if (cursor != null)
            cursor.moveToFirst();
        DataBean dataBean=new DataBean(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
        return dataBean;
    }

    // Delete Country
    public long updateCountry(DataBean dataBean) {
        SQLiteDatabase sqLiteDatabase=dbConnect.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("city",dataBean.getCity());
        contentValues.put("province",dataBean.getProvince());
        sqLiteDatabase.update("country",contentValues,"ID="+dataBean.getID(),null);
        sqLiteDatabase.close();
        return (long)0;
    }
    // Delete Country
    public void removeCountry(DataBean dataBean) {
        SQLiteDatabase sqLiteDatabase=dbConnect.getWritableDatabase();
        sqLiteDatabase.delete("country", "ID =" + dataBean.getID(), null);
    }
}
