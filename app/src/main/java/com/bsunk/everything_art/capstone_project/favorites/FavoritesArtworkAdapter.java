package com.bsunk.everything_art.capstone_project.favorites;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bsunk.everything_art.capstone_project.data.ArtworkContract;
import com.bsunk.everything_art.capstone_project.helper.MySingleton;
import com.bsunk.everything_art.capstone_project.ui.DetailActivity;
import com.bsunk.everything_art.capstone_project.ui.DetailFragment;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Bharat on 8/24/2016.
 */
public class FavoritesArtworkAdapter extends RecyclerView.Adapter<FavoritesArtworkAdapter.ViewHolder> {

    private ImageLoader mImageLoader;

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public NetworkImageView artImageView;
        public IMyViewHolderClicks mListener;

        public ViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);
            artImageView = (NetworkImageView) itemView.findViewById(com.bsunk.everything_art.capstone_project.R.id.artImageView);
            artImageView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.imageLink(view, getAdapterPosition());
        }

        public static interface IMyViewHolderClicks {
            public void imageLink(View image, int pos);
        }
    }

    Cursor cursor;
    Context mContext;

    public FavoritesArtworkAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        mContext = context;
        setHasStableIds(true);
    }

    @Override
    public FavoritesArtworkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View artworkView = inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_main_item, parent, false);
        mImageLoader = MySingleton.getInstance(mContext).getImageLoader();

        //Set the column width depending on the screen width before populating images to provide a smoother experience. Reduces flickering when scrolling.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        artworkView.findViewById(com.bsunk.everything_art.capstone_project.R.id.artImageView).getLayoutParams().width = (size.x)/mContext.getResources().getInteger(com.bsunk.everything_art.capstone_project.R.integer.num_columns);

        FavoritesArtworkAdapter.ViewHolder vh = new ViewHolder(artworkView, new FavoritesArtworkAdapter.ViewHolder.IMyViewHolderClicks() {
            public void imageLink(View artwork, int pos) {
                if (null !=  Long.toString(getItemId(pos))) {
                    Intent i = new Intent(context, DetailActivity.class)
                            .putExtra(DetailFragment.FAV_ID_KEY, Long.toString(getItemId(pos)));
                    context.startActivity(i);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(FavoritesArtworkAdapter.ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);
        NetworkImageView iv = viewHolder.artImageView;
        String imageURL = (cursor.getString(cursor.getColumnIndex(ArtworkContract.Artwork.COLUMN_IMAGE_URL)));
        String modImageURL = imageURL.replace("?height=500&width=500", "?height=150&width=150");
        iv.setImageUrl(modImageURL, mImageLoader);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                String id = cursor.getString(cursor.getColumnIndex(ArtworkContract.Artwork.COLUMN_ART_ID));
                return Long.parseLong(id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

}

