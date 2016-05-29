package com.silver.dan.castdemo;

/**
 * Created by dan on 5/15/16.
 */
public interface FileSavedListener {
    void onSaved();

    void onError(String s);

    void onProgress(int progress, int total);
}
