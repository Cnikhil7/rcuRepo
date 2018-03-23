package com.cnnc.android.rcurepo.view.home;

import com.cnnc.android.rcurepo.base.basePresenter;
import com.cnnc.android.rcurepo.base.baseView;
import com.cnnc.android.rcurepo.model.pojo.Contacts;
import com.cnnc.android.rcurepo.model.pojo.CountryResponse;

import java.util.ArrayList;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomeActivityContract {

    interface View extends baseView<Presenter> {

        void onLoadSuccess(CountryResponse a);

        void onLoadFailure();

        void setLoadingIndicator(boolean b);

        void onErrorSavingCsvToExternalStorage();

        void onSuccessSavingCsvToExternalStorage();
    }

    interface Presenter extends basePresenter {

        void loadResult(boolean forceUpdate);

        void saveContactsToStorage(ArrayList<Contacts> contacts, String appName);
    }
}
