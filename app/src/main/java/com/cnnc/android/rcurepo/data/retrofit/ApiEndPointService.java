package com.cnnc.android.rcurepo.data.retrofit;

import com.cnnc.android.rcurepo.model.pojo.CountryResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public interface ApiEndPointService {

    @GET("tutorial/jsonparsetutorial.txt")
    Single<CountryResponse> getCountryList();
}
