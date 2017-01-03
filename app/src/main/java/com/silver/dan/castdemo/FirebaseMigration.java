package com.silver.dan.castdemo;

import android.content.Context;
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

public class FirebaseMigration {
    private DatabaseReference mDatabase;
    public static String dashboardId;

    public static boolean useFirebaseForReadsAndWrites = false;

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
    interface SimpleCompletionListener {
        void onComplete();
    }

    void start(final Context context, final SimpleCompletionListener callback) {
        // check if we've already pushed the dashboard to /user/uid/dashboards
        // if so, get the dashboard ID

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    FirebaseMigration.dashboardId = dataSnapshot.getChildren().iterator().next().getKey();
                    useFirebaseForReadsAndWrites = true;
                    callback.onComplete();
                } else {
                    uploadDashboard(context, new SimpleCompletionListener() {
                        @Override
                        public void onComplete() {
                            callback.onComplete();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(MainActivity.TAG, "loadPost:onCancelled", databaseError.toException());
                callback.onComplete();
                // ...
            }
        };


        DatabaseReference dashboardsRef = mDatabase.child("users").child(LoginActivity.user.getUid()).child("dashboards");
        dashboardsRef.addListenerForSingleValueEvent(postListener);
    }

    private void uploadDashboard(final Context context, final SimpleCompletionListener callback) {
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
                    widget.saveFirstTimeWithMigration();
                }

                useFirebaseForReadsAndWrites = true;
                callback.onComplete();

                //
                // options
                //

                AppSettingsBindings options = new AppSettingsBindings();
                options.loadAllSettingsFromSharedPreferences(context);
                options.saveAllSettings();

            }
        });


    }

}
