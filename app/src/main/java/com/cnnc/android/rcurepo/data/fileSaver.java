package com.cnnc.android.rcurepo.data;

import com.cnnc.android.rcurepo.model.pojo.Contacts;

import java.util.ArrayList;

/**
 * Created by NIKHIL on 3/23/2018.
 */

public interface fileSaver {

    interface Callback {

        void onSuccess();

        void onFailure();
    }

    void writeThisAsCsvToStorage(ArrayList<Contacts> contacts, String appName, Callback callback);

    void onDestroy();
}
