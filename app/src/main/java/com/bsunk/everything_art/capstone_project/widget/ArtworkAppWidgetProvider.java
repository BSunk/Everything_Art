package com.bsunk.everything_art.capstone_project.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Created by Bharat on 8/29/2016.
 */
//Implements a widget using an adapterviewflipper and RemoteViews/ContentProvider for the image data. Takes the images from the favorites database.
public class ArtworkAppWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "com.android.bharat.capstone_project.ACTION_DATA_UPDATED";
    public static final String ACTION_NEXT = "com.android.bharat.capstone_project.NEXT";
    public static final String ACTION_PREVIOUS = "com.android.bharat.capstone_project.PREVIOUS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, ArtworkWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), com.bsunk.everything_art.capstone_project.R.layout.artwork_widget);
            rv.setRemoteAdapter(com.bsunk.everything_art.capstone_project.R.id.widget_view_flipper, intent);
            rv.setEmptyView(com.bsunk.everything_art.capstone_project.R.id.widget_view_flipper, com.bsunk.everything_art.capstone_project.R.id.empty_view);

            final Intent nextIntent = new Intent(context,
                    ArtworkAppWidgetProvider.class);
            nextIntent.setAction(ArtworkAppWidgetProvider.ACTION_NEXT);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            final PendingIntent nextPendingIntent = PendingIntent
                    .getBroadcast(context, 0, nextIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(com.bsunk.everything_art.capstone_project.R.id.widget_right, nextPendingIntent);

            final Intent prevIntent = new Intent(context,
                    ArtworkAppWidgetProvider.class);
            prevIntent.setAction(ArtworkAppWidgetProvider.ACTION_PREVIOUS);
            prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            final PendingIntent prevPendingIntent = PendingIntent
                    .getBroadcast(context, 0, prevIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(com.bsunk.everything_art.capstone_project.R.id.widget_left, prevPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ACTION_NEXT)) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    com.bsunk.everything_art.capstone_project.R.layout.artwork_widget);
            rv.showNext(com.bsunk.everything_art.capstone_project.R.id.widget_view_flipper);
            AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID), rv);
        }
        if (action.equals(ACTION_PREVIOUS)) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    com.bsunk.everything_art.capstone_project.R.layout.artwork_widget);
            rv.showPrevious(com.bsunk.everything_art.capstone_project.R.id.widget_view_flipper);
            AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID), rv);
        }

        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, com.bsunk.everything_art.capstone_project.R.id.widget_view_flipper);
        }

        super.onReceive(context, intent);
    }
}
