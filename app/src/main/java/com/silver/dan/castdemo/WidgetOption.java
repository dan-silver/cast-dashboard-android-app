package com.silver.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import java.util.Date;

@ModelContainer
@Table(database = WidgetDatabase.class)
public class WidgetOption extends BaseModel {


    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public String key;

    @Column
    public String value;

    public WidgetOption() {
    }

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    ForeignKeyContainer<Widget> widgetForeignKeyContainer;

    public void associateWidget(Widget widget) {
        widgetForeignKeyContainer = FlowManager.getContainerAdapter(Widget.class).toForeignKeyContainer(widget);
    }

    public boolean getBooleanValue() {
        return value.equals("1");
    }

    public void setValue(boolean booleanValue) {
        this.value = booleanValue ? "1" : "0";
    }

    public void setValue(int value) {
        this.value = Integer.toString(value);
    }

    public int getIntValue() {
        return Integer.valueOf(value);
    }

    public void setValue(Date datetime) {
        this.value = Long.toString(datetime.getTime());
    }

    public Date getDate() {
        return new Date(Long.parseLong(this.value));
    }

    public void update(int value) {
        setValue(value);
        save();
    }

    public void update(boolean value) {
        setValue(value);
        save();
    }

    public void update(String str) {
        setValue(str);
        save();
    }

    public void update(double n) {
        setValue(n);
        this.save();
    }

    private void setValue(double n) {
        this.value = Double.toString(n);
    }

    public void setValue(long l) {
        this.value = Long.toString(l);
    }

    public void setValue(String str) {
        this.value = str;
    }
}
