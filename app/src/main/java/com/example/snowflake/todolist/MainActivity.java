package com.example.snowflake.todolist;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.example.snowflake.todolist.adapter.TabAdapter;
import com.example.snowflake.todolist.alarm.AlarmHelper;
import com.example.snowflake.todolist.database.DbHelper;
import com.example.snowflake.todolist.dialog.AddingTaskDialogFragment;
import com.example.snowflake.todolist.dialog.EditTaskDialogFragment;
import com.example.snowflake.todolist.fragment.CurrentTaskFragment;
import com.example.snowflake.todolist.fragment.DoneTaskFragment;
import com.example.snowflake.todolist.fragment.SplashFragment;
import com.example.snowflake.todolist.fragment.TaskFragment;
import com.example.snowflake.todolist.model.ModelTask;

public class MainActivity extends AppCompatActivity
        implements AddingTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener,
        EditTaskDialogFragment.EditingTaskListener {

    //allows to find the fragment, which is associated with Activity
    protected FragmentManager mFragmentManager;

    protected PreferenceHelper mPreferenceHelper;
    protected TabAdapter mTabAdapter;
    protected TaskFragment mCurrentTaskFragment;
    protected TaskFragment mDoneTaskFragment;
    protected DbHelper mDbHelper;
    protected SearchView mSearchView;
    protected Toolbar mToolbar;

    private FirebaseAuth mAuth;

    public DbHelper getDbHelper() {
        return mDbHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlarmHelper.getInstance().init(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        PreferenceHelper.getInstance().init(getApplicationContext());
        mPreferenceHelper = PreferenceHelper.getInstance();

        mDbHelper = new DbHelper(getApplicationContext());

        // Allows to find the fragment, which is associated with Activity
        mFragmentManager = getFragmentManager();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Checks value of PreferenceHelper
        runSplash();

        setUI(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Switches the checkbox from the saved settings
        MenuItem spleshItem = menu.findItem(R.id.action_splash);
        spleshItem.setChecked(mPreferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_splash) {
            item.setChecked(!item.isChecked());
            //changes the value of the checkbox if user click checkbox in Menu
            mPreferenceHelper.putBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE, item.isChecked());
            return true;
        }

        if (id == R.id.action_exit) {
            this.signOut();
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_map) {
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    // Checks value of PreferenceHelper
    public void runSplash() {
        //Reads the state of the checkbox
        if (!mPreferenceHelper.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)) {
            SplashFragment splashFragment = new SplashFragment();

            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, splashFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    //Responsible for the user interface.
    public void setUI(Context context) {
        if (mToolbar != null) {
            mToolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.white));
            setSupportActionBar(mToolbar);
        }
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.current_task));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.done_task));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mTabAdapter = new TabAdapter(mFragmentManager);

        viewPager.setAdapter(mTabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

        mCurrentTaskFragment = (CurrentTaskFragment) mTabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        mDoneTaskFragment = (DoneTaskFragment) mTabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);

        mSearchView = (SearchView) findViewById(R.id.search_view);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCurrentTaskFragment.findTasks(newText);
                mDoneTaskFragment.findTasks(newText);
                return false;
            }
        });
        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
                addingTaskDialogFragment.show(mFragmentManager, "AddingTaskDialogFragment");
            }
        });

    }


    @Override
    public void onTaskAdded(ModelTask newTask) {
        mCurrentTaskFragment.addTask(newTask, true);
    }

    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this, "Task adding cancel", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskDone(ModelTask task) {
        mDoneTaskFragment.addTask(task, false);
    }

    @Override
    public void onTaskRestore(ModelTask task) {
        mCurrentTaskFragment.addTask(task, false);
    }

    @Override
    public void onTaskEdited(ModelTask updatedTask) {
        mCurrentTaskFragment.updateTask(updatedTask);
        mDbHelper.update().task(updatedTask);
    }
}
