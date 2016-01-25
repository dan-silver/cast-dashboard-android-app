package com.example.dan.castdemo;

import com.example.dan.castdemo.settingsFragments.CalendarSettings;
import com.example.dan.castdemo.settingsFragments.StocksSettings;
import com.example.dan.castdemo.widgets.CalendarWidget;
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
        CALENDAR(0),
        PLACEHOLDER(1),
        STOCKS(2);


        private int value;

        types(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
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

    public int getIconResource() {
        int typeIconResource;
        Widget.types type = getWidgetType();

        switch (type) {
            case CALENDAR:
                typeIconResource = R.drawable.ic_today_24dp;
                break;
            case STOCKS:
                typeIconResource = R.drawable.ic_attach_money_24dp;
                break;
            default:
                typeIconResource = R.drawable.ic_hourglass_empty_black_24px;
        }
        return typeIconResource;
    }

    final static String[] widgetNames = new String[]{
            CalendarWidget.HUMAN_NAME,
            PlaceholderWidget.HUMAN_NAME,
            StocksWidget.HUMAN_NAME
    };

    public String getHumanName() {
        return widgetNames[type];
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

}
