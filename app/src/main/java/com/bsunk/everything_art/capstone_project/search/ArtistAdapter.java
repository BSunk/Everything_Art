package com.bsunk.everything_art.capstone_project.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bharat on 8/25/2016.
 */
public class ArtistAdapter extends
        RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(com.bsunk.everything_art.capstone_project.R.id.search_name) TextView nameTextView;
        @BindView(com.bsunk.everything_art.capstone_project.R.id.search_date) TextView dateTextView;
        @BindView(com.bsunk.everything_art.capstone_project.R.id.search_item) LinearLayout searchItem;
        @BindView(com.bsunk.everything_art.capstone_project.R.id.search_culture) TextView cultureTextView;
        public IMyViewHolderClicks mListener;

        public ViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            searchItem.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.detailLink(view, getPosition());
        }

        public static interface IMyViewHolderClicks {
            public void detailLink(View video, int pos);
        }
    }

    private List<ArtistInfo> mArtists;
    private Context mContext;

    public ArtistAdapter(Context context, List<ArtistInfo> artists) {
        mArtists = artists;
        mContext = context;
    }

    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.bsunk.everything_art.capstone_project.R.layout.search_item, parent, false);

        ArtistAdapter.ViewHolder vh = new ViewHolder(v, new ArtistAdapter.ViewHolder.IMyViewHolderClicks() {
            public void detailLink(View artist, int pos) {
                String personid = mArtists.get(pos).personID;
                if (null != personid) {
                    Intent i = new Intent(mContext, SearchArtistResultActivity.class)
                            .putExtra(SearchArtistResultActivity.PERSON_ID_KEY, personid)
                            .putExtra(SearchArtistResultActivity.PERSON_NAME_KEY, mArtists.get(pos).displayName);
                    mContext.startActivity(i);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ArtistInfo artist = mArtists.get(position);
        // Set item views based on your views and data model
        viewHolder.nameTextView.setText(artist.displayName);
        if (!artist.displayDate.equals("null")) {
            viewHolder.dateTextView.setText(artist.displayDate);
        }
        if (!artist.culture.equals("null")) {
            viewHolder.cultureTextView.setText(artist.culture);
        }
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }
}
