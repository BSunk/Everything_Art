package com.bsunk.everything_art.capstone_project.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bsunk.everything_art.capstone_project.helper.MySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Bharat on 8/17/2016.
 */
public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ViewHolder> {

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
            mListener.imageLink(view, getPosition());
        }

        public static interface IMyViewHolderClicks {
            public void imageLink(View image, int pos);
        }
    }

    private List<ArtworkInfo> mArtwork;
    private Context mContext;

    public ArtworkAdapter(Context context, List<ArtworkInfo> artwork) {
        mArtwork = artwork;
        mContext = context;
    }

    @Override
    public ArtworkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        ArtworkAdapter.ViewHolder vh = new ViewHolder(artworkView, new ArtworkAdapter.ViewHolder.IMyViewHolderClicks() {
            public void imageLink(View artwork, int pos) {
                String id = mArtwork.get(pos).id;
                if (null != id) {
                    Intent i = new Intent(context, DetailActivity.class)
                            .putExtra(DetailFragment.ID_KEY, id);
                    context.startActivity(i);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ArtworkAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ArtworkInfo artworkInfo = mArtwork.get(position);
        NetworkImageView iv = viewHolder.artImageView;
        iv.setImageUrl(artworkInfo.imageURL, mImageLoader);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArtwork.size();
    }

}
