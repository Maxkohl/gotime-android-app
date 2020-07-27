package com.example.gotimer.entity;

public class Application {

    String appName;
    String appIconRes;
    boolean isSelected;

    public Application(String appName, String appIconRes, boolean isSelected) {
        this.appName = appName;
        this.appIconRes = appIconRes;
        this.isSelected = isSelected;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIconRes() {
        return appIconRes;
    }

    public void setAppIconRes(String appIconRes) {
        this.appIconRes = appIconRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
