package com.silver.dan.castdemo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ModelContainer
@Table(database = WidgetDatabase.class)
@IgnoreExtraProperties
public class WidgetOption extends BaseModel {



    @PrimaryKey(autoincrement = true)
    @Exclude
    public long id;

    @Column
    public String key;

    @Column
    public String value;

    public WidgetOption() {
    }


    @Column
    @ForeignKey(saveForeignKeyModel = false)
    @Exclude
    ForeignKeyContainer<Widget> widgetForeignKeyContainer;

    @Exclude
    public void associateWidget(Widget widget) {
        widgetForeignKeyContainer = FlowManager.getContainerAdapter(Widget.class).toForeignKeyContainer(widget);
    }

    @Exclude
    public boolean getBooleanValue() {
        return value.equals("1");
    }

    @Exclude
    public void setValue(boolean booleanValue) {
        this.value = booleanValue ? "1" : "0";
    }

    @Exclude
    public void setValue(int value) {
        this.value = Integer.toString(value);
    }

    @Exclude
    public int getIntValue() {
        return Integer.valueOf(value);
    }

    @Exclude
    public void setValue(Date datetime) {
        this.value = Long.toString(datetime.getTime());
    }

    @Exclude
    public Date getDate() {
        return new Date(Long.parseLong(this.value));
    }

    @Exclude
    @Override
    public void save() {
        super.save();

        Widget widget = widgetForeignKeyContainer.load();

        if (widget != null) {
            widget.saveFirebaseOnly();
        }
    }

    @Exclude
    public void update(int value) {
        setValue(value);
        save();
    }

    @Exclude
    public void update(boolean value) {
        setValue(value);
        save();
    }

    @Exclude
    public void update(String str) {
        setValue(str);
        save();
    }

    @Exclude
    public void update(double n) {
        setValue(n);
        save();
    }

    @Exclude
    private void setValue(double n) {
        this.value = Double.toString(n);
    }

    @Exclude
    public void setValue(long l) {
        this.value = Long.toString(l);
    }

    @Exclude
    public void setValue(String str) {
        this.value = str;
    }
}
