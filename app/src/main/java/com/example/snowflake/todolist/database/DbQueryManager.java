package com.example.snowflake.todolist.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.snowflake.todolist.model.ModelTask;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by SnowFlake on 05.02.2016.
 */
public class DbQueryManager {

    private SQLiteDatabase mDatabase;

    DbQueryManager(SQLiteDatabase database) {
        this.mDatabase = database;
    }

    public ModelTask getTask(long timeStapm) {
        ModelTask modelTask = null;
        Cursor cursor = mDatabase.query(DbHelper.TASKS_TABLE, null, DbHelper.SELECTION_TIME_STAMP,
                new String[]{Long.toString(timeStapm)}, null, null, null);
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_TITLE_COLUMN));
            long date = cursor.getLong(cursor.getColumnIndex(DbHelper.TASK_DATE_COLUMN));
            int priority = cursor.getInt(cursor.getColumnIndex(DbHelper.TASK_PRIORITY_COLUMN));
            int status = cursor.getInt(cursor.getColumnIndex(DbHelper.TASK_STATUS_COLUMN));

            modelTask = new ModelTask(title, date, priority, status, timeStapm);
        }
        cursor.close();

        return modelTask;
    }


    // returns list of tasks
    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();


        //finds data and forms it into a task
        Cursor c = mDatabase.query(DbHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);
        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndex(DbHelper.TASK_TITLE_COLUMN));
                long date = c.getLong(c.getColumnIndex(DbHelper.TASK_DATE_COLUMN));
                int priority = c.getInt(c.getColumnIndex(DbHelper.TASK_PRIORITY_COLUMN));
                int status = c.getInt(c.getColumnIndex(DbHelper.TASK_STATUS_COLUMN));
                long timeStamp = c.getLong(c.getColumnIndex(DbHelper.TASK_TIME_STAMP_COLUMN));

                ModelTask modelTask = new ModelTask(title, date, priority, status, timeStamp);
                tasks.add(modelTask);
            } while (c.moveToNext());
        }
        c.close();

        return tasks;
    }
}
