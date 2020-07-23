package com.example.gotimer.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "profile")
public class Profile {

    @NonNull
    @PrimaryKey
    private int profileId;

    @ColumnInfo(name = "name")
    @NonNull
    String profileName;

    @NonNull
    String startTime;
    @NonNull
    String endTime;

    List<String> daysActive;

    List<String> blockedApps;

    @NonNull
    boolean isOn;

    //TODO Add location variable to be saved into SQLite database. What's the best way to store?


    public Profile(@NonNull String profileName, @NonNull String startTime,
                   @NonNull String endTime, List<String> daysActive, List<String> blockedApps) {
        this.profileName = profileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysActive = daysActive;
        this.blockedApps = blockedApps;
        this.isOn = false;
    }

    public int getProfileId() {
        return profileId;
    }

    @NonNull
    public String getProfileName() {
        return profileName;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    @NonNull
    public String getEndTime() {
        return endTime;
    }

    public List<String> getDaysActive() {
        return daysActive;
    }

    public List<String> getBlockedApps() {
        return blockedApps;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setProfileName(@NonNull String profileName) {
        this.profileName = profileName;
    }

    public void setStartTime(@NonNull String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(@NonNull String endTime) {
        this.endTime = endTime;
    }

    public void setDaysActive(List<String> daysActive) {
        this.daysActive = daysActive;
    }

    public void setBlockedApps(List<String> blockedApps) {
        this.blockedApps = blockedApps;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
