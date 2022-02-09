package com.example.memoapplication.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.memoapplication.MainActivity
import com.example.memoapplication.MemoInfoActivity
import com.example.memoapplication.R

class WidgetProvider: AppWidgetProvider() {
    // activity 로 이동
    private fun setMyAction(context: Context?): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun buildURIIntent(context: Context?): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/ImGaram"))
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    @SuppressLint("RemoteViewLayout")
    private fun addViews(context: Context?): RemoteViews {
        val views = RemoteViews(context?.packageName, R.layout.widget_memo)
        views.setOnClickPendingIntent(R.id.move_main_activity, setMyAction(context))
        views.setOnClickPendingIntent(R.id.move_git, buildURIIntent(context))
        return views
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?,
    ) {
        appWidgetIds?.forEach { appWidgetId ->
            val views: RemoteViews = addViews(context)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}