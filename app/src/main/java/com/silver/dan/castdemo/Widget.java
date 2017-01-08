package com.silver.dan.castdemo;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
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
import com.silver.dan.castdemo.settingsFragments.CalendarSettings;
import com.silver.dan.castdemo.settingsFragments.StocksSettings;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;
import com.silver.dan.castdemo.widgets.CalendarWidget;
import com.silver.dan.castdemo.widgets.ClockWidget;
import com.silver.dan.castdemo.widgets.CountdownWidget;
import com.silver.dan.castdemo.widgets.FreeTextWidget;
import com.silver.dan.castdemo.widgets.MapWidget;
import com.silver.dan.castdemo.widgets.RSSWidget;
import com.silver.dan.castdemo.widgets.StocksWidget;
import com.silver.dan.castdemo.widgets.UIWidget;
import com.silver.dan.castdemo.widgets.WeatherWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silver.dan.castdemo.FirebaseMigration.useFirebaseForReadsAndWrites;

@ModelContainer
@Table(database = WidgetDatabase.class)
@IgnoreExtraProperties

public class Widget extends BaseModel {
    @Exclude
    private static int DEFAULT_WIDGET_HEIGHT = 60;

    @Exclude
    private static int DEFAULT_SCROLL_INTERVAL = 20;

    public static String GUID = "GUID";

    @Exclude
    UIWidget getUIWidget(Context context) {
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
            case CUSTOM_TEXT:
                widget = new FreeTextWidget(context, this);
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
        COUNTDOWN(7, R.string.countdown_timer, R.drawable.ic_timer_black_24dp),
        CUSTOM_TEXT(8, R.string.custom_text, R.drawable.ic_insert_comment_black_24dp);

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

    @Exclude
    protected static DatabaseReference getFirebaseDashboardWidgetsRef() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase
            .child("users")
            .child(LoginActivity.user.getUid())
            .child("widgets");
    }

    public interface GetWidgetCallback {
        void complete(Widget widget);

        void error();
    }

    // firebase
    @Exclude
    public static Widget getFromCache(String key) {
        for (Widget widget : WidgetList.widgetsCache) {
            if (widget.guid.equals(key)) return widget;
        }
        return null;
    }

