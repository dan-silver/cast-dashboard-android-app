package com.example.dan.castdemo;

import com.example.dan.castdemo.settingsFragments.CalendarSettings;
import com.example.dan.castdemo.settingsFragments.ClockSettings;
import com.example.dan.castdemo.settingsFragments.MapSettings;
import com.example.dan.castdemo.settingsFragments.StocksSettings;
import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.ClockWidget;
import com.example.dan.castdemo.widgets.MapWidget;
import com.example.dan.castdemo.widgets.PlaceholderWidget;
import com.example.dan.castdemo.widgets.StocksWidget;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

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
        CLOCK(4, ClockWidget.HUMAN_NAME, R.drawable.ic_access_time_24dp);

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
        } else if (getWidgetType() == types.STOCKS) {
            StocksSettings.init(this);
        } else if (getWidgetType() == types.MAP) {
            MapSettings.init(this);
        } else if (getWidgetType() == types.CLOCK) {
            ClockSettings.init(this);
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

}
