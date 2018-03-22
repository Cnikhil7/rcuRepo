package com.cnnc.android.redcarpetuprepo.view;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.cnnc.android.redcarpetuprepo.data.DataLoad;
import com.cnnc.android.redcarpetuprepo.data.DataLoader;
import com.cnnc.android.redcarpetuprepo.data.retrofit.RetroClient;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import org.jetbrains.annotations.Contract;

import java.net.URI;

import io.reactivex.annotations.NonNull;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomePresenter implements HomeActivityContract.Presenter {

    private boolean firstLaunch = true;
    private HomeActivityContract.View homeView;
    private RetroClient retroClient;
    private DataLoader dataLoader;


    public HomePresenter(@NonNull HomeActivityContract.View homeView, @NonNull RetroClient retroClient, @NonNull DataLoader dataLoader) {
        this.homeView = homeView;
        this.retroClient = retroClient;
        this.dataLoader = dataLoader;
    }

    @Override
    public void onStart() {
        loadResult(false);
    }

    @Override
    public void onStop() {
        //clear the disposable
        dataLoader.onDestroy();
    }

    @Override
    public void loadResult(boolean forceRefresh) {
        if (forceRefresh || firstLaunch) {
            //continue to load
            continueLoading();
        }

        firstLaunch = false;
    }

    @Override
    public void fetchContacts() {


    }

    private void continueLoading() {
        homeView.setLoadingIndicator(true);

        dataLoader.getResults(retroClient, new DataLoad.Callback() {
            @Override
            public void onSuccess(CountryResponse response) {
                homeView.onLoadSuccess(response);
                homeView.setLoadingIndicator(false);
            }

            @Override
            public void onFailure() {
                homeView.onLoadFailure();
                homeView.setLoadingIndicator(false);
            }
        });
    }
}
