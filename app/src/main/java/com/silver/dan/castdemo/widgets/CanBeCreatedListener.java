package com.silver.dan.castdemo.widgets;

/**
 * Created by dan on 5/28/16.
 */

public abstract class CanBeCreatedListener {
    private int requiredCondition;

    public abstract void onCanBeCreated();

    public boolean ifConditionsAreMet(int key) {
        return key == requiredCondition;
    }

    public void setRequestCallbackReturnCode(int condition) {
        this.requiredCondition = condition;
    }
}
