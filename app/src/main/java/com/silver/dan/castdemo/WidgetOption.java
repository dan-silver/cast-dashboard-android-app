package com.silver.dan.castdemo;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silver.dan.castdemo.Widget.getFirebaseDashboardWidgetsRef;

@IgnoreExtraProperties
public class WidgetOption {

    @Exclude
    Widget widgetRef;

    @Exclude
    String key;

    public String value;

    public WidgetOption() {
    }


    @Exclude
    private DatabaseReference getOptionsRef() {
        return getFirebaseDashboardWidgetsRef()
                .child(widgetRef.guid)
                .child("optionsMap");
    }

    @Exclude
    private Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("value", value);

        return result;
    }


    @Exclude
    void associateWidget(Widget widget) {
        this.widgetRef = widget;
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
    public void save() {
        getOptionsRef()
                .child(this.key)
                .setValue(this.toMap());
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


    @Exclude
    public List<String> getList() {
        String[] stringArray = this.value.split(",");
        List<String> items = new ArrayList<>();
        Collections.addAll(items, stringArray);

        items.removeAll(Arrays.asList("", null)); //http://stackoverflow.com/questions/5520693/in-java-remove-empty-elements-from-a-list-of-strings


        if (items.size() == 1 && items.get(0).length() == 0)
            return new ArrayList<>();

        return items;
    }

    @Exclude
    public void update(List<String> enabledIds) {
        setValue(enabledIds);
        save();
    }

    @Exclude
    protected void setValue(List<String> enabledIds) {
        setValue(TextUtils.join(",", enabledIds));
    }
}
