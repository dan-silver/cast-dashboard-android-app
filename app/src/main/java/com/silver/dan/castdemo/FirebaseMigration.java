package com.silver.dan.castdemo;

import android.content.Context;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.silver.dan.castdemo.widgetList.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dan on 1/1/17.
 */

class FirebaseMigration {
    private final Context context;
    private DatabaseReference mDatabase;
    public static String dashboardId;

    FirebaseMigration(Context applicationContext) {
        this.context = applicationContext;
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @IgnoreExtraProperties
    public class Dashboard {
        private String id;
        //        public String id;
        public String userId;

//        public ArrayList<Map<String, Object>> widgets;

        public Dashboard() {

        }

        public Dashboard(String id) {
            this.id = id;
            this.userId = LoginActivity.user.getUid();
        }


        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("userId", userId);
//            result.put("widgets", widgets);
            return result;
        }
    }

    void start() {
        // check if we've already pushed the dashboard to /user/uid/dashboards
        // if so, get the dashboard ID

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    FirebaseMigration.dashboardId = dataSnapshot.getChildren().iterator().next().getKey();
                } else {
                    uploadDashboard();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(MainActivity.TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


        DatabaseReference dashboardsRef = mDatabase.child("users").child(LoginActivity.user.getUid()).child("dashboards");
        dashboardsRef.addListenerForSingleValueEvent(postListener);
    }

    private void uploadDashboard() {
        // async fetch all saved widgets
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {


                DatabaseReference dashboardsRef = mDatabase.child("users").child(LoginActivity.user.getUid()).child("dashboards");

                dashboardId = dashboardsRef.push().getKey();

                Dashboard dash = new Dashboard(dashboardId);//, username, title, body);
                Map<String, Object> postValues = dash.toMap();


                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(dashboardId, postValues);

                dashboardsRef.updateChildren(childUpdates);


                //
                // insert widgets
                //

                DatabaseReference widgetsRef = dashboardsRef.child(dashboardId).child("widgets");

                Map<String, Object> widgetUpdates = new HashMap<>();
                for (Widget widget : widgets) {
                    String widgetUid = widgetsRef.push().getKey();
                    widgetUpdates.put(widgetUid, widget.toMap());

                    // save locally for now also
                    widget.guid = widgetUid;
                    widget.save();
                }

                widgetsRef.updateChildren(widgetUpdates);
            }
        });


    }

}
