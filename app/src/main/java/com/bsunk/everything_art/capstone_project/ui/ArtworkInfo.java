package com.bsunk.everything_art.capstone_project.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bharat on 8/17/2016.
 */
public class ArtworkInfo implements Parcelable {

    String id;
    String imageURL;

    public ArtworkInfo(String id, String imageUrl) {
        this.id = id;
        this.imageURL = imageUrl;
    }

    private ArtworkInfo(Parcel in) {
        id = in.readString();
        imageURL = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(imageURL);
    }

    public static final Parcelable.Creator<ArtworkInfo> CREATOR = new Parcelable.Creator<ArtworkInfo>() {
        public ArtworkInfo createFromParcel(Parcel in) {
            return new ArtworkInfo(in);
        }

        public ArtworkInfo[] newArray(int size) {
            return new ArtworkInfo[size];
        }
    };

}
