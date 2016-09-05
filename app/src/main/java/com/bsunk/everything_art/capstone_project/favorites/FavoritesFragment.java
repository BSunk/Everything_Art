package com.bsunk.everything_art.capstone_project.favorites;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsunk.everything_art.capstone_project.data.ArtworkContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bharat on 8/24/2016.
 */
//Fragment that shows the saved artwork using Loaders and retrieving the information from the content provider.
public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final int LOADER = 0;
    int columnCount;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.artwork_images) RecyclerView artView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.no_favorites_error) TextView noFavTextView;
    StaggeredGridLayoutManager sglm;
    FavoritesArtworkAdapter adapter;

    public FavoritesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);
        columnCount = getResources().getInteger(com.bsunk.everything_art.capstone_project.R.integer.num_columns); //For the staggeredgridlayout
        sglm = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        artView.setLayoutManager(sglm);
        getLoaderManager().initLoader(LOADER, null, this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getLoaderManager().getLoader(LOADER) == null) {
            getLoaderManager().initLoader(LOADER, null, this);
        } else {
            getLoaderManager().restartLoader(LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(),
                ArtworkContract.Artwork.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        if(cursor.getCount()==0) { noFavTextView.setVisibility(View.VISIBLE); }
        else { noFavTextView.setVisibility(View.INVISIBLE);}
        adapter = new FavoritesArtworkAdapter(getContext(), cursor);
        artView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

}
