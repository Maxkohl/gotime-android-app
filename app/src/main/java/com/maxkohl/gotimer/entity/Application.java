package com.maxkohl.gotimer.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Application implements Serializable {

    String appName;
    Drawable appIconRes;
    String processName;
    boolean isSelected;

    public Application(String appName, Drawable appIconRes, String processName) {
        this.appName = appName;
        this.appIconRes = appIconRes;
        this.isSelected = false;
        this.processName = processName;
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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
