package com.bsunk.everything_art.capstone_project.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bsunk.everything_art.capstone_project.favorites.FavoritesFragment;
import com.bsunk.everything_art.capstone_project.search.SearchActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.reflect.InvocationTargetException;

//Contains the tablayout and the viewpager to swipe between centuries. Also implements the banner advertisement.

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    String mSearchString = "";
    SearchView searchView;
    private static String SEARCH_KEY = "search_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bsunk.everything_art.capstone_project.R.layout.activity_main);

        //Initialize firebase ads and google analytics.
        MobileAds.initialize(getApplicationContext(), getString(com.bsunk.everything_art.capstone_project.R.string.app_id));

        //Initialize spinner for selecting artwork medium.
        Spinner spinner = (Spinner) findViewById(com.bsunk.everything_art.capstone_project.R.id.classification_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                com.bsunk.everything_art.capstone_project.R.array.classification_array, com.bsunk.everything_art.capstone_project.R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(com.bsunk.everything_art.capstone_project.R.id.viewpager);
        PagerAdapter pagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(com.bsunk.everything_art.capstone_project.R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar toolbar = (Toolbar) findViewById(com.bsunk.everything_art.capstone_project.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_KEY);
        }

        AdView mAdView = (AdView) findViewById(com.bsunk.everything_art.capstone_project.R.id.adView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAdView.getLayoutParams();
        int navBarHeight = getNavigationBarSize(getApplicationContext()).y;
        if (navBarHeight<200) {
            params.setMargins(0, 0, 0, navBarHeight);
        }

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("765407EBA000F31C68FB6DAB6AB600F9")
                .build();
        mAdView.loadAd(adRequest);

    }

    //Next three functions are used to find the navigation bar height to place the banner ad in thee right spot
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }
        return size;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.bsunk.everything_art.capstone_project.R.menu.main, menu);
        //initialize the search function.
        MenuItem searchItem = menu.findItem(com.bsunk.everything_art.capstone_project.R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getBaseContext(), SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        //In case of OnSaveInstance, restore the text field of the search box.
        if (mSearchString != null && !mSearchString.isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(mSearchString, false);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saves the search box text if any.
        if (searchView != null) {
            mSearchString = searchView.getQuery().toString();
            outState.putString(SEARCH_KEY, mSearchString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case com.bsunk.everything_art.capstone_project.R.id.action_about:
                AboutDialog about = new AboutDialog();
                about.show(getFragmentManager(), "aboutdialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // A method to find height of the status bar.
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //Refreshes the list in the viewpager if spinner selection is changed.
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) viewPager.getAdapter();
        a.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        Context context;
        String[] tabTitles = {getString(com.bsunk.everything_art.capstone_project.R.string.tab_all), getString(com.bsunk.everything_art.capstone_project.R.string.tab_favorites), getString(com.bsunk.everything_art.capstone_project.R.string.tab_21th), getString(com.bsunk.everything_art.capstone_project.R.string.tab_20th),
                getString(com.bsunk.everything_art.capstone_project.R.string.tab_19th), getString(com.bsunk.everything_art.capstone_project.R.string.tab_18th), getString(com.bsunk.everything_art.capstone_project.R.string.tab_17th), getString(com.bsunk.everything_art.capstone_project.R.string.tab_16th), getString(com.bsunk.everything_art.capstone_project.R.string.tab_15th)};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MainActivityFragment();
            Bundle bundle = new Bundle();
            Spinner spinner = (Spinner) findViewById(com.bsunk.everything_art.capstone_project.R.id.classification_spinner);
            if (spinner.getSelectedItem().toString().equals("All media")) {
                bundle.putString("classification", "any");
            } else {
                bundle.putString("classification", spinner.getSelectedItem().toString());
            }
            switch (position) {
                case 0:
                    bundle.putString("century", "any");
                    fragment.setArguments(bundle);
                    return fragment;
                case 1:
                    return new FavoritesFragment();
                case 2:
                    bundle.putString("century", "21st century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 3:
                    bundle.putString("century", "20th century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 4:
                    bundle.putString("century", "19th century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 5:
                    bundle.putString("century", "18th century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 6:
                    bundle.putString("century", "17th century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 7:
                    bundle.putString("century", "16th century");
                    fragment.setArguments(bundle);
                    return fragment;
                case 8:
                    bundle.putString("century", "15th century");
                    fragment.setArguments(bundle);
                    return fragment;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
