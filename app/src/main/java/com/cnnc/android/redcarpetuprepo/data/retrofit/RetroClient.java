package com.cnnc.android.redcarpetuprepo.data.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by NIKHIL on 1/3/2018.
 */

public class RetroClient {

    private ApiEndPointService mClient ;

    private static String BASE_URL = "http://www.androidbegin.com/";


    public ApiEndPointService getClient() {

        if (mClient == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return mClient = retrofit.create(ApiEndPointService.class);
        }

        return mClient;
    }



}
