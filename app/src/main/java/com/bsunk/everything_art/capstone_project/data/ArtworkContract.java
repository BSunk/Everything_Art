package com.bsunk.everything_art.capstone_project.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Bharat on 8/24/2016.
 */
public class ArtworkContract {

    public static final String CONTENT_AUTHORITY = "com.android.bharat.capstone_project";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES= "favorites";

    public static final class Artwork implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ART_ID = "art_id";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PEOPLE = "people";
        public static final String COLUMN_TIME_PERIOD = "time_period";
        public static final String COLUMN_CULTURE = "culture";
        public static final String COLUMN_CLASSIFICATION= "classification";
        public static final String COLUMN_MEDIUM = "medium";
        public static final String COLUMN_TECHNIQUE = "technique";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_CREDIT = "credit";

        public static Uri buildArtworkUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static int getArtworkIDFromURI(Uri uri) {
        try {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
        catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

}
