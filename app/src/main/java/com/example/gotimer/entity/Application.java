package com.example.gotimer.entity;

import android.graphics.drawable.Drawable;

public class Application {

    String appName;
    Drawable appIconRes;
    boolean isSelected;

    public Application(String appName, Drawable appIconRes) {
        this.appName = appName;
        this.appIconRes = appIconRes;
        this.isSelected = false;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIconRes() {
        return appIconRes;
    }

    public void setAppIconRes(Drawable appIconRes) {
        this.appIconRes = appIconRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
