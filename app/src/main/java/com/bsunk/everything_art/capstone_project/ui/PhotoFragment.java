package com.bsunk.everything_art.capstone_project.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsunk.everything_art.capstone_project.helper.MySingleton;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
//Fragment that displays an image when an artwork is selected. This is the fragment the viewpager uses to display images.
public class PhotoFragment extends Fragment {

    public static String URL_KEY = "url";
    private static String BITMAP_KEY = "bitmap";
    private int mMutedColor = 0xFF333333;
    private int mVibrantDark = 0xFF333333;
    public static final String LOG_TAG = PhotoFragment.class.getName();
    @BindView(com.bsunk.everything_art.capstone_project.R.id.loadingPicture) ProgressBar loadingPicture;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.photo_container) FrameLayout photoContainer;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.no_pic) TextView noPicTextView;
    @BindView(com.bsunk.everything_art.capstone_project.R.id.photo) ImageView photo;
    Animation fadeInAnimation;
    String url;
    Bitmap bitmap;

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.bsunk.everything_art.capstone_project.R.layout.fragment_photo, container, false);
        fadeInAnimation = AnimationUtils.loadAnimation(getActivity().getBaseContext(), com.bsunk.everything_art.capstone_project.R.anim.fadein);
        ButterKnife.bind(this, rootView);
        Bundle bundle = this.getArguments();
        if (savedInstanceState != null) {
            bitmap = savedInstanceState.getParcelable(BITMAP_KEY);
            if (bitmap != null) {
                setImageFromBundle(bitmap);
            }
            else {
                if (bundle != null) {
                    url = bundle.getString(URL_KEY);
                    setImageFromURL(url);
                }
            }
        }
        else {
            if (bundle != null) {
                url = bundle.getString(URL_KEY);
                setImageFromURL(url);
            }
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BITMAP_KEY, bitmap);

    }

    public void setImageFromURL(String url) {
        MySingleton.getInstance(getActivity()).getImageLoader().get(url, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                bitmap = imageContainer.getBitmap();
                if (bitmap != null) {
                    noPicTextView.setVisibility(View.INVISIBLE);
                    Palette p = Palette.generate(bitmap, 12);
                    mMutedColor = p.getDarkMutedColor(0xFF333333);
                    mVibrantDark = p.getDarkVibrantColor(0xFF333333);
                    loadingPicture.setVisibility(View.INVISIBLE);
                    photo.setImageBitmap(imageContainer.getBitmap());
                    if(getActivity()!=null) {
                        photo.startAnimation(fadeInAnimation);
                    }
                    photoContainer.setBackgroundColor(mMutedColor);
                    photoContainer.startAnimation(fadeInAnimation);

                    if(getActivity()!=null) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(mVibrantDark);
                        window.setNavigationBarColor(mVibrantDark);
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e(LOG_TAG, "Error retrieving image.");
                noPicTextView.setVisibility(View.VISIBLE);
                loadingPicture.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setImageFromBundle(Bitmap bitmap) {
        if (bitmap != null) {
            noPicTextView.setVisibility(View.INVISIBLE);
            Palette p = Palette.generate(bitmap, 12);
            mMutedColor = p.getDarkMutedColor(0xFF333333);
            mVibrantDark = p.getDarkVibrantColor(0xFF333333);
            loadingPicture.setVisibility(View.INVISIBLE);
            photo.setImageBitmap(bitmap);
            if(getActivity()!=null) {
                photo.startAnimation(fadeInAnimation);
            }
            photoContainer.setBackgroundColor(mMutedColor);
            photoContainer.startAnimation(fadeInAnimation);

            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mVibrantDark);
            window.setNavigationBarColor(mVibrantDark);
        }

    }

}
