package com.silver.dan.castdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dan on 1/1/17.
 */

class FirebaseMigration {
    private DatabaseReference mDatabase;

    static boolean useFirebaseForReadsAndWrites = false;

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

        DatabaseReference dashboardsRef = mDatabase.child("users").child(AuthHelper.user.getUid());
        dashboardsRef.addListenerForSingleValueEvent(postListener);
    }

    private void uploadDashboard(final Context context, final SimpleCompletionListener callback) {
        ConditionGroup conditions = ConditionGroup.clause();

        QueryTransaction.Builder<Widget> query = new QueryTransaction.Builder<>(
                new Select()
                        .from(Widget.class)
                        .where(conditions)
                        .orderBy(Widget_Table.position, true));


        FlowManager
                .getDatabase(WidgetDatabase.class)
                .beginTransactionAsync(query.queryResult(new QueryTransaction.QueryResultCallback<Widget>() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction, @NonNull CursorResult<Widget> result) {
                        List<Widget> widgets = result.toList();

                        //
                        // upload widgets
                        //


                        if (widgets != null) {
                            for (Widget widget : widgets) {
                                widget.saveFirstTimeWithMigration();
                            }
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
                }).build()).build().execute();





    }

}
