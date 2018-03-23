package com.cnnc.android.rcurepo.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


import java.util.List;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class CountryResponse implements Parcelable {

    @SerializedName("worldpopulation")
    private List<Country> countryList;

    //public constructor as retrofit requires pojos with collections to have one.
    public CountryResponse(List<Country> countryList) {
        this.countryList = countryList;
    }

    protected CountryResponse(Parcel in) {
        countryList = in.createTypedArrayList(Country.CREATOR);
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public static final Creator<CountryResponse> CREATOR = new Creator<CountryResponse>() {
        @Override
        public CountryResponse createFromParcel(Parcel in) {
            return new CountryResponse(in);
        }

        @Override
        public CountryResponse[] newArray(int size) {
            return new CountryResponse[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(countryList);
    }
}
