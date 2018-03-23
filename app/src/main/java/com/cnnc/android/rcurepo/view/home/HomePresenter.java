package com.cnnc.android.rcurepo.view.home;

import com.cnnc.android.rcurepo.data.ExternalFileWriter;
import com.cnnc.android.rcurepo.model.pojo.Contacts;
import com.cnnc.android.rcurepo.model.pojo.CountryResponse;
import com.cnnc.android.rcurepo.data.DataLoad;
import com.cnnc.android.rcurepo.data.DataLoader;
import com.cnnc.android.rcurepo.data.fileSaver;
import com.cnnc.android.rcurepo.data.retrofit.RetroClient;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomePresenter implements HomeActivityContract.Presenter {

    private boolean firstLaunch = true;
    private HomeActivityContract.View homeView;
    private RetroClient retroClient;
    private DataLoader dataLoader;
    private ExternalFileWriter fileWriter;


    public HomePresenter(@NonNull HomeActivityContract.View homeView
            , @NonNull RetroClient retroClient, @NonNull DataLoader dataLoader, ExternalFileWriter fileWriter) {
        this.homeView = homeView;
        this.retroClient = retroClient;
        this.dataLoader = dataLoader;
        this.fileWriter = fileWriter;
    }

    @Override
    public void onStart() {
        loadResult(false);
    }

    @Override
    public void onStop() {
        //clear the disposables
        dataLoader.onDestroy();
        fileWriter.onDestroy();
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
    public void saveContactsToStorage(ArrayList<Contacts> contacts, String appName) {
        homeView.setLoadingIndicator(true);

        fileWriter.writeThisAsCsvToStorage(contacts, appName, new fileSaver.Callback() {
            @Override
            public void onSuccess() {
                homeView.onSuccessSavingCsvToExternalStorage();
                homeView.setLoadingIndicator(false);
            }

            @Override
            public void onFailure() {
                homeView.onErrorSavingCsvToExternalStorage();
                homeView.setLoadingIndicator(false);
            }
        });


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
