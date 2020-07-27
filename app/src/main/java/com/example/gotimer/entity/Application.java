package com.example.gotimer.entity;

public class Application {

    String appName;
    int appIconRes;
    boolean isSelected;

    public Application(String appName, int appIconRes) {
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

    public int getAppIconRes() {
        return appIconRes;
    }

    public void setAppIconRes(int appIconRes) {
        this.appIconRes = appIconRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
