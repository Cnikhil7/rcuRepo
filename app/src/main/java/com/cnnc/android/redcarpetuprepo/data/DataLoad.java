package com.cnnc.android.redcarpetuprepo.data;

import com.cnnc.android.redcarpetuprepo.data.retrofit.RetroClient;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

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
