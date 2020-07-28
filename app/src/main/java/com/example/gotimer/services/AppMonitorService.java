package com.example.gotimer.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class AppMonitorService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityManager mActivityManager =
                (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        String mPackageName;
        if (Build.VERSION.SDK_INT > 20) {
            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }

        Log.e("TestFunc", mPackageName);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        context.startService(new Intent(context, AppMonitorService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, AppMonitorService.class));
    }

}
