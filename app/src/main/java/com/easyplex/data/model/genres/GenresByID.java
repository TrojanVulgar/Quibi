
package com.easyplex.data.model.genres;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenresByID implements Parcelable {

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;


    public GenresByID(List<Genre> genres) {
        super();
        this.genres = genres;
    }

    protected GenresByID(Parcel in) {
        genres = in.createTypedArrayList(Genre.CREATOR);
    }

    public static final Creator<GenresByID> CREATOR = new Creator<GenresByID>() {
        @Override
        public GenresByID createFromParcel(Parcel in) {
            return new GenresByID(in);
        }

        @Override
        public GenresByID[] newArray(int size) {
            return new GenresByID[size];
        }
    };

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(genres);
    }

}
