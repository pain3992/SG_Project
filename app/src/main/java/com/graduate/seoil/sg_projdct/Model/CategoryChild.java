package com.graduate.seoil.sg_projdct.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by baejanghun on 16/04/2019.
 */
public class CategoryChild implements Parcelable {
    private String name;
    private boolean isFavorite;

    public CategoryChild(String name, boolean isFavorite) {
        this.name = name;
        this.isFavorite = isFavorite;
    }

    protected CategoryChild(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryChild)) return false;

        CategoryChild artist = (CategoryChild) o;

        if (isFavorite() != artist.isFavorite()) return false;
        return getName() != null ? getName().equals(artist.getName()) : artist.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (isFavorite() ? 1 : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryChild> CREATOR = new Creator<CategoryChild>() {
        @Override
        public CategoryChild createFromParcel(Parcel in) {
            return new CategoryChild(in);
        }

        @Override
        public CategoryChild[] newArray(int size) {
            return new CategoryChild[size];
        }
    };
}
