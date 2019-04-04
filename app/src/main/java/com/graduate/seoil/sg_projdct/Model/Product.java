package com.graduate.seoil.sg_projdct.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
   public final String name;

    private Product(Parcel in) {
        name = in.readString();
    }

    public Product(String name) {
        this.name = name;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }

    };

    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
