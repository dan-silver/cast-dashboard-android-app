package com.silver.dan.castdemo;

/**
 * Created by dan on 1/8/17.
 */

public interface SimpleCallback<T> {
    void onComplete(T result);
    void onError(Exception e);
}

