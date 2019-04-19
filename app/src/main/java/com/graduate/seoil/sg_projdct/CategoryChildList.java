package com.graduate.seoil.sg_projdct;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by baejanghun on 17/04/2019.
 */
public class CategoryChildList implements Parcelable {
    private String title;

    public CategoryChildList(String title) {
        this.title = title;
    }

    protected CategoryChildList(Parcel in) {
        title = in.readString();
    }

    public static final Creator<CategoryChildList> CREATOR = new Creator<CategoryChildList>() {
        @Override
        public CategoryChildList createFromParcel(Parcel in) {
            return new CategoryChildList(in);
        }

        @Override
        public CategoryChildList[] newArray(int size) {
            return new CategoryChildList[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
