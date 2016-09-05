package com.bsunk.everything_art.capstone_project.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bsunk.everything_art.capstone_project.R.layout.activity_search);

        toolbar = (Toolbar) findViewById(com.bsunk.everything_art.capstone_project.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState==null) {
            String query = handleIntent(getIntent());
            if (query!=null) {
                Bundle arguments = new Bundle();
                arguments.putString(SearchActivityFragment.QUERY_KEY, query);
                SearchActivityFragment fragment = new SearchActivityFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(com.bsunk.everything_art.capstone_project.R.id.search_fragment_container, fragment)
                        .commit();
            }
        }
    }

    private String handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            String title = String.format(getResources().getString(com.bsunk.everything_art.capstone_project.R.string.search_title_bar), query);
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
            return query;
        }
        return null;
    }

}
