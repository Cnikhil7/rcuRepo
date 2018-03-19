package com.cnnc.android.redcarpetuprepo.data.retrofit;

import com.cnnc.android.redcarpetuprepo.model.pojo.Country;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public interface ApiEndPointService {

    @GET("tutorial/jsonparsetutorial.txt")
    Single<CountryResponse> getCountryList();
}
