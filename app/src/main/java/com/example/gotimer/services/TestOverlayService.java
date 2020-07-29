package com.example.gotimer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gotimer.R;

public class TestOverlayService extends Service {

    private WindowManager windowManager;
    private ImageView imageView;

    @Override
    public void onCreate() {
        super.onCreate();
        // Get window manager reference


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
