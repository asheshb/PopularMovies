package com.bitwindow.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ashbey on 8/21/2015.
 */
public class GenreItem  implements Parcelable {
    private static final String LOG_TAG = GenreItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private int id;
    private String name;

    public int getId(){
        return id;
    }

    public void setId(int id){ this.id = id;}

    public String getName(){ return this.name;}

    public void setName(String name){
        this.name = name;
    }

    protected GenreItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);

    }

    public static final Parcelable.Creator<GenreItem> CREATOR = new Parcelable.Creator<GenreItem>() {
        @Override
        public GenreItem createFromParcel(Parcel in) {
            return new GenreItem(in);
        }

        @Override
        public GenreItem[] newArray(int size) {
            return new GenreItem[size];
        }
    };

}
