package com.silver.dan.castdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.silver.dan.castdemo.FirebaseMigration.useFirebaseForReadsAndWrites;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String DEVICE_MIGRATED_TO_FIREBASE = "MIGRATED_TO_FIREBASE";
    public static String LOGOUT = "LOGOUT";
    private FirebaseAuth mAuth;

    @BindView(R.id.sign_in_button)
    com.google.android.gms.common.SignInButton signInButton;


    @BindView(R.id.login_loading_spinner)
    ProgressBar loadingSpinner;

    private static GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 10000;

    static FirebaseUser user;
    private String SHARED_PREFS = "SHARED_PREFS_USER_FLOW";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadingSpinner.setVisibility(View.INVISIBLE);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
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
                if (user != null) {
                    LoginActivity.user = user;
                    userFinishedAuth();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

    }

    static boolean restoreUser() {
        if (LoginActivity.user != null) {
            return true;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            LoginActivity.user = user;
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

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        loadingSpinner.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                    } else {
                        LoginActivity.user = task.getResult().getUser();
                        userFinishedAuth();
                    }


                }
            });
    }

    private SharedPreferences getSharedPref() {
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
        //Delete.tables(Widget.class, WidgetOption.class, Stock.class);
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