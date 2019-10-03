package com.example.admin.browser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DlDbHelpBussiness {
    private static DlDbHelpBussiness ddb= null;
    private MyDSQLite dbHelper;
    private DlDbHelpBussiness(Context context){
        dbHelper = new MyDSQLite(context);
    }
    public static DlDbHelpBussiness getInstance(Context context){
        if(ddb==null){
            ddb = new DlDbHelpBussiness(context);
        }
        return ddb;
    }
    public MyDSQLite getDbHelper(){
        return dbHelper;
    }
}
class MyDSQLite extends SQLiteOpenHelper {
    public MyDSQLite(Context context) {
        super(context, "download_db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table download_db(_id integer primary key autoincrement, url varchar,fileName nvarchar,start int,fin int,now int)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void insert(SQLiteDatabase db,String url,String fileName,int start,int fin,int now){
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("fileName", fileName);
        values.put("start", start);
        values.put("fin", fin);
        values.put("now", now);
        db.insert("download_db", null, values);
    }
    public void clean(SQLiteDatabase db){
        db.execSQL("delete from download_db");
    }
    public void change(SQLiteDatabase db,int now,int count){
        String s = "update download_db set now = " + now + " where _id = " + count;
        db.execSQL(s);
    }
    public void del(SQLiteDatabase db,int id){
        String s = "delete from download_db where _id  = " + id;
        db.execSQL(s);
    }
}