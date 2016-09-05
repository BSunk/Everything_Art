package com.bsunk.everything_art.capstone_project.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bsunk.everything_art.capstone_project.BuildConfig;
import com.bsunk.everything_art.capstone_project.helper.EndlessRecyclerViewScrollListener;
import com.bsunk.everything_art.capstone_project.helper.MySingleton;
import com.bsunk.everything_art.capstone_project.search.SearchArtistResultActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

//Fragment that fetches the image urls using Volley and sends the information to ArtworkAdapter.
// It also sets up a StaggeredGridlayout to show grid of images.
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getName();
    private static final String ARRAY_KEY = "array_key";
    private static final String CLASSIFICATION_KEY = "class_key";
    private static final String PAGE_KEY = "page_key";
    RequestQueue queue;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.artwork_images) RecyclerView artView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.emptyView) TextView emptyView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.noNetworkError) TextView noNetwork;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.main_fragment_container) FrameLayout mainContainer;
    ArrayList<ArtworkInfo> artArray = new ArrayList<>();
    ArtworkAdapter adapter;
    int columnCount;
    String century = "any";
    String classification = "any";
    String personID = null;
    int pageNumber;
    StaggeredGridLayoutManager sglm;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);
        queue = MySingleton.getInstance(getContext()).getRequestQueue();
        columnCount = getResources().getInteger(com.bsunk.everything_art.capstone_project.R.integer.num_columns); //For the staggeredgridlayout. Number of columns depends on screen size.

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            century = bundle.getString("century");
            classification = bundle.getString("classification");
            personID = bundle.getString(SearchArtistResultActivity.PERSON_ID_KEY);
        }
        if(savedInstanceState == null || !savedInstanceState.containsKey(ARRAY_KEY)) {
            getArtwork(1, false, century, classification, personID);
        }
        else {
            artArray = savedInstanceState.getParcelableArrayList(ARRAY_KEY);
            String classificationSaved = savedInstanceState.getString(CLASSIFICATION_KEY);
            String personIDSaved = savedInstanceState.getString(SearchArtistResultActivity.PERSON_ID_KEY);
            if(classificationSaved!=null && classificationSaved.equals(classification)| personIDSaved!=null) {
                pageNumber = savedInstanceState.getInt(PAGE_KEY);
                getArtwork(pageNumber, true, century, classification, personID);
            }
            else {
                artArray.clear();
                getArtwork(1, false, century, classification, personID);
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(ARRAY_KEY, artArray);
        savedInstanceState.putString(CLASSIFICATION_KEY, classification);
        savedInstanceState.putInt(PAGE_KEY, pageNumber);
        super.onSaveInstanceState(savedInstanceState);
    }


    //Uses Volley to retrieve artwork from the Harvard Museum Json API. The id of the artwork and the image url is stored in an array list in an ArtworkInfo object.
    // The recyclerview is attached to the adapter here. This will also retrieve subsequent artwork triggered by the EndlessScrolling library using the nextPage boolean set to true.
    private void getArtwork(final int page, final boolean nextPage, final String century, final String classification, final String personID) {

        String baseURL = "http://api.harvardartmuseums.org/object";
        String url = "";
        Uri builtURI;

        if(personID!=null) {
            builtURI = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter("person", personID)
                    .appendQueryParameter("apikey", BuildConfig.ARTDB_API_KEY)
                    .appendQueryParameter("page", Integer.toString(page))
                    .appendQueryParameter("size", "8")
                    .build();
            url = builtURI.toString() + "&q=imagepermissionlevel:0";
        }

        else if(century.equals("any")) {
            builtURI = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter("apikey", BuildConfig.ARTDB_API_KEY)
                    .appendQueryParameter("century", century)
                    .appendQueryParameter("hasimage", "1")
                    .appendQueryParameter("sort", "random")
                    .appendQueryParameter("classification", classification)
                    .appendQueryParameter("page", Integer.toString(page))
                    .appendQueryParameter("size", "8")
                    .build();
            url = builtURI.toString();
        }

        else if (!(century.equals("any"))) {
            builtURI = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter("century", century)
                    .appendQueryParameter("hasimage", "1")
                    .appendQueryParameter("sort", "random")
                    .appendQueryParameter("classification", classification)
                    .appendQueryParameter("apikey", BuildConfig.ARTDB_API_KEY)
                    .appendQueryParameter("page", Integer.toString(page))
                    .appendQueryParameter("size", "8")
                    .build();
            url = builtURI.toString();
        }

        JsonObjectRequest customJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                sglm  = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

                if (response!=null) {
                    try {
                        noNetwork.setVisibility(View.INVISIBLE);
                        //If it is not next page then it will clear the array rather than append data.
                        if(!nextPage) {
                            artArray.clear();
                        }
                        //checks to see if there are any records. If there is not it will tell the user by making the textView visible.
                        JSONObject info = response.getJSONObject("info");
                        if(info.getString("totalrecords").equals("0")) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                        if((Integer.parseInt(info.getString("page"))<=Integer.parseInt(info.getString("pages")))) {
                            JSONArray records = response.getJSONArray("records");
                            for (int i = 0; i < records.length(); i++) {
                                JSONObject artwork = records.getJSONObject(i);
                                String id = artwork.getString("objectid");
                                try {
                                    String thumbnailURL = artwork.getString("primaryimageurl");
                                    if (!thumbnailURL.equals("null")) {
                                        thumbnailURL = thumbnailURL + "?height=150&width=150";
                                        artArray.add(new ArtworkInfo(id, thumbnailURL));
                                    }
                                } catch (JSONException e) {

                                }
                            }
                            //sets the recyclerview to the adapter.
                            if (!nextPage) {
                                adapter = new ArtworkAdapter(getContext(), artArray);
                                //sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                                artView.setAdapter(adapter);
                                artView.setLayoutManager(sglm);
                            }
                            else {
                                if (adapter == null) {
                                    adapter = new ArtworkAdapter(getContext(), artArray);artView.setAdapter(adapter);
                                    artView.setItemViewCacheSize(20);
                                    artView.setLayoutManager(sglm);
                                    sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                                    sglm.scrollToPosition(0);
                                }
                                int curSize = adapter.getItemCount();
                                adapter.notifyItemRangeInserted(curSize, artArray.size() - 1);
                            }
                        }

                        //Sets a scroll listener to notify if more data needs to be loaded.
                        artView.addOnScrollListener(new EndlessRecyclerViewScrollListener(sglm) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount) {
                                pageNumber = page+1;
                                if (personID == null) {
                                    getArtwork(page+1, true, century, classification, null);
                                } else {
                                    getArtwork(page+1, true, century, classification, personID);
                                }
                            }
                        });

                    } catch (JSONException e) {
                        emptyView.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(LOG_TAG, "Error retrieving artwork images.");
                noNetwork.setVisibility(View.VISIBLE);
            }
        });
        queue.add(customJsonObjectRequest);
    }

}

