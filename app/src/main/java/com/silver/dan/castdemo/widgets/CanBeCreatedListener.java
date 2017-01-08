package com.silver.dan.castdemo.widgets;

/**
 * Created by dan on 5/28/16.
 */

public abstract class CanBeCreatedListener {
    private int requiredCondition;

    public abstract void onCanBeCreated();

    public boolean checkIfConditionsAreMet(int key) {
        return key == requiredCondition;
    }

    void setRequiredCondition(int condition) {
        this.requiredCondition = condition;
    }
}
