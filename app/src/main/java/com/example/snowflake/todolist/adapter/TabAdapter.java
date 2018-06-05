package com.example.snowflake.todolist.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.example.snowflake.todolist.fragment.CurrentTaskFragment;
import com.example.snowflake.todolist.fragment.DoneTaskFragment;

/**
 * Created by SnowFlake on 30.01.2016.
 */
public class TabAdapter extends FragmentStatePagerAdapter {

    public static final int CURRENT_TASK_FRAGMENT_POSITION = 0;
    public static final int DONE_TASK_FRAGMENT_POSITION = 1;

    private Fragment[] mFragments = {
            new CurrentTaskFragment(),
            new DoneTaskFragment(),
    };

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

}
