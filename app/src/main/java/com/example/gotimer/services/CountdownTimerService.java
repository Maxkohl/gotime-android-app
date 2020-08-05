package com.example.gotimer.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

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


        mEndTime = timerViewModel.getEndTime();
        long currentTime = System.currentTimeMillis();
        long durationTime = mEndTime - currentTime;
        timer = new CountDownTimer(durationTime, 1000) {
            public void onTick(long millisUntilFinished) {
                broadcastIntent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(broadcastIntent);

//                timerCountdown.setText("Time Remaining: " + new SimpleDateFormat(
//                        "HH:mm:ss").format(new Date(millisUntilFinished)));

            }

            public void onFinish() {
                timerCountdown.setText("done!");
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
