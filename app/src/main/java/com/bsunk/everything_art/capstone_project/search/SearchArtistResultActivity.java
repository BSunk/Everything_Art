package com.bsunk.everything_art.capstone_project.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bsunk.everything_art.capstone_project.ui.MainActivityFragment;

//Activity to display the artwork for the selected artist. Uses the MainActivityFragment for showing the artwork.
public class SearchArtistResultActivity extends AppCompatActivity {

    public static String PERSON_ID_KEY = "person_id_key";
    public static String PERSON_NAME_KEY= "person_name_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bsunk.everything_art.capstone_project.R.layout.activity_search_artist_result);
        Toolbar toolbar = (Toolbar) findViewById(com.bsunk.everything_art.capstone_project.R.id.search_results_toolbar);
        toolbar.setTitle(getIntent().getStringExtra(PERSON_NAME_KEY));
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        toolbar.setNavigationIcon(getResources().getDrawable(com.bsunk.everything_art.capstone_project.R.drawable.back_button_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(savedInstanceState==null) {
            Bundle arguments = new Bundle();
            if (getIntent().getStringExtra(PERSON_ID_KEY) != null) {
                arguments.putString(PERSON_ID_KEY, getIntent().getStringExtra(PERSON_ID_KEY));
                arguments.putString("century", "any");
                arguments.putString("classification", "any");
            }

            MainActivityFragment fragment = new MainActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(com.bsunk.everything_art.capstone_project.R.id.search_result_container, fragment)
                    .commit();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
