package com.example.gotimer.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppMonitorService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getCurrentApp();
        Timer timer = new Timer("AppCheckServices");
        timer.schedule(updateTask, 1000L, 1000L);

        return Service.START_NOT_STICKY;
    }

    private TimerTask updateTask = new TimerTask() {

        @Override
        public void run() {
            getCurrentApp();
        }
    };

    private String getCurrentApp() {
        if (Build.VERSION.SDK_INT >= 21) {
            String currentApp = null;
            UsageStatsManager usm =
                    (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> applist = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (applist != null && applist.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : applist) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            Log.e("AppMontiorServices", "Current App in foreground is: " + currentApp);

            return currentApp;

        } else {
            //For anything lower than Lollipop
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String mm = (manager.getRunningTasks(1).get(0)).topActivity.getPackageName();
            Log.e("AppMontiorService", "Current App in foreground is: " + mm);
            return mm;
        }
    }
}
