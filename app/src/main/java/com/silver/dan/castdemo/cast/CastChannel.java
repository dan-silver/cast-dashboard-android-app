package com.silver.dan.castdemo.cast;

import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastSession;
import com.silver.dan.castdemo.MainActivity;

/**
 * Created by dan on 1/2/18.
 */


public class CastChannel implements Cast.MessageReceivedCallback {
    String namespace;

    public String getNamespace() {
        return namespace;
    }

    public CastChannel(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace,
                                  String message) {
        Log.d(MainActivity.TAG, "onMessageReceived: " + message);
    }

    public void sendMessage(CastSession session, String message) {
        if (session != null) {
            session.sendMessage(this.getNamespace(), message);
        }
    }

}