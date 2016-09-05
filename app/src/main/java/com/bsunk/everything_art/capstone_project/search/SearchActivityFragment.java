package com.bsunk.everything_art.capstone_project.search;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsunk.everything_art.capstone_project.BuildConfig;
import com.bsunk.everything_art.capstone_project.helper.MySingleton;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    public static String QUERY_KEY = "query";
    RequestQueue queue;
    public static final String LOG_TAG = SearchActivityFragment.class.getName();
    ArtistAdapter adapter;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.search_results) RecyclerView searchRecyclerView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.no_search_results) TextView noResultsTextView;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = this.getArguments();
        if (arguments!=null) {
            String query = arguments.getString(QUERY_KEY);
            Log.v(LOG_TAG, query);
            queue = MySingleton.getInstance(getContext()).getRequestQueue();
            getQueryResults(query);
        }
        return rootView;
    }

    public void getQueryResults(String query) {
        String baseURL = "http://api.harvardartmuseums.org/person?q=displayname:" + query;
        String url;
        Uri builtURI;
        builtURI = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter("apikey",  BuildConfig.ARTDB_API_KEY)
                .appendQueryParameter("size", "30")
                .build();
        url = builtURI.toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject infos = response.getJSONObject("info");
                            if(infos.getString("totalrecords").equals("0")) {
                                noResultsTextView.setVisibility(View.VISIBLE);
                            }

                            JSONArray records = response.getJSONArray("records");
                            ArrayList<ArtistInfo> artistInfos = new ArrayList<>();
                            for(int i= 0; i<records.length(); i++) {
                                String personid = records.getJSONObject(i).getString("personid");
                                String displayDate = records.getJSONObject(i).getString("displaydate");
                                String culture = records.getJSONObject(i).getString("culture");
                                String name = records.getJSONObject(i).getString("displayname");
                                artistInfos.add(new ArtistInfo(personid, displayDate, culture, name));
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                            adapter = new ArtistAdapter(getContext(),artistInfos);
                            searchRecyclerView.setAdapter(adapter);
                            searchRecyclerView.setLayoutManager(linearLayoutManager);

                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(searchRecyclerView, "Cannot fetch artwork. Please check network connection", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(com.bsunk.everything_art.capstone_project.R.color.text_color_white)).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(LOG_TAG, "Error retrieving search results");
                    }
                });
        queue.add(jsObjRequest);
    }
}





