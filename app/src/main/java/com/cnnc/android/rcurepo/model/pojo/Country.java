package com.cnnc.android.rcurepo.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class Country implements Parcelable {

    @SerializedName("rank")
    private String countryRank;

    @SerializedName("country")
    private String countryName;

    @SerializedName("population")
    private String countryPopulation;

    @SerializedName("flag")
    private String countryFlagUrl;

    protected Country(Parcel in) {
        countryRank = in.readString();
        countryName = in.readString();
        countryPopulation = in.readString();
        countryFlagUrl = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String getCountryRank() {
        return countryRank;
    }

    public void setCountryRank(String countryRank) {
        this.countryRank = countryRank;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryPopulation() {
        return countryPopulation;
    }

    public void setCountryPopulation(String countryPopulation) {
        this.countryPopulation = countryPopulation;
    }

    public String getCountryFlagUrl() {
        return countryFlagUrl;
    }

    public void setCountryFlagUrl(String countryFlagUrl) {
        this.countryFlagUrl = countryFlagUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryRank);
        dest.writeString(countryName);
        dest.writeString(countryPopulation);
        dest.writeString(countryFlagUrl);
    }

}
