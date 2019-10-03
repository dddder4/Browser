package com.example.admin.browser;

import android.content.*;
import android.database.sqlite.*;

public class DbHelpBussiness {
    private static DbHelpBussiness db= null;
    private MySQLite dbHelper;
    private DbHelpBussiness(Context context){
        dbHelper = new MySQLite(context);
    }
    public static DbHelpBussiness getInstance(Context context){
        if(db==null){
            db = new DbHelpBussiness(context);
        }
        return db;
    }
    public MySQLite getDbHelper(){
        return dbHelper;
    }
}
class MySQLite extends SQLiteOpenHelper {
    public MySQLite(Context context) {
        super(context, "history_db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table history_db(_id integer primary key autoincrement, title nvarchar)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void insert(SQLiteDatabase db,String title){
        ContentValues values = new ContentValues();
        values.put("title", title);
        db.insert("history_db", null, values);
        db.close();
    }
    public void delete(SQLiteDatabase db){
        db.execSQL("delete from history_db");
    }
}