package com.example.gotimer.entity;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Entity(tableName = "profiles_table")
public class Profile {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int profileId;

    @ColumnInfo(name = "name")
    @NonNull
    private String profileName;

    @NonNull
    private long startTime;
    @NonNull
    private long endTime;

    //TODO Uncomment. How to store in SQLite?
    private String daysActive;

    private String blockedApps;

    private String blockedAppsProcessNames;

    @NonNull
    private boolean isBlockActive;

    @NonNull
    private boolean isAlarmActive;

    private int alarmId;

    private String locationAddress;

    //TODO Add location variable to be saved into SQLite database. What's the best way to store?


    public Profile(@NonNull String profileName, @NonNull long startTime,
                   @NonNull long endTime) {
        this.profileName = profileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBlockActive = false;
        this.isAlarmActive = false;
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

    public String getStartTimeString() {
        long unixSeconds = startTime;
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = "Start Time: " + sdf.format(date);
        return (new SimpleDateFormat("HH:mm")).format(new Date(unixSeconds));
    }

    public String getEndTimeString() {
        long unixSeconds = endTime;
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");

//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedTime = "End Time: " + sdf.format(date);
        return (new SimpleDateFormat("HH:mm")).format(new Date(unixSeconds));
    }

    public List<String> getDaysActiveList() {
        return new ArrayList<String>(Arrays.asList(daysActive.split("-")));
    }

    public String getBlockedApps() {
        return blockedApps;
    }

    public boolean isBlockActive() {
        return isBlockActive;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDaysActiveString(List<String> daysActive) {
        this.daysActive = stringifyList(daysActive);
    }

    public void setBlockedApps(String blockedApps) {
        this.blockedApps = blockedApps;
    }

    public void setBlockActive(boolean blockActive) {
        isBlockActive = blockActive;
    }

    public String getDaysActive() {
        return daysActive;
    }

    public void setDaysActive(String daysActive) {
        this.daysActive = daysActive;
    }

    public void setBlockedAppsString(List<String> blockedApps) {
        this.blockedApps = stringifyList(blockedApps);
    }

    public List<String> getBlockedAppsList() {
        return new ArrayList<String>(Arrays.asList(blockedApps.split(" ")));
    }

    private String stringifyList(List<String> list) {
        String resultString = "";
        for (String element : list) {
            resultString = resultString + element + " ";
        }
        return resultString;
    }

    public String getBlockedAppsProcessNames() {
        return blockedAppsProcessNames;
    }

    public void setBlockedAppsProcessNames(String blockesAppsProcessNames) {
        this.blockedAppsProcessNames = blockesAppsProcessNames;
    }

    public void setBlockedProcessNamesString(List<String> blockedProcessNames) {
        this.blockedAppsProcessNames = stringifyList(blockedProcessNames);
    }

    public List<String> getBlockedProcessNames() {
        if (blockedAppsProcessNames != null) {
            return new ArrayList<String>(Arrays.asList(blockedAppsProcessNames.split(" ")));
        }
        return new ArrayList<>();
    }

    public boolean isAlarmActive() {
        return isAlarmActive;
    }

    public void setAlarmActive(boolean alarmActive) {
        isAlarmActive = alarmActive;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }
}

