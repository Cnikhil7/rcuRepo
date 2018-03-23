package com.cnnc.android.rcurepo.data;

import com.cnnc.android.rcurepo.data.retrofit.RetroClient;
import com.cnnc.android.rcurepo.model.pojo.CountryResponse;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public interface DataLoad {

    interface Callback {
        void onSuccess(CountryResponse response);

        void onFailure();
    }

    void getResults(RetroClient retroClient, Callback callback);

    void onDestroy();
}
