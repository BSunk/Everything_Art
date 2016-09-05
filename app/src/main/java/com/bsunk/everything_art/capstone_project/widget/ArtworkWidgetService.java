package com.bsunk.everything_art.capstone_project.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bsunk.everything_art.capstone_project.data.ArtworkContract;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

/**
 * Created by Bharat on 8/29/2016.
 */
public class ArtworkWidgetService extends RemoteViewsService {

    public final String LOG_TAG = ArtworkWidgetService.class.getSimpleName();

    // these indices must match the projection
    private static final String[] ARTWORK_COLUMNS = {
            ArtworkContract.Artwork.COLUMN_ID,
            ArtworkContract.Artwork.COLUMN_IMAGE_URL};
    int appwidgetid = 0;
    Context context;

    static final int INDEX_ID= 0;
    static final int INDEX_IMAGE_URL= 1;
    RemoteViews views;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    appwidgetid = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }
                context = getApplicationContext();
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                data = null;
                final long identityToken = Binder.clearCallingIdentity();
                Uri StockURI = ArtworkContract.Artwork.CONTENT_URI;
                data = getContentResolver().query(StockURI,
                        ARTWORK_COLUMNS,
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                views = new RemoteViews(context.getPackageName(), com.bsunk.everything_art.capstone_project.R.layout.artwork_widget_item);
                final String imageURL = data.getString(INDEX_IMAGE_URL);

                Bitmap image = null;
                try {
                    image = Glide.with(ArtworkWidgetService.this)
                            .load(imageURL)
                            .asBitmap()
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(LOG_TAG, "Error retrieving image from" + imageURL, e);
                }
                if (image != null) {
                    views.setImageViewBitmap(com.bsunk.everything_art.capstone_project.R.id.widget_image, image);
                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };

    }
}
