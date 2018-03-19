package com.cnnc.android.redcarpetuprepo.view;

import com.cnnc.android.redcarpetuprepo.base.basePresenter;
import com.cnnc.android.redcarpetuprepo.base.baseView;
import com.cnnc.android.redcarpetuprepo.model.pojo.Country;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import java.util.ArrayList;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomeActivityContract {

    interface View extends baseView<Presenter> {

        void onLoadSuccess(CountryResponse a);

        void onLoadFailure();

        void setLoadingIndicator(boolean b);
    }

    interface Presenter extends basePresenter {

        void loadResult(boolean forceUpdate);

    }
}
