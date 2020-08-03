package com.example.gotimer.ui.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AppBlockOverlay extends ViewGroup {

    private Paint mLoadPaint;
    private Context mContext;

    public AppBlockOverlay(Context context) {
        super(context);
        Toast.makeText(getContext(),"HUDView", Toast.LENGTH_LONG).show();
        mContext = context;

        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setTextSize(10);
        mLoadPaint.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
        return true;
    }
}
