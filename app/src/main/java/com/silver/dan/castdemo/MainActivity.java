package com.silver.dan.castdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.silver.dan.castdemo.cast.CastChannel;
import com.silver.dan.castdemo.settingsFragments.GoogleCalendarSettings;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.silver.dan.castdemo.BillingHelper.UPGRADE_RETURN_CODE;

public class MainActivity extends AppCompatActivity implements OnSettingChangedListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int NAV_VIEW_WIDGETS_ITEM = 0;
    public static final int NAV_VIEW_OPTIONS_LAYOUT_ITEM = 1;
    public static final int NAV_VIEW_OPTIONS_THEME_ITEM = 2;


    //drawer
    @BindView(R.id.nvView)
    NavigationView navView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.top_toolbar)
    Toolbar top_toolbar;

    @BindView(R.id.upgrade_btn)
    Button upgradeBtn;

    ImageView userUpgradedBadge;

    private static Dashboard dashboard;
    private ServiceConnection mServiceConn;

    @OnClick(R.id.logout_btn)
    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.LOGOUT, true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.upgrade_btn)
    public void upgrade() {
        BillingHelper.purchaseUpgrade(mService, this);
    }

    private ArrayList<MenuItem> menuItems = new ArrayList<>();

    private CastContext mCastContext;
    static CastSession mCastSession;
    static CastChannel mHelloWorldChannel;

    private WidgetList widgetListFrag;

    private FirebaseAnalytics mFirebaseAnalytics;
    IInAppBillingService mService;


    public void switchToFragment(final Fragment destinationFrag, final boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, destinationFrag);

        if (addToBackStack)
            transaction.addToBackStack(null);

        transaction.commitAllowingStateLoss();
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

        mDrawer.addDrawerListener(mDrawerToggle);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle.syncState();

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        if (!LoginActivity.restoreUser()) {
            logout();
            return;
        }

        setupNavBarUserInfo();

        // load options and widgets
        switchToFragment(new LoadingFragment(), false);
        getDashboard().onLoaded(getApplicationContext(), new OnCompleteCallback() {
            @Override
            public void onComplete() {
                widgetListFrag = new WidgetList();
                switchToFragment(widgetListFrag, false);
            }

            @Override
            public void onError(Exception e) {
                Bundle bundle = new Bundle();
                bundle.putString("ERROR", e.toString());
                mFirebaseAnalytics.logEvent("ON_DASHBOARD_LOADED_ON_ERROR", bundle);
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                BillingHelper.fetchUpgradedStatus(mService, getPackageName(), new SimpleCallback<Boolean>() {
                    @Override
                    public void onComplete(Boolean upgraded) {
                        updateUpgradeButtonVisibility(upgraded);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        };

        mCastContext = CastContext.getSharedInstance(this);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void updateUpgradeButtonVisibility(final boolean upgraded) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                upgradeBtn.setVisibility(upgraded ? View.GONE : View.VISIBLE);

                userUpgradedBadge.setVisibility(upgraded ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    public void sendCredentials() {
        JSONObject creds = new JSONObject();
        try {
            creds.put("SERVICE_ACCESS_TOKEN", AuthHelper.userJwt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CastCommunicator.sendJSON("CREDENTIALS", creds);
    }

    private void setupNavBarUserInfo() {
        View header = navView.getHeaderView(0);
        userUpgradedBadge = navView.getHeaderView(0).findViewById(R.id.userUpgradedBadge);

        TextView displayName = header.findViewById(R.id.userDisplayName);
        displayName.setText(AuthHelper.user.getDisplayName());

        TextView email = header.findViewById(R.id.userEmail);
        email.setText(AuthHelper.user.getEmail());

        ImageView profilePhoto = header.findViewById(R.id.userPhoto);
        Picasso.with(getApplicationContext()).load(AuthHelper.user.getPhotoUrl()).into(profilePhoto);
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
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    private void sendAllOptions() {
        getDashboard().onLoaded(getApplicationContext(), new OnCompleteCallback() {
            @Override
            public void onComplete() {
                JSONObject options = new JSONObject();
                try {
                    options.put(AppSettingsBindings.COLUMN_COUNT, dashboard.settings.getNumberOfColumnsUI());
                    options.put(AppSettingsBindings.BACKGROUND_COLOR, dashboard.settings.getBackgroundColorHexStr());
                    options.put(AppSettingsBindings.WIDGET_COLOR, dashboard.settings.getWidgetColorHexStr());
                    options.put(AppSettingsBindings.BACKGROUND_TYPE, dashboard.settings.getBackgroundTypeUI());
                    options.put(AppSettingsBindings.WIDGET_TRANSPARENCY, dashboard.settings.getWidgetTransparencyUI());
                    options.put(AppSettingsBindings.TEXT_COLOR, dashboard.settings.getTextColorHextStr());
                    options.put(AppSettingsBindings.SCREEN_PADDING, dashboard.settings.getScreenPaddingUI());
                    options.put(AppSettingsBindings.LOCALE, getResources().getConfiguration().locale.toString());
                    options.put(AppSettingsBindings.LANGUAGE_CODE, getResources().getConfiguration().locale.getLanguage());
                    options.put(AppSettingsBindings.SLIDESHOW_INTERVAL, dashboard.settings.getSlideshowInterval());
                    options.put(AppSettingsBindings.BACKGROUND_GOOGLE_ALBUM_ID, dashboard.settings.backgroundGooglePhotosAlbumId);
                    options.put(AppSettingsBindings.BACKGROUND_GOOGLE_ALBUM_NAME, dashboard.settings.backgroundGooglePhotosAlbumName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CastCommunicator.sendJSON("options", options);

            }

            @Override
            public void onError(Exception e) {
                Bundle bundle = new Bundle();
                bundle.putString("ERROR", e.toString());
                mFirebaseAnalytics.logEvent("SEND_ALL_OPTIONS_LOADED_ON_ERROR", bundle);

            }
        });

    }

    public static Dashboard getDashboard() {
        if (dashboard == null) {
            dashboard = new Dashboard();
        }
        return dashboard;
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
    protected void onPause() {
        super.onPause();

        // Unregister cast session listener
        mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener,
                CastSession.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register cast session listener
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener,
                CastSession.class);
        if (mCastSession == null) {
            // Get the current session if there is one
            mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        }

        updateUpgradeButtonVisibility(BillingHelper.hasPurchased);
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == WidgetSettingsActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (intent.hasExtra(Widget.DELETE_WIDGET)) {
                    new Handler().post(new Runnable() {
                        public void run() {
                            String widgetIdToDelete = intent.getStringExtra(Widget.DELETE_WIDGET);
                            widgetListFrag.deleteWidget(dashboard.getWidgetById(widgetIdToDelete));
                        }
                    });
                }
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else if (requestCode == GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                AuthHelper authHelper = new AuthHelper(this);
                authHelper.completeCommonAuth(account, new SimpleCallback<String>() {
                    @Override
                    public void onComplete(String result) {
                        sendCredentials(); // Receiver must get new jwt for new google access token that has permissions to Google Calendar
                        widgetListFrag.processPermissionReceivedCallback(GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR, true);
                    }

                    @Override
                    public void onError(Exception e) {
                        widgetListFrag.processPermissionReceivedCallback(GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR, false);
                    }
                });
            } else {
                widgetListFrag.processPermissionReceivedCallback(GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR, false);
            }
        } else if (requestCode == UPGRADE_RETURN_CODE) {
            if (BillingHelper.extractHasPurchased(resultCode, intent)) {
                onPurchasedUpgrade();
            }
        } else if (requestCode == AppSettingsTheme.PERMISSION_RESULT_CODE_GOOGLE_ALBUMS) {
            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    AuthHelper authHelper = new AuthHelper(this);
                    authHelper.completeCommonAuth(account, new SimpleCallback<String>() {
                        @Override
                        public void onComplete(String result) {
                            sendCredentials(); // Receiver must get new jwt for new google access token that has permissions to Google Calendar

                            AppSettingsTheme themeSettings = ((AppSettingsTheme) getSupportFragmentManager().findFragmentById(R.id.main_fragment));

                            themeSettings.selectGooglePhotosAlbum();
                        }

                        @Override
                        public void onError(Exception e) {
                            //@todo
                        }
                    });

                }
            }
        }

    }

    private void onPurchasedUpgrade() {
        updateUpgradeButtonVisibility(true);
    }

    @Override
    public void onBackPressed() {
        mDrawer.closeDrawer(GravityCompat.START);
        uncheckAllMenuItems();
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(MainActivity.TAG, "Error connecting to google services");
    }


    private SessionManagerListener<CastSession> mSessionManagerListener
            = new SessionManagerListener<CastSession>() {
        @Override
        public void onSessionStarting(CastSession castSession) {
            // ignore
        }

        @Override
        public void onSessionStarted(CastSession castSession, String sessionId) {
            Log.v(TAG, "Session started");
            mCastSession = castSession;
            invalidateOptionsMenu();
            startCustomMessageChannel();

            sendCredentials();
            dashboard.setOnDataRefreshListener(new OnCompleteCallback() {
                @Override
                public void onComplete() {
                    sendAllOptions();
                    CastCommunicator.sendAllWidgets(getApplicationContext(), getDashboard());
                }

                @Override
                public void onError(Exception e) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ERROR", e.toString());
                    mFirebaseAnalytics.logEvent("ON_CONNECTED_REFRESH_ON_ERROR", bundle);
                }
            });

        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int error) {
            // ignore
        }

        @Override
        public void onSessionEnding(CastSession castSession) {
            // ignore
        }

        @Override
        public void onSessionEnded(CastSession castSession, int error) {
            Log.d(TAG, "Session ended");
            if (mCastSession == castSession) {
                cleanupSession();
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int reason) {
            // ignore
        }

        @Override
        public void onSessionResuming(CastSession castSession, String sessionId) {
            // ignore
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean wasSuspended) {
            Log.d(TAG, "Session resumed");
            mCastSession = castSession;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int error) {
            // ignore
        }
    };

    private void startCustomMessageChannel() {
        if (mCastSession != null && mHelloWorldChannel == null) {
            mHelloWorldChannel = new CastChannel(getString(R.string.namespace));
            try {
                mCastSession.setMessageReceivedCallbacks(mHelloWorldChannel.getNamespace(),
                        mHelloWorldChannel);
                Log.d(TAG, "Message channel started");
            } catch (IOException e) {
                Log.d(TAG, "Error starting message channel", e);
                mHelloWorldChannel = null;
            }
        }
    }

    private void cleanupSession() {
        closeCustomMessageChannel();
        mCastSession = null;
    }


    private void closeCustomMessageChannel() {
        if (mCastSession != null && mHelloWorldChannel != null) {
            try {
                mCastSession.removeMessageReceivedCallbacks(mHelloWorldChannel.getNamespace());
                Log.d(TAG, "Message channel closed");
            } catch (IOException e) {
                Log.d(TAG, "Error closing message channel", e);
            } finally {
                mHelloWorldChannel = null;
            }
        }
    }
}