    // firebase
    @Exclude
    public static void getFromKey(String key, final GetWidgetCallback callback) {
        DatabaseReference ref = getFirebaseDashboardWidgetsRef()
                .child(key);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Widget widget = dataSnapshot.getValue(Widget.class);
                    widget.guid = dataSnapshot.getKey();


                    Widget.loadOptions(widget);
                    callback.complete(widget);
                    return;
                }
                callback.error();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(MainActivity.TAG, "loadPost:onCancelled", databaseError.toException());
                callback.error();
                // ...
            }
        };

        ref.addListenerForSingleValueEvent(postListener);
    }

    @Exclude
    private static void loadOptions(Widget widget) {
        if (widget.optionsMap != null) {
            for (Map.Entry pair : widget.optionsMap.entrySet()) {
                WidgetOption opt = (WidgetOption) pair.getValue();
                opt.widgetRef = widget;
                opt.key = (String) pair.getKey();
            }
        }
    }

    @Exclude
    private DatabaseReference getFirebaseWidgetRef() {
        DatabaseReference widgetsRef = getFirebaseDashboardWidgetsRef();

        // if the guid is empty, it's never been saved to firebase before
        if (this.guid == null) {
            this.guid = widgetsRef.push().getKey();
        }

        return widgetsRef.child(this.guid);
    }


    @Exclude
    void saveFirstTimeWithMigration() {
        getFirebaseWidgetRef().setValue(this.toMapFirstTimeMigration());
    }


    @Exclude
    @Override
    public void save() {
        if (!useFirebaseForReadsAndWrites)
            super.save();
        saveFirebaseOnly();
    }

    void saveFirebaseOnly() {
        if (useFirebaseForReadsAndWrites)
            getFirebaseWidgetRef().setValue(this.toMap());
    }

    void savePosition() {
        getFirebaseWidgetRef()
                .child("position")
                .setValue(this.position);
    }

    @Exclude
    @Override
    public void delete() {
        getFirebaseWidgetRef().removeValue();
        super.delete();
    }

    @Exclude
    private WidgetType getWidgetType() {
        return WidgetType.getEnumByValue(type);
    }

    @Exclude
    @PrimaryKey(autoincrement = true)
    @Deprecated
    public long id;

    @Column
    public int type;

    @Exclude
    public String guid;

    @Column
    public int position;

    // needs to be accessible for DELETE
    @Exclude
    @Deprecated
    List<WidgetOption> options;

    // for firebase
    public Map<String, WidgetOption> optionsMap;

    @Exclude
    public int getIconResource() {
        return getWidgetType().getIcon();
    }

    @Exclude
    public int getHumanNameRes() {
        return getWidgetType().getHumanNameRes();
    }

    public Widget() {
    }



    // Migrate old list format to string CSV
    // the dbflow implementation didn't use unique keys, firebase we do

    @Exclude
    public Map<String, Object> toMapFirstTimeMigration() {
        Map<String, Object> widgetMap = toMap();

        Map<String, Map<String, String>> optionsMap = new HashMap<>();

        List<WidgetOption> enabledCalendars = new ArrayList<>();
        List<WidgetOption> savedStocks = new ArrayList<>();
        for (WidgetOption opt : this.getOptions()) {
            if (opt.key.equals(CalendarSettings.CALENDAR_ENABLED)) {
                enabledCalendars.add(opt);
                continue;
            }

            if (opt.key.equals(StocksSettings.STOCK_IN_LIST)) {
                savedStocks.add(opt);
                continue;
            }

            optionsMap.put(opt.key, opt.toMap());
        }


        if (enabledCalendars.size() > 0) {
            WidgetOption enabledCalendarsOption = new WidgetOption();
            enabledCalendarsOption.key = CalendarSettings.CALENDAR_ENABLED;
            List<String> enabledCalIds = new ArrayList<>();
            for (WidgetOption cal : enabledCalendars)
                enabledCalIds.add(cal.value);

            enabledCalendarsOption.setValue(enabledCalIds);

            optionsMap.put(CalendarSettings.CALENDAR_ENABLED, enabledCalendarsOption.toMap());
        }

        if (savedStocks.size() > 0) {
            WidgetOption savedStocksOption = new WidgetOption();
            savedStocksOption.key = StocksSettings.STOCK_IN_LIST;
            List<String> stockTickers = new ArrayList<>();
            for (WidgetOption stock : savedStocks) {
                Stock selectedStock = new Select().from(Stock.class).where(Stock_Table._id.is(stock.getIntValue())).querySingle();
                if (selectedStock != null)
                    stockTickers.add(selectedStock.getTicker());
            }

            savedStocksOption.setValue(stockTickers);

            optionsMap.put(StocksSettings.STOCK_IN_LIST, savedStocksOption.toMap());
        }



        widgetMap.put("optionsMap", optionsMap);

        return widgetMap;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("type", type);
        result.put("position", position);
        result.put("optionsMap", getMappedOptions());

        return result;
    }

    // {key:x,value:y}
    private Map<String, WidgetOption> getMappedOptions() {
        return optionsMap;
    }

    @Exclude
    public void setType(WidgetType type) {
        this.type = type.getValue();
    }



    @Exclude
    @Deprecated
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "options")
    public List<WidgetOption> getOptions() {
//        if (options == null || options.isEmpty()) {
            options = SQLite.select()
                    .from(WidgetOption.class)
                    .where(WidgetOption_Table.widgetForeignKeyContainer_id.eq(id))
                    .queryList();
//        }
        return options;
    }


    @Exclude
    public WidgetOption getOption(String key) {
        if (useFirebaseForReadsAndWrites) {
            return this.optionsMap.get(key);
        }

        return SQLite.select()
                .from(WidgetOption.class)
                .where(WidgetOption_Table.widgetForeignKeyContainer_id.eq(id))
                .and(WidgetOption_Table.key.eq(key))
                .querySingle();
    }

    @Exclude
    static void fetchAll(FetchAllWidgetsListener listener) {
        fetchAll(null, listener);
    }


    @Exclude
    static void fetchAll(final WidgetType type, final FetchAllWidgetsListener listener) {
        if (useFirebaseForReadsAndWrites) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Widget> widgets = new ArrayList<>();
                    for (DataSnapshot nextWidget : dataSnapshot.getChildren()) {
                        Widget widget = nextWidget.getValue(Widget.class);

                        widget.guid = nextWidget.getKey();

                        Widget.loadOptions(widget);

                        // optionally use the filter
                        if (type == null || type == widget.getWidgetType())
                            widgets.add(widget);
                    }

                    Collections.sort(widgets, new Comparator<Widget>() {
                        @Override
                        public int compare(Widget w1, Widget w2) {
                            if(w1.position == w2.position)
                                return 0;
                            return w1.position < w2.position ? -1 : 1;
                        }
                    });
                    listener.results(widgets);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
//                    listener.onError();
//                    @todo
                    Log.w(MainActivity.TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };

            getFirebaseDashboardWidgetsRef().addListenerForSingleValueEvent(postListener);

        } else {
            // this code path is only used for the initial firebase migration
            ConditionGroup conditions = ConditionGroup.clause();

            if (type != null) {
                conditions.and(Widget_Table.type.is(type.getValue()));
            }

            QueryTransaction.Builder<Widget> query = new QueryTransaction.Builder<>(
                    new Select()
                            .from(Widget.class)
                            .where(conditions)
                            .orderBy(Widget_Table.position, true));


            FlowManager
                    .getDatabase(WidgetDatabase.class)
                    .beginTransactionAsync(query.queryResult(new QueryTransaction.QueryResultCallback<Widget>() {
                        @Override
                        public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Widget> result) {
                            listener.results(result.toList());
                        }
                    }).build()).build().execute();
        }
    }


    @Exclude
    JSONObject getJSONContent(Context applicationContext) {
        JSONObject payload = new JSONObject();

        try {
            payload.put("type", type);
            payload.put("id", guid);
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

    @Exclude
    public void initOption(String key, String defaultValue) {
        if (this.optionsMap == null) {
            this.optionsMap = new HashMap<>();
        }


        if (this.getOption(key) != null) {
            return;
        }


        WidgetOption option = new WidgetOption();
        option.key = key;
        option.value = defaultValue;
        option.associateWidget(this);
        option.save();

        this.optionsMap.put(key, option);
    }

    @Exclude
    public void initOption(String key, boolean defaultValue) {
        initOption(key, defaultValue ? 1 : 0);
    }

    @Exclude
    public void initOption(String key, int defaultValue) {
        initOption(key, String.valueOf(defaultValue));
    }

    @Exclude
    public void initOption(String key, long oneWeek) {
        initOption(key, Long.toString(oneWeek));
    }

    @Exclude
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

    @Exclude
    void initWidgetSettings(Context context) {
        // global widget properties
        initOption(WidgetSettingsFragment.WIDGET_HEIGHT, DEFAULT_WIDGET_HEIGHT);
        initOption(WidgetSettingsFragment.SCROLL_INTERVAL, DEFAULT_SCROLL_INTERVAL);

        // start widget specific properties
        getUIWidget(context).init();
    }
}