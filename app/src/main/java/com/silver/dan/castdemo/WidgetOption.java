package com.silver.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

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

    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "1" : "0";
    }

    public void setIntValue(int value) {
        this.value = String.valueOf(value);
    }

    public int getIntValue() {
        return Integer.valueOf(value);
    }

    public void update(int value) {
        setIntValue(value);
        save();
    }

    public void update(boolean isChecked) {
        setBooleanValue(isChecked);
        save();
    }

    public void update(String str) {
        this.value = str;
        save();
    }

    public void update(double n) {
        this.value = String.valueOf(n);
        this.save();
    }

}
