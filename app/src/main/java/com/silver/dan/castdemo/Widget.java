package com.silver.dan.castdemo;

import android.content.Context;
import android.util.Log;

import com.silver.dan.castdemo.widgets.CalendarWidget;
import com.silver.dan.castdemo.widgets.ClockWidget;
import com.silver.dan.castdemo.widgets.MapWidget;
import com.silver.dan.castdemo.widgets.PlaceholderWidget;
import com.silver.dan.castdemo.widgets.StocksWidget;
import com.silver.dan.castdemo.widgets.UIWidget;
import com.silver.dan.castdemo.widgets.WeatherWidget;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.SelectListTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@ModelContainer
@Table(database = WidgetDatabase.class)
public class Widget extends BaseModel {

    // For bundle data passing
    public static String ID = "WIDGET_ID";

    public UIWidget getUIWidget(Context context) {
        UIWidget widget;
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
            default:
                widget = new PlaceholderWidget(context, this);
                break;
        }
        return widget;
    }

    enum WidgetType {
        CALENDAR(0, CalendarWidget.HUMAN_NAME, R.drawable.ic_today_24dp),
        PLACEHOLDER(1, PlaceholderWidget.HUMAN_NAME, R.drawable.ic_hourglass_empty_black_24px),
        STOCKS(2, StocksWidget.HUMAN_NAME, R.drawable.ic_attach_money_24dp),
        MAP(3, MapWidget.HUMAN_NAME, R.drawable.ic_map_24dp),
        CLOCK(4, ClockWidget.HUMAN_NAME, R.drawable.ic_access_time_24dp),
        WEATHER(5, WeatherWidget.HUMAN_NAME, R.drawable.ic_cloud_queue_24dp);

        private int value;
        private int icon;
        private String humanName;

        WidgetType(int value, String humanName, int icon) {
            this.value = value;
            this.icon = icon;
            this.humanName = humanName;
        }

        public int getValue() {
            return value;
        }

        public int getIcon() {
            return icon;
        }

        public String getHumanName() {
            return humanName;
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

    public String getHumanName() {
        return getWidgetType().getHumanName();
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
        TransactionManager.getInstance().addTransaction(
                new SelectListTransaction<>(new Select().from(Widget.class).orderBy(Widget_Table.position, true),
                        new TransactionListenerAdapter<List<Widget>>() {
                            @Override
                            public void onResultReceived(List<Widget> someObjectList) {
                                listener.results(someObjectList);
                            }
                        }));

    }


    public static void fetchByType(WidgetType type, final FetchAllWidgetsListener listener) {
        TransactionManager.getInstance().addTransaction(

                new SelectListTransaction<>(
                        new Select().from(Widget.class)
                                .where(ConditionGroup.clause().and(Widget_Table.type.is(type.getValue()))),
                        new TransactionListenerAdapter<List<Widget>>() {
                            @Override
                            public void onResultReceived(List<Widget> someObjectList) {
                                listener.results(someObjectList);
                            }
                        }));

    }

    public JSONObject getJSONContent(Context applicationContext) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("type", getWidgetType().getHumanName().toLowerCase());
        payload.put("id", this.id);
        payload.put("options", new JSONObject());
        payload.put("position", position);

        payload.put("data", getUIWidget(applicationContext).getContent());

        return payload;
    }


    public void initOption(String key, String defaultValue) {
        WidgetOption option = new WidgetOption();
        option.key = key;
        option.value = defaultValue;
        option.associateWidget(this);
        option.save();
    }

    public void initOption(String key, boolean defaultValue) {
        if (this.getOption(key) == null)
            initOption(key, defaultValue ? 1 : 0);
    }

    public void initOption(String key, int defaultValue) {
        initOption(key, String.valueOf(defaultValue)
        );
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
        getUIWidget(context).init();
    }
}