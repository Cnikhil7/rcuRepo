package com.cnnc.android.redcarpetuprepo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.cnnc.android.redcarpetuprepo.R;
import com.cnnc.android.redcarpetuprepo.data.DataLoad;
import com.cnnc.android.redcarpetuprepo.data.DataLoader;
import com.cnnc.android.redcarpetuprepo.data.retrofit.RetroClient;
import com.cnnc.android.redcarpetuprepo.model.pojo.Country;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.View {

    private HomeActivityContract.Presenter presenter;
    private RetroClient retroClient;
    private DataLoader dataLoader;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retroClient = new RetroClient();
        dataLoader = new DataLoader();
        setPresenter(new HomePresenter(this, retroClient, dataLoader));

        setRecyclerView();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(new CountryResponse(new ArrayList<>()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //safely clear any leftover garbage from presenter
        presenter.onStop();

        if (presenter != null) presenter = null;
        if (retroClient != null) retroClient = null;
        if (dataLoader != null) dataLoader = null;


    }

    @Override
    public void onLoadSuccess(CountryResponse response) {
        Toast.makeText(this, response.getCountryList() + "", Toast.LENGTH_SHORT).show();

        bindDataToRecyclerView(response.getCountryList());
    }

    private void bindDataToRecyclerView(List<Country> countryList) {
        ((RecyclerViewAdapter)adapter).addItems(countryList);
    }

    @Override
    public void onLoadFailure() {
        Toast.makeText(this, "Error Loading Results", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLoadingIndicator(boolean b) {

    }

    @Override
    public void setPresenter(HomeActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
