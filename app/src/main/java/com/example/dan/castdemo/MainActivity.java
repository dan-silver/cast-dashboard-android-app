package com.example.dan.castdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.StocksWidget;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.DataCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.SelectListTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnSettingChanged {

    public static final String TAG = MainActivity.class.getSimpleName();
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static final int NAV_VIEW_WIDGETS_ITEM = 0;
    public static final int NAV_VIEW_OPTIONS_ITEM = 1;



    //drawer

    @Bind(R.id.nvView)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.top_toolbar)
    Toolbar top_toolbar;
    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private DataCastManager mCastManager;
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
        menuItems.add(menu.add(0, NAV_VIEW_OPTIONS_ITEM, 1, "Settings"));


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
        CastConfiguration options = new CastConfiguration.Builder(getResources().getString(R.string.app_id))
                .enableAutoReconnect()
                .enableWifiReconnection()
                .enableDebug()
                .addNamespace(getResources().getString(R.string.namespace))
                .build();
        DataCastManager.initialize(this, options);

        mCastManager = DataCastManager.getInstance();
        mCastManager.reconnectSessionIfPossible();
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


    }

    private void selectDrawerItem(MenuItem menuItem) {
        int selected = menuItem.getItemId();
        Fragment destination = null;
        boolean backStack = false;

        if (selected == NAV_VIEW_OPTIONS_ITEM) {
            destination = new AppSettings();
            backStack = true;

        } else if (selected == NAV_VIEW_WIDGETS_ITEM) {
            destination = new WidgetList();
            backStack = false;
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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
        MainActivity.getAllWidgets(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                JSONArray widgetsArr = new JSONArray();
                for (Widget widget : widgets) {
                    try {
                        widgetsArr.put(getWidgetJSON(widget));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                sendJSONToClient("widgets", widgetsArr);

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
            options.put(AppSettingsBindings.BACKGROUND_TYPE, settings.getBackgroundTypeUI());
            options.put(AppSettingsBindings.WIDGET_TRANSPARENCY, settings.getWidgetTransparencyUI());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJSONToClient("options", options);
    }


    public void setDrawerItemChecked(int menuItem) {
        navView.getMenu().getItem(menuItem).setChecked(true);
    }

    @Override
    public void onSettingChanged(String setting, String value) {
        JSONObject options = new JSONObject();
        try {
            options.put(setting, value);
            sendJSONToClient("options", options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSettingChanged(String setting, int value) {
        onSettingChanged(setting, String.valueOf(value));
    }

    public void onItemMoved(JSONObject widgetsOrder) {
        sendJSONToClient("order", widgetsOrder);
    }


    public static void getAllWidgets(final FetchAllWidgetsListener listener) {
        TransactionManager.getInstance().addTransaction(
                new SelectListTransaction<>(new Select().from(Widget.class),
                        new TransactionListenerAdapter<List<Widget>>() {
                            @Override
                            public void onResultReceived(List<Widget> someObjectList) {
                                listener.results(someObjectList);
                            }
                        }));

    }

    private void sendJSONToClient(final String key, final JSONObject payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainerToClient(container);
    }

    private void sendJSONContainerToClient(final JSONObject container) {
        new Runnable() {
            public void run() {
                try {
                    mCastManager.sendDataMessage(container.toString(), getResources().getString(R.string.namespace));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.run();
    }

    private void sendJSONToClient(final String key, final JSONArray payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainerToClient(container);
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
    protected void onPause() {
        mCastManager.decrementUiCounter();
        mCastManager.removeDataCastConsumer(mCastConsumer);
        super.onPause();
    }

    public JSONObject getWidgetJSON(Widget widget) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("type", widget.getWidgetType().getHumanName().toLowerCase());
        payload.put("id", widget.id);
        payload.put("options", new JSONObject());
        payload.put("position", widget.position);

        if (widget.getWidgetType() == Widget.types.CALENDAR) {
            CalendarWidget cw = new CalendarWidget(this, widget);
            payload.put("data", cw.getContent());
        } else if (widget.getWidgetType() == Widget.types.STOCKS) {
            StocksWidget sw = new StocksWidget(this, widget);
            payload.put("data", sw.getContent());
        }

        return payload;
    }

    @Override
    public void onBackPressed() {
        mDrawer.closeDrawer(GravityCompat.START);
        uncheckAllMenuItems();
        super.onBackPressed();
    }

    public void sendWidget(Widget widget) {
        try {
            sendJSONToClient("widget", getWidgetJSON(widget));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteWidget(Widget widget) {
        try {
            JSONObject info = new JSONObject();
            info.put("id", widget.id);
            sendJSONToClient("deleteWidget", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}