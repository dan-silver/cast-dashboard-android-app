package com.silver.dan.castdemo;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dan on 1/7/17.
 */

public class Dashboard {
    List<Widget> widgets = new ArrayList<>();;
    AppSettingsBindings settings;

    public Widget getWidgetById(String widgetKey) {
        if (widgets == null)
            return null;
        for (Widget w : widgets) {
            if (w.guid.equals(widgetKey)) {
                return w;
            }
        }
        return null;
    }

    public void clearData() {
        this.widgets.clear();
        this.settings = null;
    }

    interface OnLoadCallback  {
        void onReady();

        void onError();
    }

    public Dashboard() {

    }

    public static DatabaseReference getFirebaseUserDashboardReference() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase
            .child("users")
            .child(AuthHelper.user.getUid());
    }

    private void addOptions(DataSnapshot options, Context ctx) {
        settings = options.getValue(AppSettingsBindings.class);

        if (settings == null) {
            settings = new AppSettingsBindings();
        }
        settings.initDefaults(ctx);
    }

    void loadFromFirebase(final Context ctx, final OnLoadCallback callback) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addOptions(dataSnapshot.child("options"), ctx);
                addWidgets(dataSnapshot.child("widgets"));
                callback.onReady();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError();
            }
        };

        getFirebaseUserDashboardReference().addListenerForSingleValueEvent(postListener);

    }
    private void addWidgets(DataSnapshot rawWidgets) {
        for (DataSnapshot nextWidget : rawWidgets.getChildren()) {
            Widget widget = nextWidget.getValue(Widget.class);

            widget.guid = nextWidget.getKey();

            Widget.loadOptions(widget);
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
    }
}
