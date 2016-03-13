package com.silver.dan.castdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnSettingChanged, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static final int NAV_VIEW_WIDGETS_ITEM = 0;
    public static final int NAV_VIEW_OPTIONS_LAYOUT_ITEM = 1;
    public static final int NAV_VIEW_OPTIONS_THEME_ITEM = 2;

    private GoogleApiClient mGoogleApiClient;


    //drawer
    @Bind(R.id.nvView)
    NavigationView navView;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.top_toolbar)
    Toolbar top_toolbar;

    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private static DataCastManager mCastManager;
    private DataCastConsumer mCastConsumer;
    private MenuItem mediaRouteMenuItem;

    public void switchToFragment(Fragment destinationFrag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_fragment, destinationFrag);

        if (addToBackStack)
            transaction.addToBackStack(null);

        transaction.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showFtu() {
        IntroductoryOverlay overlay = new IntroductoryOverlay.Builder(this)
                .setMenuItem(mediaRouteMenuItem)
                .setTitleText(R.string.intro_overlay_text)
                .setSingleTime()
                .setOnDismissed(new IntroductoryOverlay.OnOverlayDismissedListener() {
                    @Override
                    public void onOverlayDismissed() {
                        Log.d(TAG, "overlay is dismissed");
                    }
                })
                .build();
        overlay.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        ButterKnife.bind(this);




        // Set the adapter for the list view
        Menu menu = navView.getMenu();

        menuItems.add(menu.add(0, NAV_VIEW_WIDGETS_ITEM, 0, "Widgets"));
        menuItems.add(menu.add(0, NAV_VIEW_OPTIONS_LAYOUT_ITEM, 1, "Layout"));
        menuItems.add(menu.add(0, NAV_VIEW_OPTIONS_THEME_ITEM, 2, "Theme"));


        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(top_toolbar);

        // Set the menu icon instead of the launcher icon.
        ActionBar ab = getSupportActionBar();
        assert ab != null;

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, top_toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        mDrawer.setDrawerListener(mDrawerToggle);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle.syncState();


        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        FlowManager.init(this);
        //Delete.tables(Widget.class, WidgetOption.class, Stock.class);


        switchToFragment(new WidgetList(), false);

        BaseCastManager.checkGooglePlayServices(this);
        CastConfiguration.Builder options = new CastConfiguration.Builder(getResources().getString(R.string.app_id))
                .enableAutoReconnect()
                .enableWifiReconnection()
                .addNamespace(getResources().getString(R.string.namespace))
                .setLaunchOptions(false, Locale.getDefault());

        if (BuildConfig.DEBUG)
            options.enableDebug();

        DataCastManager.initialize(this, options.build());

        mCastManager = DataCastManager.getInstance();
        mCastManager.setStopOnDisconnect(false);

        mCastManager.reconnectSessionIfPossible();
        CastCommunicator.init(this, mCastManager);
        mCastConsumer = new DataCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata, String applicationStatus,
                                               String sessionId, boolean wasLaunched) {
                sendAllOptions();
                sendAllWidgets();
                invalidateOptionsMenu();
            }

            @Override
            public void onDisconnected() {
                invalidateOptionsMenu();
            }

            @Override
            public void onCastAvailabilityChanged(boolean castPresent) {
                if (castPresent && mIsHoneyCombOrAbove) {

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaRouteMenuItem.isVisible()) {
                                showFtu();
                            }
                        }
                    }, 1000);
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


    }

    private void selectDrawerItem(MenuItem menuItem) {
        int selected = menuItem.getItemId();
        Fragment destination = null;
        boolean backStack = false;

        if (selected == NAV_VIEW_OPTIONS_LAYOUT_ITEM) {
            destination = new AppSettingsLayout();
            backStack = true;

        } else if (selected == NAV_VIEW_WIDGETS_ITEM) {
            destination = new WidgetList();
            backStack = false;
        } else if (selected == NAV_VIEW_OPTIONS_THEME_ITEM) {
            destination = new AppSettingsTheme();
            backStack = true;
        }


        if (!menuItem.isChecked()) {
            switchToFragment(destination, backStack);
            uncheckAllMenuItems();
        }

        mDrawer.closeDrawer(GravityCompat.START);

    }

    private void uncheckAllMenuItems() {
        for (MenuItem item : menuItems) {
            item.setChecked(false);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mediaRouteMenuItem = mCastManager.
                addMediaRouterButton(menu, R.id.media_route_menu_item);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    public void sendAllWidgets() {
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                JSONArray widgetsArr = new JSONArray();
                for (Widget widget : widgets) {
                    try {
                        widgetsArr.put(widget.getJSONContent(getApplicationContext()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CastCommunicator.sendJSON("widgets", widgetsArr);

            }
        });
    }


    private void sendAllOptions() {
        AppSettingsBindings settings = new AppSettingsBindings();
        settings.loadAllSettings(this);

        JSONObject options = new JSONObject();
        try {
            options.put(AppSettingsBindings.COLUMN_COUNT, settings.getNumberOfColumnsUI());
            options.put(AppSettingsBindings.BACKGROUND_COLOR, settings.getBackgroundColorHexStr());
            options.put(AppSettingsBindings.WIDGET_COLOR, settings.getWidgetColorHexStr());
            options.put(AppSettingsBindings.BACKGROUND_TYPE, settings.getBackgroundTypeUI());
            options.put(AppSettingsBindings.WIDGET_TRANSPARENCY, settings.getWidgetTransparencyUI());
            options.put(AppSettingsBindings.TEXT_COLOR, settings.getTextColorHextStr());
            options.put(AppSettingsBindings.SCREEN_PADDING, settings.getScreenPaddingUI());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CastCommunicator.sendJSON("options", options);
    }


    public void setDrawerItemChecked(int menuItem) {
        navView.getMenu().getItem(menuItem).setChecked(true);
    }

    @Override
    public void onSettingChanged(String setting, String value) {
        JSONObject options = new JSONObject();
        try {
            options.put(setting, value);
            CastCommunicator.sendJSON("options", options);

            // @todo cleanup, move to client/angular
            // when the column count changes, force refresh all maps
            if (setting.equals(AppSettingsBindings.COLUMN_COUNT)) {
                Widget.fetchByType(Widget.WidgetType.MAP, new FetchAllWidgetsListener() {
                    @Override
                    public void results(List<Widget> widgets) {
                        for (Widget widget : widgets) {
                            CastCommunicator.sendWidget(widget);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSettingChanged(String setting, int value) {
        onSettingChanged(setting, String.valueOf(value));
    }

    public void onItemMoved(JSONObject widgetsOrder) {
        CastCommunicator.sendJSON("order", widgetsOrder);
    }



    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() was called");
        mCastManager = DataCastManager.getInstance();
        if (mCastManager != null) {
            mCastManager.addDataCastConsumer(mCastConsumer);
            mCastManager.incrementUiCounter();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        mDrawer.closeDrawer(GravityCompat.START);
        uncheckAllMenuItems();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        mCastManager.decrementUiCounter();
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(MainActivity.TAG, "Error connecting to google services");
    }
}