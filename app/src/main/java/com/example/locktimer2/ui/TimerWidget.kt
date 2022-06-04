package com.example.locktimer2.ui

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.locktimer2.R
import com.example.locktimer2.timer.startDefaultTimer
import com.example.locktimer2.util.ACTION_START_DEFAULT_TIMER

class TimerWidget : AppWidgetProvider() {

    companion object {

        fun createIntentForDefaultTimer(context: Context): Intent =
            Intent(context, TimerWidget::class.java)
                .setAction(ACTION_START_DEFAULT_TIMER)
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach {
            updateWidget(context, appWidgetManager, it)
        }
    }

    private fun updateWidget(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        widgetId: Int
    ) {
        val rv = RemoteViews(context?.packageName, R.layout.widget_timer)
        val intent = createIntentForDefaultTimer(requireNotNull(context))
        val pi = PendingIntent.getBroadcast(context, widgetId, intent, FLAG_IMMUTABLE)

        rv.setOnClickPendingIntent(R.id.widget_btn, pi)

        appWidgetManager?.updateAppWidget(widgetId, rv)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == ACTION_START_DEFAULT_TIMER) {
            context?.startDefaultTimer()
        }
    }
}
