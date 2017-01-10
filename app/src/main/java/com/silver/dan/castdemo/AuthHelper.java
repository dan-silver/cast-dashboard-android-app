package com.silver.dan.castdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dan on 1/8/17.
 */

public class AuthHelper {
    public String AuthHelperSharedPreferences = "AuthHelperSharedPreferences";
    private String SERVICE_JWT = "SERVICE_JWT";

    public static FirebaseUser user;
    static String userJwt;
    public static Set<Scope> grantedScopes;

    static String googleAccessToken;
    static Date googleAccessTokenExpiresAt;

    private final Context context;

    private FirebaseAuth mAuth;

    public AuthHelper(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }


    // Configure Google Sign In
    public GoogleSignInOptions getGoogleGSO() {
        return getGoogleGSO(new HashSet<Scope>());
    }


    public GoogleSignInOptions getGoogleGSO(Set<Scope> scopes) {
        GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.web_client_id))
                .requestServerAuthCode(context.getString(R.string.web_client_id), false)
                .requestEmail();

        for (Scope scope : scopes)
            gsoBuilder.requestScopes(scope);

        return gsoBuilder.build();
    }

    public String getSavedServiceJwt() {
        SharedPreferences prefs = context.getSharedPreferences(AuthHelperSharedPreferences, Context.MODE_PRIVATE);
        String jwt = prefs.getString(SERVICE_JWT, "");
        if (jwt.equals("")) {
            return null;
        }
        return jwt;
    }

    public void setServiceJwt(String userJwt) {
        AuthHelper.userJwt = userJwt;
    }

    public void setNewUserInfo(FirebaseUser user, Set<Scope> grantedScopes) {
        AuthHelper.user = user;
        AuthHelper.grantedScopes = grantedScopes;
    }

    public void completeCommonAuth(final GoogleSignInAccount acct, final SimpleCallback<String> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                setNewUserInfo(authResult.getUser(), acct.getGrantedScopes());

                String userId = authResult.getUser().getUid();
                String serverAuthCode = acct.getServerAuthCode();
                AuthHelper authHelper = new AuthHelper(context);

                authHelper.exchangeServerAuthCodeForJWT(userId, serverAuthCode, acct.getGrantedScopes(), new SimpleCallback<String>() {

                    @Override
                    public void onComplete(String jwt) {
                        SharedPreferences prefs = context.getSharedPreferences(AuthHelperSharedPreferences, Context.MODE_PRIVATE);
                        prefs.edit().putString(SERVICE_JWT, jwt).apply();
                        callback.onComplete(jwt);
                    }
                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onError(e);
            }
        });
    }


    // implements caching on expiresAt field
    public static void getGoogleAccessToken(Context context, final SimpleCallback<String> callback) {
        if (AuthHelper.googleAccessToken != null) {
            Date now = new Date();

            // won't expire within the next 5 minutes
            if (AuthHelper.googleAccessTokenExpiresAt.getTime() - now.getTime()>= 5*60*1000) {
                callback.onComplete(AuthHelper.googleAccessToken);
                return;
            }
        }

        Ion.with(context)
            .load(context.getString(R.string.APP_URL) + "/exchangeServiceJWTForGoogleAccessToken")
            .setBodyParameter("jwt", userJwt)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e != null) {
                        callback.onError(e);
                        return;
                    }
                    String accessToken = result.get("googleAccessToken").getAsString();
                    long expiresAt = result.get("expiresAt").getAsLong();

                    AuthHelper.googleAccessToken = accessToken;
                    AuthHelper.googleAccessTokenExpiresAt = new Date(expiresAt);

                    callback.onComplete(accessToken);
                }
            });

    }

    public static void signout() {
        AuthHelper.googleAccessToken = null;
        AuthHelper.googleAccessTokenExpiresAt = null;
    }


    void exchangeServerAuthCodeForJWT(String firebaseUserId, String authCode, Set<Scope> grantedScopes, final SimpleCallback<String> jwtCallback) {
        Ion.with(context)
                .load(context.getString(R.string.APP_URL) + "/exchangeServerAuthCodeForJWT")
                .setBodyParameter("serverCode", authCode)
                .setBodyParameter("firebaseUserId", firebaseUserId)
                .setBodyParameter("grantedScopes", android.text.TextUtils.join(",", grantedScopes))
                .asJsonObject()
                .setCallback(new com.koushikdutta.async.future.FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            jwtCallback.onError(e);
                            return;
                        }
                        String jwt = result.get("serviceAccessToken").getAsString();
                        AuthHelper.userJwt = jwt;
                        jwtCallback.onComplete(jwt);
                    }
                });
    }
}
