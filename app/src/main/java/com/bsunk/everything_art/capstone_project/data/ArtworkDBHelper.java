package com.bsunk.everything_art.capstone_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bharat on 8/24/2016.
 */
public class ArtworkDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "artwork.db";
    private static ArtworkDBHelper mInstance = null;

    public static ArtworkDBHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new ArtworkDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public ArtworkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + ArtworkContract.Artwork.TABLE_NAME + " (" +
                ArtworkContract.Artwork.COLUMN_ID + " INTEGER PRIMARY KEY," +
                ArtworkContract.Artwork.COLUMN_ART_ID + " INTEGER NOT NULL," +
                ArtworkContract.Artwork.COLUMN_IMAGE_URL + " TEXT NOT NULL," +
                ArtworkContract.Artwork.COLUMN_TITLE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_DATE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_CLASSIFICATION + " TEXT," +
                ArtworkContract.Artwork.COLUMN_CREDIT + " TEXT," +
                ArtworkContract.Artwork.COLUMN_CULTURE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_MEDIUM + " TEXT," +
                ArtworkContract.Artwork.COLUMN_PEOPLE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_TECHNIQUE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_SIZE + " TEXT," +
                ArtworkContract.Artwork.COLUMN_URL + " TEXT," +
                ArtworkContract.Artwork.COLUMN_TIME_PERIOD + " TEXT" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArtworkContract.Artwork.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
