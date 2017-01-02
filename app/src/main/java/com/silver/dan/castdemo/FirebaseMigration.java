package com.silver.dan.castdemo;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dan on 1/1/17.
 */

class FirebaseMigration {
    private DatabaseReference mDatabase;
    public static String dashboardId;

    FirebaseMigration() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @IgnoreExtraProperties
    public class Dashboard {
        private String id;
        String userId;


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
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                DatabaseReference dashboardsRef = mDatabase
                    .child("users")
                    .child(LoginActivity.user.getUid())
                    .child("dashboards");

                dashboardId = dashboardsRef.push().getKey();

                Dashboard dash = new Dashboard(dashboardId);
                Map<String, Object> postValues = dash.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(dashboardId, postValues);

                dashboardsRef.updateChildren(childUpdates);


                //
                // upload widgets
                //

                for (Widget widget : widgets) {
                    // save locally for now also
                    // force saving the widget populates the GUID locally and uploads a clone to firebase
                    widget.save();
                }

            }
        });


    }

}
