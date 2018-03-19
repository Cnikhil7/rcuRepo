package com.cnnc.android.redcarpetuprepo.data;

import com.cnnc.android.redcarpetuprepo.data.retrofit.ApiEndPointService;
import com.cnnc.android.redcarpetuprepo.data.retrofit.RetroClient;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class DataLoader implements DataLoad {

    private CompositeDisposable disposable;

    @Override
    public void getResults(RetroClient retroClient, Callback callback) {
        ApiEndPointService service = retroClient.getClient();

        if (disposable == null) disposable = new CompositeDisposable();

        disposable
                .add(service.getCountryList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CountryResponse>() {
                            @Override
                            public void onSuccess(CountryResponse countryResponse) {
                                callback.onSuccess(countryResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onFailure();
                            }
                        }));

    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }

}
