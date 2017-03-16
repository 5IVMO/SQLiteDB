package com.example.hii.sqlitedb;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Owais on 3/16/2017.
 */
public class Util{

    public static void ToastShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    public static void ToastLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
