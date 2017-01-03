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

    public static boolean useFirebaseForReadsAndWrites = false;

    FirebaseMigration() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    interface SimpleCompletionListener {
        void onComplete();
    }

    void start(final Context context, final SimpleCompletionListener callback) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
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

        DatabaseReference dashboardsRef = mDatabase.child("users").child(LoginActivity.user.getUid());
        dashboardsRef.addListenerForSingleValueEvent(postListener);
    }

    private void uploadDashboard(final Context context, final SimpleCompletionListener callback) {
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
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
