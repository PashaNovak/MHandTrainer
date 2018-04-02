package pavelnovak.com.mhandtrainer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pavelnovak.com.mhandtrainer.fragments.StatisticFragment;
import pavelnovak.com.mhandtrainer.fragments.TrainingFragment;

/**
 * Created by PavelNovak on 20.03.2018.
 */

public class BaseActivity extends AppCompatActivity implements TrainingFragment.SaveClickListener{
    public static final String APP_PREFERENCES = "mysettings";
    public static final String EXTRA_FROM = "from_activity";
    SharedPreferences mSettings;

    private String[] titles;
    private ListView drawerList;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    private int currentPosition = 0;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        titles = getResources().getStringArray(R.array.titles);

        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                titles
        );
        drawerList.setAdapter(adapter);

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState != null){
            currentPosition = savedInstanceState.getInt("position");
            setTitle(titles[currentPosition]);
        } else {
            selectItem(0);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null,
                R.string.open_drawer, R.string.close_drawer) {
            //Вызывается при переходе выдвижной панели в полностью закрытое состояние.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            //Вызывается при переходе выдвижной панели в полностью открытое состояние.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragMan = getFragmentManager();
                Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                if (fragment instanceof TrainingFragment){
                    currentPosition = 0;
                }
                if (fragment instanceof StatisticFragment){
                    currentPosition = 1;
                }
                setTitle(titles[currentPosition]);
                drawerList.setItemChecked(currentPosition, true);
            }
        });
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }else {
            setTitle(mTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void saveClicked(boolean clicked) {
        if (clicked){
            selectItem(1);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectItem(i);
        }
    }

    private void selectItem(int position){
        boolean isLogOut = false;

        currentPosition = position;
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new TrainingFragment();
                ((TrainingFragment) fragment).setUserLogin(mSettings.getString(StartActivity.APP_PREFERENCE_LOGIN, ""));
                break;
            case 1:
                fragment = new StatisticFragment();
                ((StatisticFragment) fragment).setUserLogin(mSettings.getString(StartActivity.APP_PREFERENCE_LOGIN, ""));
                break;
            case 2:
                isLogOut = true;
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(StartActivity.APP_PREFERENCE_LOGIN, "");
                editor.apply();
                Intent intent = new Intent(BaseActivity.this, AuthorizationActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragment = new TrainingFragment();
                ((TrainingFragment) fragment).setUserLogin(mSettings.getString(StartActivity.APP_PREFERENCE_LOGIN, ""));
        }

        if (!isLogOut) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, "visible_fragment");
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            setTitle(titles[position]);

            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager managerBack = getFragmentManager();
        if (managerBack.getBackStackEntryCount() == 0){
           this.finish();
           if (!getIntent().getStringExtra(EXTRA_FROM).equals("start")){
               SharedPreferences.Editor editor = mSettings.edit();
               editor.putString(StartActivity.APP_PREFERENCE_LOGIN, "");
               editor.apply();
           }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

}
