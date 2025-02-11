package org.l3ger0j.demo;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.l3ger0j.library.ResideMenu;
import org.l3ger0j.library.ResideMenuItem;

public class MenuActivity extends FragmentActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemSettings;
    private static final String MENU_STATE = "MenuActivity.MENU_STATE";
    private static final String MENU_DIRECTION = "MenuActivity.MENU_DIRECTION";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        setUpMenu(savedInstanceState);
        if(savedInstanceState == null) {
            changeFragment(new HomeFragment());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (resideMenu != null) {
            outState.putBoolean(MENU_STATE, resideMenu.isOpened());
            outState.putInt(MENU_DIRECTION, resideMenu.getCurrentDirection());
        }
    }

    private void setUpMenu(Bundle savedInstanceState) {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.baseline_home_24,"Home");
        itemProfile  = new ResideMenuItem(this, R.drawable.baseline_person_24,  "Profile");
        itemCalendar = new ResideMenuItem(this, R.drawable.baseline_calendar_24, "Calendar");
        itemSettings = new ResideMenuItem(this, R.drawable.baseline_settings_24,"Settings");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemCalendar.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(view ->
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT, true));
        findViewById(R.id.title_bar_right_menu).setOnClickListener(view ->
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT, true));

        if (savedInstanceState != null && savedInstanceState.getBoolean(MENU_STATE)) {
            resideMenu.openMenu(savedInstanceState.getInt(MENU_DIRECTION));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new HomeFragment());
        } else if (view == itemProfile){
            changeFragment(new ProfileFragment());
        } else if (view == itemCalendar){
            changeFragment(new CalendarFragment());
        } else if (view == itemSettings){
            changeFragment(new SettingsFragment());
        }

        resideMenu.closeMenu();
    }

    private final ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .commit();
    }

    public ResideMenu getResideMenu(){
        return resideMenu;
    }
}
