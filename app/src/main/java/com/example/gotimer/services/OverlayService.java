package com.example.gotimer.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
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
import androidx.core.app.NotificationCompat;

import com.example.gotimer.MainActivity;
import com.example.gotimer.R;
import com.example.gotimer.ui.overlay.AppBlockOverlay;
import com.example.gotimer.ui.timer.TimerFragment;
import com.example.gotimer.util.TransparentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class OverlayService extends Service {

    private ViewGroup mViewGroup;
    Handler handler;
    Runnable runnableCode;
    WindowManager windowManager;
    ArrayList<String> processList;

    boolean alreadyDisplayed;
    public static boolean isServiceRunning = false;
    private static final int NOTIFICATION_ID = 5;
    private static final String NOTIFICATION_CHANNEL_ID = "com.example.gotimer.services" +
            ".overlayservice";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        startServiceAndNotification();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processList = intent.getStringArrayListExtra("processList");

        isServiceRunning = intent.getBooleanExtra("serviceOn", false);
        if (isServiceRunning) {
            Toast.makeText(this, "GoTimer Enabled", Toast.LENGTH_SHORT).show();
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
        } else {
            processList = null;
            stopMyService();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void blockApp() {
        String currentAppProcess = getCurrentApp();
        //IF THIS GIVES YOU ERROR CHECK PERMISSIONS (USAGE, LOCATION, BATTERY)
        if (processList != null && currentAppProcess != null) {
            for (String processName : processList) {
                if (currentAppProcess.equals(processName)) {
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
        }
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
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);

        mViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
                startHomescreen.addCategory(Intent.CATEGORY_HOME);
                startHomescreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startHomescreen);
                windowManager.removeViewImmediate(mViewGroup);
                alreadyDisplayed = false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "GoTimer";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name,
                importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "GoTimer";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startServiceAndNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle("GoTimer"
        ).setContentText("Currently blocking selected applications").setSmallIcon(R.drawable.ic_timer).setContentIntent(pendingIntent).setOngoing(true).setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        processList = null;
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "App Block Ended", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopMyService() {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
        mNotificationManager.cancel(NOTIFICATION_ID);
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }
}
