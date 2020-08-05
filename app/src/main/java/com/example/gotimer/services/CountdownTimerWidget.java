package com.example.gotimer.services;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.gotimer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownTimerWidget extends AppWidgetProvider {

    private static final String UPDATE_WIDGET = "com.example.gotimer.services.countdowntimerwidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_timer_widget);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, CountdownTimerWidget.class);
        if (intent.getAction().equals(UPDATE_WIDGET)) {
            int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
            // handle intent here
            long mEndTime = intent.getLongExtra("countdown", 0);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedString = sdf.format(new Date(mEndTime));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_timer_widget);
            views.setTextViewText(R.id.countdown_text, formattedString);
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }


}

