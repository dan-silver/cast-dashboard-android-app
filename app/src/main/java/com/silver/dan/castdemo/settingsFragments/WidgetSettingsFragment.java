package com.silver.dan.castdemo.settingsFragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.silver.dan.castdemo.BillingHelper;
import com.silver.dan.castdemo.CastCommunicator;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.SimpleCallback;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class WidgetSettingsFragment extends Fragment {
    protected Widget widget;
    protected int scrollViewHeaderLayout = -1;

    public static String WIDGET_HEIGHT = "WIDGET_HEIGHT";
    public static String SCROLL_INTERVAL = "SCROLL_INTERVAL";
    public static String REFRESH_INTERVAL = "REFRESH_INTERVAL_SECONDS";

    @Nullable
    @BindView(R.id.widget_height)
    TwoLineSettingItem widgetHeight;

    @Nullable
    @BindView(R.id.widget_scroll_interval)
    TwoLineSettingItem scrollInterval;

    @Nullable
    @BindView(R.id.widget_refresh_interval)
    TwoLineSettingItem widgetRefreshInterval;

    // percentage of screen height
    WidgetOption optionWidgetHeight;

    // scroll interval in seconds
    WidgetOption optionScrollInterval;

    // how often to refresh the widget
    WidgetOption optionRefreshInterval;

    private IInAppBillingService mService;


    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget, getContext());
    }

    protected void updateWidgetProperty(String property, Object value) {
        CastCommunicator.sendWidgetProperty(widget, property, value);
    }

    protected WidgetOption loadOrInitOption(String property) {
        return widget.loadOrInitOption(property, getContext());
    }


    @Optional
    @OnClick(R.id.widget_refresh_interval)
    public void changeRefreshInterval() {
        if (BillingHelper.hasPurchased)
            return;

        BillingHelper.purchaseUpgrade(mService);
    }

    @Optional
    @OnClick(R.id.widget_height)
    public void cycleWidgetHeight() {
        // options are 40, 60, 80, 100
        int currentHeight = optionWidgetHeight.getIntValue();
        if (currentHeight == 100) {
            optionWidgetHeight.update(40);
        } else {
            optionWidgetHeight.update(currentHeight + 20);
        }
        updateWidgetHeightText();
        updateWidgetProperty(WidgetSettingsFragment.WIDGET_HEIGHT, optionWidgetHeight.getIntValue());
    }

    @Optional
    @OnClick(R.id.widget_scroll_interval)
    public void cycleWidgetScrollInterval() {
        // options are 10, 20, 30, 40
        int currentInterval = optionScrollInterval.getIntValue();
        if (currentInterval >= 40) {
            optionScrollInterval.update(10);
        } else {
            optionScrollInterval.update(currentInterval + 10);
        }
        updateScrollIntervalText();
        updateWidgetProperty(WidgetSettingsFragment.SCROLL_INTERVAL, optionScrollInterval.getIntValue());
    }

    public void updateWidgetHeightText() {
        if (widgetHeight != null)
            widgetHeight.setSubHeaderText(optionWidgetHeight.getIntValue() + "%");
    }


    public void updateScrollIntervalText() {
        if (scrollInterval != null)
            scrollInterval.setSubHeaderText(optionScrollInterval.getIntValue() + " " + getString(R.string.seconds));
    }

    public void updateRefreshIntervalText() {
        if (widgetRefreshInterval == null) return;

        widgetRefreshInterval.setSubHeaderText("Refresh every " + optionRefreshInterval.getIntValue() / 60 + " " + getString(R.string.minutes));
    }

    public void updateRefreshIntervalAfterPurchase() {

    }

    abstract public void initView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        String widgetKey = bundle.getString(Widget.GUID);
        this.widget = MainActivity.dashboard.getWidgetById(widgetKey);


        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                BillingHelper.fetchUpgradedStatus(mService, new SimpleCallback<Boolean>() {
                    @Override
                    public void onComplete(Boolean upgraded) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        BillingHelper.init(getActivity());
    }

    protected void supportWidgetRefreshInterval() {
        if (widgetRefreshInterval != null) {
            if (BillingHelper.hasPurchased){
                widgetRefreshInterval.setVisibility(View.GONE);
            } else{
                widgetRefreshInterval.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_1484313736_star));
            }

        }

        optionRefreshInterval = loadOrInitOption(WidgetSettingsFragment.REFRESH_INTERVAL);
        updateRefreshIntervalText();
    }

    protected void supportWidgetHeightOption() {
        optionWidgetHeight = loadOrInitOption(WidgetSettingsFragment.WIDGET_HEIGHT);
        updateWidgetHeightText();
    }


    protected void supportWidgetScrollInterval() {
        optionScrollInterval = loadOrInitOption(WidgetSettingsFragment.SCROLL_INTERVAL);
        updateScrollIntervalText();
    }

    public boolean hasScrollViewHeader() {
        return scrollViewHeaderLayout != -1;
    }

    protected void setScrollViewHeader(int layoutResource) {
        scrollViewHeaderLayout = layoutResource;
    }

    public int getScrollViewHeader() {
        return scrollViewHeaderLayout;
    }


    public void onPurchasedUpgrade() {
        updateRefreshIntervalAfterPurchase(); // updates the option to 5 minutes, or whatever per widget
        supportWidgetRefreshInterval();
    }
}

