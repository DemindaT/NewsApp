package com.education.assignmentsqlite.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.education.assignmentsqlite.App;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks_db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper databaseHelper;
    public  static String TABLE_TASKS="TABLE_TASKS";
    public  static String  TASK_NAME="TASK_BODY";
    public  static String  TASK_BODY="TASK_NAME";

    SQLiteDatabase sqLiteDatabase;
    public DatabaseHelper() {
        super(App.context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance() {

        if (databaseHelper == null) {
            synchronized (DatabaseHelper.class){ //thread safe singleton
                if (databaseHelper == null)
                    databaseHelper = new DatabaseHelper();
            }
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

       try {
           String CREATE_TASKS = "CREATE TABLE " + TABLE_TASKS + "("
                   + TASK_NAME + " TEXT PRIMARY KEY , "
                   + TASK_BODY + " TEXT NOT NULL"+
                   ")";
           sqLiteDatabase.execSQL(CREATE_TASKS);
       }catch (Exception e){
           Log.e("s","a");
       }


    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);


        // Create tables again
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foreign_keys=ON;");
    }


    public void   addTask(Task task)
    {
        ContentValues values = new ContentValues();
        values.put(TASK_NAME, task.getName());
        values.put(TASK_BODY, task.getBody());
        try {

          getWritableDatabase().insert(TABLE_TASKS, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

    }

    public void   updateTask(String title,Task task)
    {
        ContentValues values = new ContentValues();
        values.put(TASK_NAME, task.getName());
        values.put(TASK_BODY, task.getBody());
        try {
            getWritableDatabase().update(TABLE_TASKS, values, TASK_NAME+"=?", new String[]{title});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

    }

    public ArrayList<Task> getTasks(){
        ArrayList<Task> dataArrays = new ArrayList<>();
        Cursor cursor = null;
        try {


            cursor =getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_TASKS, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {

                    Task task = new Task();
                    task.setName(cursor.getString(0));
                    task.setBody(cursor.getString(1));
                    dataArrays.add(task);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public  void  deleteTask(Task task)
    {
       Cursor cursor =getWritableDatabase().rawQuery("Delete FROM " + TABLE_TASKS+" Where "+TASK_NAME+" = '"+task.getName()+"'", null, null);
        cursor.moveToFirst();
    }
}