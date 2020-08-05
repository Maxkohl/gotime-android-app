package com.example.gotimer.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class CountdownTimerService extends Service {

    private static final String TAG = "CountdownTimerService";
    private static final String COUNTDOWN_BR = "com.example.gotimer.services.countdowntimerservice";

    Intent broadcastIntent = new Intent((COUNTDOWN_BR));
    private long mDurationTime;
    CountDownTimer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDurationTime = intent.getLongExtra("durationTime", 0);

        Toast.makeText(this, "TIMER SERVICE STARTED", Toast.LENGTH_SHORT).show();
        timer = new CountDownTimer(mDurationTime, 1000) {
            public void onTick(long millisUntilFinished) {
                broadcastIntent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(broadcastIntent);
            }

            public void onFinish() {
//                timerCountdown.setText("done!");
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
