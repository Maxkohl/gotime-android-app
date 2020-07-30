package com.example.gotimer.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.gotimer.MainActivity;
import com.example.gotimer.R;
import com.example.gotimer.ui.overlay.AppBlockOverlay;
import com.example.gotimer.util.TransparentActivity;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class OverlayService extends Service {

    private ViewGroup mViewGroup;
    Handler handler;
    Runnable runnableCode;
    WindowManager windowManager;

    boolean alreadyDisplayed;

    int LAYOUT_FLAG;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Test Service Started", Toast.LENGTH_SHORT).show();
        alreadyDisplayed = false;
        handler = new Handler(Looper.getMainLooper());
        runnableCode = new Runnable() {
            @Override
            public void run() {
                blockApp();
                handler.postDelayed(runnableCode, 1000);
            }
        };
        handler.post(runnableCode);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    public void blockApp() {
        String currentAppProcess = getCurrentApp();
        String lastProcess = "";
//        if (currentAppProcess != lastProcess) {
        if (currentAppProcess.equals("com.instagram.android") || currentAppProcess.equals(
                "com" +
                        ".twitter.android")) {
            if (!alreadyDisplayed) {
                displayOverlay();
                alreadyDisplayed = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void displayOverlay() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        mViewGroup = (ViewGroup) inflater.inflate(R.layout.overlay_layout, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;


        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        if (!mViewGroup.isShown()) {
            windowManager.addView(mViewGroup, params);
        }

        //Launches TransparentActivity to "clear" last process name on actual phone. Then
        // launches home screen
        Intent mainIntent = new Intent(this, TransparentActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
        startHomescreen.addCategory(Intent.CATEGORY_HOME);
        startHomescreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHomescreen);

        mViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
//                startHomescreen.addCategory(Intent.CATEGORY_HOME);
//                startHomescreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startHomescreen);
                mViewGroup.setVisibility(View.INVISIBLE);
                alreadyDisplayed = false;
            }
        });
    }

//    public void removeOverlay() {
//        if (removeOverlay) {
//            removeOverlay = false;
//            mViewGroup.setVisibility(View.INVISIBLE);
//            try {
//                windowManager.removeView(mViewGroup);
//            } catch (IllegalArgumentException e) {
//                Log.e("REMOVE OVERLAY", "ERROR");
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(mViewGroup);
    }
}
