package com.example.snowflake.todolist.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.snowflake.todolist.database.DbHelper;
import com.example.snowflake.todolist.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SnowFlake on 07.02.2016.
 */
public class AllarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DbHelper dbHelper = new DbHelper(context);

        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(dbHelper.query().getTasks(DbHelper.SELECTION_STATUS + " OR "
                + DbHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)}, DbHelper.TASK_DATE_COLUMN));


        for (ModelTask task : tasks){
            if (task.getDate() != 0){
                alarmHelper.setAlarm(task);
            }
        }
    }
}
