package com.example.gotimer.services;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppMonitorService extends AccessibilityService {

    //TODO Put service on different thread than Main. Use Executor?

    Handler handler;
    Runnable runnableCode;
    Context mContext;
    boolean serviceOn;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mContext = getApplicationContext();
        handler = new Handler(Looper.getMainLooper());
        runnableCode = new Runnable() {
            @Override
            public void run() {
                if (serviceOn) {
                    blockApp();
                }
                handler.postDelayed(runnableCode, 1000);
            }
        };
        handler.post(runnableCode);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceOn = intent.getBooleanExtra("serviceOn", false);

        return Service.START_STICKY;
    }

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

    public boolean blockApp() {
        String currentAppProcess = getCurrentApp();
        if ("com.instagram.android".equals(currentAppProcess)) {
            openHomeScreen();
            //TODO add a slight delay here?
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean openHomeScreen() {
        Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomescreen);
        return true;
    }
}
