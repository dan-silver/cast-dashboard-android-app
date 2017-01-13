package com.silver.dan.castdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.silver.dan.castdemo.FirebaseMigration.useFirebaseForReadsAndWrites;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String DEVICE_MIGRATED_TO_FIREBASE = "MIGRATED_TO_FIREBASE";
    public static String LOGOUT = "LOGOUT";

    @BindView(R.id.sign_in_button)
    com.google.android.gms.common.SignInButton signInButton;


    @BindView(R.id.login_loading_spinner)
    ProgressBar loadingSpinner;

    private static GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadingSpinner.setVisibility(View.INVISIBLE);

        final AuthHelper googleAuthHelper = new AuthHelper(getApplicationContext());

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleAuthHelper.getGoogleGSO())
                .enableAutoManage(this, this)
                .build();


        Intent intent = getIntent();
        final boolean shouldLogout = intent.getBooleanExtra(LoginActivity.LOGOUT, false);

        if (shouldLogout) {
            signout();
        }

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (shouldLogout) {
                    return;
                }
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    return;
                }


                // crack open jwt from saved storage, see if it's not expired
                // check if firebaseUserId == user.id (from below)
                // if so, save jwt, scopes to AuthHelper static fields

                String serviceJwt = googleAuthHelper.getSavedServiceJwt();
                if (serviceJwt == null) return;
                JWT jwt = new JWT(serviceJwt);

                String firebaseUserId = jwt.getClaim("firebaseUserId").asString();
                List<String> scopes = jwt.getClaim("grantedScopes").asList(String.class);
                Set<Scope> grantedScopes = new HashSet<>();

                for (String scope : scopes) {
                    grantedScopes.add(new Scope(scope));
                }

                if (firebaseUserId.equals(user.getUid())) {
                    googleAuthHelper.setServiceJwt(serviceJwt); // @todo why? didn't we just get this from authhelper?
                    googleAuthHelper.setNewUserInfo(user, grantedScopes);
                    AuthHelper.user = user;

                    userFinishedAuth();
                }

            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);


        // Moving here because we can't do this on stock widget creation since they might sync an
        // account that has one
        // 
        // Make sure that the stocks have been dumped into db, only happens on first app launch
        // in async call
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Stock.insertAllStocks(getApplicationContext());
            }
        });


    }

    static boolean restoreUser() {
        if (AuthHelper.user != null) {
            return true;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthHelper.user = user;
            return true;
        }
        return false;
    }

    @OnClick(R.id.sign_in_button)
    public void signin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                loadingSpinner.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        loadingSpinner.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);


        AuthHelper authHelper = new AuthHelper(getApplicationContext());
        authHelper.completeCommonAuth(acct, new SimpleCallback<String>() {
            @Override
            public void onComplete(String result) {
                userFinishedAuth();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private SharedPreferences getSharedPref() {
        String SHARED_PREFS = "SHARED_PREFS_USER_FLOW";
        return getApplicationContext().getSharedPreferences(SHARED_PREFS, 0);
    }

    private boolean hasMigrated() {
        return getSharedPref().getBoolean(DEVICE_MIGRATED_TO_FIREBASE, false);
    }

    private void setHasMigrated(boolean migrated) {
        getSharedPref().edit().putBoolean(DEVICE_MIGRATED_TO_FIREBASE, migrated).apply();
    }

    private void userFinishedAuth() {
        if (hasMigrated()) {
            useFirebaseForReadsAndWrites = true;
            launchMainActivity();
            return;
        }

        FirebaseMigration migration = new FirebaseMigration();
        migration.start(getApplicationContext(), new FirebaseMigration.SimpleCompletionListener() {
            @Override
            public void onComplete() {
                setHasMigrated(true);
                useFirebaseForReadsAndWrites = true;
                launchMainActivity();
            }
        });

    }

    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        loadingSpinner.setVisibility(View.GONE);
        signInButton.setVisibility(View.VISIBLE);
    }

    public void signout() {
        AuthHelper.signout();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }
}