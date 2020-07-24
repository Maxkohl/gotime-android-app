package com.example.gotimer.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "profiles_table")
public class Profile {

    @NonNull
    @PrimaryKey
    private int profileId;

    @ColumnInfo(name = "name")
    @NonNull
    private String profileName;

    @NonNull
    private long startTime;
    @NonNull
    private long endTime;

    //TODO Uncomment. How to store in SQLite?
//    List<String> daysActive;
//
//    List<String> blockedApps;

    @NonNull
    boolean isOn;

    //TODO Add location variable to be saved into SQLite database. What's the best way to store?


    public Profile(@NonNull String profileName, @NonNull long startTime,
                   @NonNull long endTime) {
        this.profileName = profileName;
        this.startTime = startTime;
        this.endTime = endTime;
//        this.daysActive = daysActive;
//        this.blockedApps = blockedApps;
        this.isOn = true;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    @NonNull
    public String getProfileName() {
        return profileName;
    }

    @NonNull
    public long getStartTime() {
        return startTime;
    }

    @NonNull
    public long getEndTime() {
        return endTime;
    }

//    public List<String> getDaysActive() {
//        return daysActive;
//    }
//
//    public List<String> getBlockedApps() {
//        return blockedApps;
//    }

    public boolean isOn() {
        return isOn;
    }

    public void setProfileName(@NonNull String profileName) {
        this.profileName = profileName;
    }

    public void setStartTime(@NonNull long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(@NonNull long endTime) {
        this.endTime = endTime;
    }

//    public void setDaysActive(List<String> daysActive) {
//        this.daysActive = daysActive;
//    }
//
//    public void setBlockedApps(List<String> blockedApps) {
//        this.blockedApps = blockedApps;
//    }

    public void setOn(boolean on) {
        isOn = on;
    }
}

