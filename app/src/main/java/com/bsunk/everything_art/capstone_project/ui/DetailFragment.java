package com.bsunk.everything_art.capstone_project.ui;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsunk.everything_art.capstone_project.BuildConfig;
import com.bsunk.everything_art.capstone_project.data.ArtworkContract;
import com.bsunk.everything_art.capstone_project.helper.MySingleton;
import com.bsunk.everything_art.capstone_project.widget.ArtworkAppWidgetProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
//Implements the details screen for the artwork. Also implements a ViewPager for the artwork in case there is more than one image.
//Viewpager uses PhotosFragment class to show the image. Uses Volley for the network request. If the image is a favorite then it will use
//the content provider to load the information for the detail screen.

public class DetailFragment extends Fragment implements View.OnClickListener {

    public static String ID_KEY = "id";
    public static String FAV_ID_KEY = "favid";
    public static final String LOG_TAG = MainActivityFragment.class.getName();
    RequestQueue queue;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.detailTitle) TextView title;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.detailDate) TextView date;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.time_period) TextView timePeriod;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.medium) TextView detailMedium;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.size) TextView detailSize;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.credit) TextView credit;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.technique) TextView detailTechnique;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.classification) TextView detailClassification;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.culture) TextView detailCulture;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.people) TextView peopleDetails;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.description) TextView descriptionTextView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.description_header) TextView descriptionHeaderTextView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.photo_viewpager) ViewPager photoViewPager;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.indicator) CircleIndicator indicator;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.up_arrow) ImageView upArrow;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.more_info_button) Button moreInfoButton;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.favorite_fab) FloatingActionButton favFabButton;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.wallpaper_button) ImageButton wallpaperButton;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.share_button) ImageButton shareButton;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.main_content) CoordinatorLayout coordinatorLayout;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.details_scroll_view) NestedScrollView detailNestedScrollView;
    PagerAdapter pagerAdapter;
    ArrayList<String> imagesURLs = new ArrayList<>();
    private String url;
    ContentValues addFavoriteRecord;
    boolean artworkInDB;
    String fullImageURL;
    String id;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        pagerAdapter = new PagerAdapter(getFragmentManager(), getContext());
        Bundle arguments = getArguments();
        if (arguments != null) {
            if(arguments.getString(FAV_ID_KEY)!=null)  {
                id = arguments.getString(FAV_ID_KEY);
                queue = MySingleton.getInstance(getContext()).getRequestQueue();
                setFavFabButton(id);
                setArtworkInfoFromDB(id);
            }
            if (arguments.getString(ID_KEY)!=null) {
                id = arguments.getString(ID_KEY);
                queue = MySingleton.getInstance(getContext()).getRequestQueue();
                setFavFabButton(id);
                getArtworkInfo(id);
            }
        }
        final Animation fadeInRepeat = AnimationUtils.loadAnimation(getActivity().getBaseContext(), com.bsunk.everything_art.capstone_project.R.anim.fade_in_fade_out);
        upArrow.startAnimation(fadeInRepeat);
        moreInfoButton.setOnClickListener(this);
        favFabButton.setOnClickListener(this);
        wallpaperButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        return rootView;
    }
    //This function does all the work. It gets the information from the API from Volley and then parses through it and sets the details screen text.
    public void getArtworkInfo(final String id) {
        String baseURL = "http://api.harvardartmuseums.org/object";
        String url;
        Uri builtURI;
        builtURI = Uri.parse(baseURL).buildUpon()
                .appendPath(id)
                .appendQueryParameter("apikey",  BuildConfig.ARTDB_API_KEY)
                .build();
        url = builtURI.toString();
        JsonObjectRequest customJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response!=null) {
                    try {
                        String medium = response.getString("medium");
                        String technique = response.getString("technique");
                        String size = response.getString("dimensions");
                        String dated = response.getString("dated");
                        String url = response.getString("url");
                        setURL(url);
                        String detailTitle = response.getString("title");
                        String culture = response.getString("culture");
                        String period = response.getString("period");
                        String classification = response.getString("classification");
                        String creditLine = response.getString("creditline");

                        title.setText(detailTitle);
                        date.setText(dated);
                        detailClassification.setText(classification);
                        credit.setText(creditLine);
                        if (period.equals("null")) {
                            timePeriod.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
                        }
                        else {
                            timePeriod.setText(period);
                        }
                        if (culture.equals("null")) {
                            detailCulture.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
                        }
                        else {
                            detailCulture.setText(culture);
                        }
                        if (medium.equals("null")) {
                            detailMedium.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
                        }
                        else {
                            detailMedium.setText(medium);
                        }
                        if (technique.equals("null")) {
                            detailTechnique.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
                        }
                        else {
                            detailTechnique.setText(technique);
                        }
                        if (size.equals("null")) {
                            detailSize.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
                        }
                        else {
                            detailSize.setText(size);
                        }

                        if (!(response.getString("contextualtextcount").equals("0"))) {
                            JSONArray contextualtext = response.getJSONArray("contextualtext");
                            JSONObject text = contextualtext.getJSONObject(0);
                            String contextualText = text.getString("text");
                            descriptionTextView.setText(contextualText);
                            descriptionTextView.setVisibility(View.VISIBLE);
                            descriptionHeaderTextView.setVisibility(View.VISIBLE);
                        }

                        //grab the image urls and set it to the viewpager
                        imagesURLs.clear();
                        JSONArray images = response.getJSONArray("images");
                        fullImageURL = images.getJSONObject(0).getString("baseimageurl");
                        for(int i=0; i<images.length(); i++) {
                            JSONObject image = images.getJSONObject(i);
                            String imageURL = image.getString("baseimageurl")+ "?height=500&width=500";
                            imagesURLs.add(imageURL);
                        }
                        if (!imagesURLs.isEmpty()) {
                            photoViewPager.setAdapter(pagerAdapter);
                            indicator.setViewPager(photoViewPager);
                        }

                        //get the list of people in the array
                        String allPeople = "";
                        try {
                            JSONArray people = response.getJSONArray("people");
                            for(int i=0; i<images.length(); i++) {
                                JSONObject person = people.getJSONObject(i);
                                String name = person.getString("name");
                                String personCulture = person.getString("culture");
                                if (!personCulture.equals("null")) {
                                    allPeople = allPeople + name + ", " + personCulture + " ";
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (allPeople.isEmpty()) {
                            allPeople = getString(com.bsunk.everything_art.capstone_project.R.string.unavailable);
                        }
                        peopleDetails.setText(allPeople);

                        //Values to add to the favorites database.
                        addFavoriteRecord = new ContentValues();
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_ART_ID, id);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_IMAGE_URL, imagesURLs.get(0));
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TITLE, detailTitle);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_DATE, dated);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CLASSIFICATION, classification);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CREDIT, creditLine);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CULTURE, culture);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_MEDIUM, medium);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_PEOPLE, allPeople);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TECHNIQUE, technique);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_SIZE, size);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_URL, url);
                        addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TIME_PERIOD, period);
                        setBackgroundColor();
                        photoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                setBackgroundColor();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error retrieving artwork details.");
            }
        });
        queue.add(customJsonObjectRequest);
    }

    //This will set the artwork from the database if the artwork is a favorite.
    public void setArtworkInfoFromDB(String id) {

        Uri CONTENT_URI = ArtworkContract.Artwork.CONTENT_URI.buildUpon().appendPath(id).build();
        Cursor c = getContext().getContentResolver()
                .query(CONTENT_URI, null, null, null, null);

        if (c!=null && c.moveToFirst()) {
            imagesURLs.clear();
            imagesURLs.add(c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_IMAGE_URL)));
            photoViewPager.setAdapter(pagerAdapter);
            indicator.setViewPager(photoViewPager);

            String medium = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_MEDIUM));
            String technique = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_TECHNIQUE));
            String size = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_SIZE));
            String dated = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_DATE));
            url = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_URL));
            String detailTitle = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_TITLE));
            String culture = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_CULTURE));
            String period = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_TIME_PERIOD));
            String classification = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_CLASSIFICATION));
            String creditLine = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_CREDIT));
            String allPeople = c.getString(c.getColumnIndex(ArtworkContract.Artwork.COLUMN_PEOPLE));

            title.setText(detailTitle);
            date.setText(dated);
            detailClassification.setText(classification);
            credit.setText(creditLine);
            if (period.equals("null")) {
                timePeriod.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
            }
            else {
                timePeriod.setText(period);
            }
            if (culture.equals("null")) {
                detailCulture.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
            }
            else {
                detailCulture.setText(culture);
            }
            if (medium.equals("null")) {
                detailMedium.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
            }
            else {
                detailMedium.setText(medium);
            }
            if (technique.equals("null")) {
                detailTechnique.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
            }
            else {
                detailTechnique.setText(technique);
            }
            if (size.equals("null")) {
                detailSize.setText(getString(com.bsunk.everything_art.capstone_project.R.string.unavailable));
            }
            else {
                detailSize.setText(size);
            }
            if (allPeople.isEmpty()) {
                allPeople = getString(com.bsunk.everything_art.capstone_project.R.string.unavailable);
            }
            peopleDetails.setText(allPeople);

            //Values to add to the favorites database.
            addFavoriteRecord = new ContentValues();
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_ART_ID, id);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_IMAGE_URL, imagesURLs.get(0));
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TITLE, detailTitle);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_DATE, dated);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CLASSIFICATION, classification);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CREDIT, creditLine);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_CULTURE, culture);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_MEDIUM, medium);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_PEOPLE, allPeople);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TECHNIQUE, technique);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_SIZE, size);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_URL, url);
            addFavoriteRecord.put(ArtworkContract.Artwork.COLUMN_TIME_PERIOD, period);
            fullImageURL = imagesURLs.get(0);

            setBackgroundColor();
            photoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setBackgroundColor();
                }
            });
        }
    }
    //Sees if the artwork is already in the DB, which means it was already set as a favorite. If true than it will change the favorite button to reflect that.
    public void setFavFabButton(String id) {
        Cursor c = getContext().getContentResolver()
                .query(ArtworkContract.Artwork.CONTENT_URI, null, ArtworkContract.Artwork.COLUMN_ART_ID + " = " + DatabaseUtils.sqlEscapeString(id), null, null);
        if (!(c.getCount() ==0)) {
            favFabButton.setImageResource(com.bsunk.everything_art.capstone_project.R.drawable.ic_favorite_black_24dp);
            artworkInDB = true;
            c.close();
        }
        else {
            artworkInDB = false;
        }
    }

    //onClick handlers for all the buttons.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.bsunk.everything_art.capstone_project.R.id.more_info_button:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case com.bsunk.everything_art.capstone_project.R.id.favorite_fab:
                if(!artworkInDB) {
                    Uri returnUri = getContext().getContentResolver().insert(ArtworkContract.Artwork.CONTENT_URI, addFavoriteRecord);
                    if (returnUri != null) {
                        Snackbar.make(coordinatorLayout, getResources().getString(com.bsunk.everything_art.capstone_project.R.string.addedFav), Snackbar.LENGTH_SHORT).show();
                        favFabButton.setImageResource(com.bsunk.everything_art.capstone_project.R.drawable.ic_favorite_black_24dp);
                        artworkInDB=true;
                    }
                }
                else {
                    int deletedRows = getContext().getContentResolver().delete(ArtworkContract.Artwork.CONTENT_URI, ArtworkContract.Artwork.COLUMN_ART_ID + "=?", new String[]{id});
                    if (deletedRows != 0) {
                        Snackbar.make(coordinatorLayout, getResources().getString(com.bsunk.everything_art.capstone_project.R.string.removeFav), Snackbar.LENGTH_SHORT).show();
                        favFabButton.setImageResource(com.bsunk.everything_art.capstone_project.R.drawable.ic_favorite_border_black_24dp);
                        artworkInDB=false;
                     }
                }
                //Tell widget that content has changed.
                Intent dataUpdatedIntent = new Intent(ArtworkAppWidgetProvider.ACTION_DATA_UPDATED)
                        .setPackage(getContext().getPackageName());
                getContext().sendBroadcast(dataUpdatedIntent);
                break;
            case com.bsunk.everything_art.capstone_project.R.id.share_button:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = url;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(com.bsunk.everything_art.capstone_project.R.string.share_intent_subject));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(com.bsunk.everything_art.capstone_project.R.string.share_des)));
                break;
            case com.bsunk.everything_art.capstone_project.R.id.wallpaper_button:
                new AlertDialog.Builder(getContext())
                        .setMessage(getString(com.bsunk.everything_art.capstone_project.R.string.wallpaper_des))
                        .setPositiveButton(com.bsunk.everything_art.capstone_project.R.string.wallpaper_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                setWallpaper(fullImageURL);
                            }
                        })
                        .setNegativeButton(com.bsunk.everything_art.capstone_project.R.string.wallpaper_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
    }

    //sets the color of the background of the details page depending on the image.
    public void setBackgroundColor() {
        Fragment fragment = pagerAdapter.mCurrentFragment;
        if (fragment!=null) {
            detailNestedScrollView.setBackground(fragment.getView().findViewById(com.bsunk.everything_art.capstone_project.R.id.photo_container).getBackground());
        }
    }

    public void setWallpaper(String url) {
        final ProgressDialog wallpaperProgressDialog = new ProgressDialog(getContext());
        wallpaperProgressDialog.setMessage(getString(com.bsunk.everything_art.capstone_project.R.string.wallpaper_progress_dialog));
        wallpaperProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wallpaperProgressDialog.setIndeterminate(true);
        wallpaperProgressDialog.show();
        MySingleton.getInstance(getActivity()).getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Bitmap bitmap = imageContainer.getBitmap();
                if (bitmap != null) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                    try {
                        wallpaperManager.setBitmap(bitmap);
                        wallpaperProgressDialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, "Error settings wallpaper");
                        wallpaperProgressDialog.dismiss();
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e(LOG_TAG, "Error settings wallpaper");
                wallpaperProgressDialog.dismiss();
            }
        });
    }

    public void setURL(String urlIn) {
        url = urlIn;
    }

    //Pager Adapter for ViewPager to swipe between the available images for a certain artwork.
    class PagerAdapter extends FragmentPagerAdapter {
        Context context;
        private Fragment mCurrentFragment;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }
        @Override
        public int getCount() {
            return imagesURLs.size();
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new PhotoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PhotoFragment.URL_KEY, imagesURLs.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (Fragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }
    }
}

