package com.silvr.dan.castdemo;

import android.content.Context;

import com.silvr.dan.castdemo.settingsFragments.CalendarSettings;
import com.silvr.dan.castdemo.settingsFragments.ClockSettings;
import com.silvr.dan.castdemo.settingsFragments.MapSettings;
import com.silvr.dan.castdemo.settingsFragments.StocksSettings;
import com.silvr.dan.castdemo.settingsFragments.WeatherSettings;
import com.silvr.dan.castdemo.widgets.CalendarWidget;
import com.silvr.dan.castdemo.widgets.ClockWidget;
import com.silvr.dan.castdemo.widgets.MapWidget;
import com.silvr.dan.castdemo.widgets.PlaceholderWidget;
import com.silvr.dan.castdemo.widgets.StocksWidget;
import com.silvr.dan.castdemo.widgets.UIWidget;
import com.silvr.dan.castdemo.widgets.WeatherWidget;
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

import static com.silvr.dan.castdemo.Widget.types.STOCKS;

@ModelContainer
@Table(database = WidgetDatabase.class)
public class Widget extends BaseModel {

    // For bundle data passing
    public static String ID = "WIDGET_ID";

    enum types {
        CALENDAR(0, CalendarWidget.HUMAN_NAME, R.drawable.ic_today_24dp),
        PLACEHOLDER(1, PlaceholderWidget.HUMAN_NAME, R.drawable.ic_hourglass_empty_black_24px),
        STOCKS(2, StocksWidget.HUMAN_NAME, R.drawable.ic_attach_money_24dp),
        MAP(3, MapWidget.HUMAN_NAME, R.drawable.ic_map_24dp),
        CLOCK(4, ClockWidget.HUMAN_NAME, R.drawable.ic_access_time_24dp),
        WEATHER(5, WeatherWidget.HUMAN_NAME, R.drawable.ic_cloud_queue_24dp);

        private int value;
        private int icon;
        private String humanName;

        types(int value, String humanName, int icon) {
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

        public static types getEnumByValue(int value) {
            for (types e : types.values()) {
                if (value == e.getValue()) return e;
            }
            return null;
        }
    }

    public types getWidgetType() {
        return types.getEnumByValue(type);
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

    public void setType(types type) {
        this.type = type.getValue();
    }

    public void initOptions() {

        //initialize the options for this type

        if (getWidgetType() == types.CALENDAR) {
            CalendarSettings.init(this);
        } else if (getWidgetType() == STOCKS) {
            StocksSettings.init(this);
        } else if (getWidgetType() == types.MAP) {
            MapSettings.init(this);
        } else if (getWidgetType() == types.CLOCK) {
            ClockSettings.init(this);
        } else if (getWidgetType() == types.WEATHER) {
            WeatherSettings.init(this);
        }
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


    public static void fetchByType(types type, final FetchAllWidgetsListener listener) {
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

        UIWidget widget;
        switch (getWidgetType()) {
            case STOCKS:
                widget = new StocksWidget(applicationContext, this);
                break;
            case CALENDAR:
                widget = new CalendarWidget(applicationContext, this);
                break;
            case MAP:
                widget = new MapWidget(applicationContext, this);
                break;
            case CLOCK:
                widget = new ClockWidget(applicationContext, this);
                break;
            case WEATHER:
                widget = new WeatherWidget(applicationContext, this);
                break;
            default:
                widget = new PlaceholderWidget(applicationContext, this);
                break;
        }

        payload.put("data", widget.getContent());

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
            initOption(key, defaultValue ? "1" : "0");
    }


}