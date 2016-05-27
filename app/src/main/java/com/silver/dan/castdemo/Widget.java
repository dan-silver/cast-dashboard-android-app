package com.silver.dan.castdemo;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;
import com.silver.dan.castdemo.widgets.CalendarWidget;
import com.silver.dan.castdemo.widgets.ClockWidget;
import com.silver.dan.castdemo.widgets.CountdownWidget;
import com.silver.dan.castdemo.widgets.MapWidget;
import com.silver.dan.castdemo.widgets.RSSWidget;
import com.silver.dan.castdemo.widgets.StocksWidget;
import com.silver.dan.castdemo.widgets.UIWidget;
import com.silver.dan.castdemo.widgets.WeatherWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@ModelContainer
@Table(database = WidgetDatabase.class)
public class Widget extends BaseModel {

    // For bundle data passing
    public static String ID = "WIDGET_ID";

    public static int DEFAULT_WIDGET_HEIGHT = 60;
    public static int DEFAULT_SCROLL_INTERVAL = 20;

    public UIWidget getUIWidget(Context context) {
        UIWidget widget = null;
        switch (getWidgetType()) {
            case STOCKS:
                widget = new StocksWidget(context, this);
                break;
            case CALENDAR:
                widget = new CalendarWidget(context, this);
                break;
            case MAP:
                widget = new MapWidget(context, this);
                break;
            case CLOCK:
                widget = new ClockWidget(context, this);
                break;
            case WEATHER:
                widget = new WeatherWidget(context, this);
                break;
            case RSS:
                widget = new RSSWidget(context, this);
                break;
            case COUNTDOWN:
                widget = new CountdownWidget(context, this);
                break;
        }
        return widget;
    }


    enum WidgetType {
        CALENDAR(0, R.string.calendar, R.drawable.ic_today_24dp),
        STOCKS(2, R.string.stocks, R.drawable.ic_attach_money_24dp),
        MAP(3, R.string.map, R.drawable.ic_map_24dp),
        CLOCK(4, R.string.clock, R.drawable.ic_access_time_24dp),
        WEATHER(5, R.string.weather, R.drawable.ic_cloud_queue_24dp),
        RSS(6, R.string.rss_feed, R.drawable.ic_rss_feed_black_24px),
        COUNTDOWN(7, R.string.countdown_timer, R.drawable.ic_timer_black_24dp);

        private int value;
        private int icon;
        private int humanNameRes;

        WidgetType(int value, int humanNameRes, int icon) {
            this.value = value;
            this.icon = icon;
            this.humanNameRes = humanNameRes;
        }

        public int getValue() {
            return value;
        }

        public int getIcon() {
            return icon;
        }

        public int getHumanNameRes() {
            return humanNameRes;
        }

        public static WidgetType getEnumByValue(int value) {
            for (WidgetType e : WidgetType.values()) {
                if (value == e.getValue()) return e;
            }
            return null;
        }
    }

    public WidgetType getWidgetType() {
        return WidgetType.getEnumByValue(type);
    }


    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int type;

    @Column
    public int position;

    public int getIconResource() {
        return getWidgetType().getIcon();
    }

    public int getHumanNameRes() {
        return getWidgetType().getHumanNameRes();
    }

    public Widget() {
    }

    public void setType(WidgetType type) {
        this.type = type.getValue();
    }

    // needs to be accessible for DELETE
    List<WidgetOption> options;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "options")
    public List<WidgetOption> getOptions() {
        if (options == null || options.isEmpty()) {
            options = SQLite.select()
                    .from(WidgetOption.class)
                    .where(WidgetOption_Table.widgetForeignKeyContainer_id.eq(id))
                    .queryList();
        }
        return options;
    }

    public WidgetOption getOption(String key) {
        return SQLite.select()
                .from(WidgetOption.class)
                .where(WidgetOption_Table.widgetForeignKeyContainer_id.eq(id))
                .and(WidgetOption_Table.key.eq(key))
                .querySingle();
    }

    public List<WidgetOption> getOptions(String key) {
        return SQLite.select()
                .from(WidgetOption.class)
                .where(WidgetOption_Table.widgetForeignKeyContainer_id.eq(id))
                .and(WidgetOption_Table.key.eq(key))
                .queryList();
    }

    public static void fetchAll(final FetchAllWidgetsListener listener) {
        fetchAll(null, listener);
    }

    public static void fetchAll(WidgetType type, final FetchAllWidgetsListener listener) {
        ConditionGroup conditions = ConditionGroup.clause();

        if (type != null) {
            conditions.and(Widget_Table.type.is(type.getValue()));
        }

        QueryTransaction.Builder<Widget> query = new QueryTransaction.Builder<>(
            new Select()
                .from(Widget.class)
                .where(conditions));


        FlowManager
            .getDatabase(WidgetDatabase.class)
            .beginTransactionAsync(query.queryResult(new QueryTransaction.QueryResultCallback<Widget>() {
                @Override
                public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Widget> result) {
                    listener.results(result.toList());
                }
            }).build()).build().execute();
    }

    public JSONObject getJSONContent(Context applicationContext) {
        JSONObject payload = new JSONObject();

        try {
            payload.put("type", type);
            payload.put("id", id);
            payload.put("position", position);

            JSONObject data = getUIWidget(applicationContext).getContent();

            // if the widget has overridden the height, send it in the data {} so it can be quickly updated via the updateWidgetProperty channel
            WidgetOption height = loadOrInitOption(WidgetSettingsFragment.WIDGET_HEIGHT, applicationContext);
            if (height != null) {
                data.put(WidgetSettingsFragment.WIDGET_HEIGHT, height.getIntValue());
            }

            WidgetOption scrollInterval = loadOrInitOption(WidgetSettingsFragment.SCROLL_INTERVAL, applicationContext);
            if (scrollInterval != null) {
                data.put(WidgetSettingsFragment.SCROLL_INTERVAL, scrollInterval.getIntValue());
            }

            payload.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return payload;
    }


    public void initOption(String key, String defaultValue) {
        if (this.getOption(key) != null) {
            return;
        }

        WidgetOption option = new WidgetOption();
        option.key = key;
        option.value = defaultValue;
        option.associateWidget(this);
        option.save();
    }

    public void initOption(String key, boolean defaultValue) {
        initOption(key, defaultValue ? 1 : 0);
    }

    public void initOption(String key, int defaultValue) {
        initOption(key, String.valueOf(defaultValue));
    }

    public void initOption(String key, long oneWeek) {
        initOption(key, Long.toString(oneWeek));
    }

    public WidgetOption loadOrInitOption(String optionKey, Context context) {
        WidgetOption option = getOption(optionKey);

        if (option != null) {
            return option;
        }

        // this might be a new version of the app that doesn't have this option yet
        // that's fine, pretend like we're creating this widget for the first time (non-destructive for existing saved options)

        initWidgetSettings(context);


        option = getOption(optionKey);
        if (option == null) {
            Log.e(MainActivity.TAG, "Trying to access option that doesn't exist!" + optionKey);
        }

        return option;

    }

    public void initWidgetSettings(Context context) {
        // global widget properties
        initOption(WidgetSettingsFragment.WIDGET_HEIGHT, DEFAULT_WIDGET_HEIGHT);
        initOption(WidgetSettingsFragment.SCROLL_INTERVAL, DEFAULT_SCROLL_INTERVAL);

        // init widget specific properties
        getUIWidget(context).init();
    }
}