package com.example.gotimer.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CountdownTimerService extends Service {

    private static final String TAG = "CountdownTimerService";
    private static final String COUNTDOWN_BR = "com.example.gotimer.services.countdowntimerservice";

    Intent broadcastIntent = new Intent((COUNTDOWN_BR));
    private long mEndTime;
    CountDownTimer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "TIMER SERVICE STARTED", Toast.LENGTH_SHORT).show();

        long currentTime = System.currentTimeMillis();
        long durationTime = mEndTime - currentTime;
        timer = new CountDownTimer(270000, 1000) {
            public void onTick(long millisUntilFinished) {
                broadcastIntent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(broadcastIntent);
            }

            public void onFinish() {
//                timerCountdown.setText("done!");
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mEndTime = intent.getLongExtra("endTime", 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
