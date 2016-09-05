package com.bsunk.everything_art.capstone_project.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bsunk.everything_art.capstone_project.R.layout.activity_detail);
        if(savedInstanceState==null) {
            Bundle arguments = new Bundle();
            if (getIntent().getStringExtra(DetailFragment.ID_KEY) != null) {
                arguments.putString(DetailFragment.ID_KEY, getIntent().getStringExtra(DetailFragment.ID_KEY));
            }
            else if (getIntent().getStringExtra(DetailFragment.FAV_ID_KEY)!= null) {
                arguments.putString(DetailFragment.FAV_ID_KEY, getIntent().getStringExtra(DetailFragment.FAV_ID_KEY));
            }

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(com.bsunk.everything_art.capstone_project.R.id.detail_container, fragment)
                    .commit();
        }
    }
    public void backButtonOnClick(View view) {
        onBackPressed();
    }

}
