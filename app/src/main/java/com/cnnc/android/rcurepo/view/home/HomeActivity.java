package com.cnnc.android.rcurepo.view.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cnnc.android.rcurepo.data.ExternalFileWriter;
import com.cnnc.android.rcurepo.model.pojo.Contacts;

import com.cnnc.android.rcurepo.data.DataLoader;
import com.cnnc.android.rcurepo.data.retrofit.RetroClient;
import com.cnnc.android.rcurepo.model.pojo.Country;
import com.cnnc.android.rcurepo.model.pojo.CountryResponse;
import com.cnnc.android.rcurepo.util.GeneralUtils;
import com.cnnc.android.rcurepo.view.detail.ImageDetailActivity;
import com.cnnc.android.redcarpetuprepo.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.View,
        RecyclerViewAdapter.HomeRecyclerInterface {

    public static final String COUNTRY_ITEM = "countryPoster";

    private static final int READ_CONTACT_REQUEST = 0;
    private static final int STORAGE_WRITE_REQUEST = 1;
    private static final int STORAGE_READ_REQUEST = 2;

    private static final String PERSISTANT_LIST = "LIST";

    private HomeActivityContract.Presenter presenter;
    private RetroClient retroClient;
    private DataLoader dataLoader;
    private ExternalFileWriter fileWriter;
    private RecyclerView.Adapter adapter;
    private Disposable disposable;

    private ArrayList<Country> countries;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) countries = savedInstanceState.getParcelableArrayList(PERSISTANT_LIST);
        else countries = new ArrayList<>();

        retroClient = new RetroClient();
        dataLoader = new DataLoader();
        fileWriter = new ExternalFileWriter();

        // "this" here is HomeActivityContract.View interface reference, not the activity context.
        setPresenter(new HomePresenter(this, retroClient, dataLoader, fileWriter));

        setRecyclerView();
        setUpButtons();
    }

    private void setUpButtons() {
        findViewById(R.id.home_fab).setOnClickListener(a -> prepareFetchingContactList());
    }

    private void prepareFetchingContactList() {

        if (!checkContactReadPermission()) {
            requestUserForAccess(Manifest.permission.READ_CONTACTS, READ_CONTACT_REQUEST);
            return;
        }
        if (!checkStorageReadPermission()) {
            requestUserForAccess(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_READ_REQUEST);
            return;
        }
        if (!checkStorageWritePermission()) {
            requestUserForAccess(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_WRITE_REQUEST);
            return;
        }

        setLoadingIndicator(true);
        disposable = fetchContactList();


    }

    private Disposable fetchContactList() {
        String appName = getString(R.string.app_name);

        return Single.fromCallable(this::continueFetchingContactList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> writeCsvToDisk(a, appName));

    }

    private void writeCsvToDisk(ArrayList<Contacts> a, String appName) {
        presenter.saveContactsToStorage(a, appName);
    }

    private void requestUserForAccess(String permissionName, int requestCode) {
        ActivityCompat.requestPermissions(this
                , new String[]{permissionName}
                , requestCode);
    }


    private boolean checkStorageReadPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStorageWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkContactReadPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED);
    }

    private String getContactNumber(String id) {

        StringBuilder phoneNumber = new StringBuilder("");

        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id}
                , null);

        if (c != null && c.moveToNext()) {
            phoneNumber.append(c.getString(c.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }

        if (c != null) c.close();

        return phoneNumber.toString();
    }

    private ArrayList<Contacts> continueFetchingContactList() {
        //Have read access, continue retrieving the list.

        ArrayList<Contacts> list = new ArrayList<>();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projections = new String[]{
                ContactsContract.Contacts._ID
                , ContactsContract.Contacts.DISPLAY_NAME
                , ContactsContract.Contacts.HAS_PHONE_NUMBER
                , ContactsContract.Contacts.TIMES_CONTACTED};

        Cursor c = getContentResolver().query(uri, projections, null, null, null);

        if (c != null && c.moveToNext()) {
            do {

                String contactNumber = "";
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String timesContacted = c.getString(c.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));

                if (c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1)
                    contactNumber = getContactNumber(id);

                Contacts contact = new Contacts();
                contact.setContactId(id);
                contact.setContactName(name);
                contact.setContactTimesContacted(timesContacted);
                contact.setContactPhone(!contactNumber.isEmpty() ? contactNumber : "");

                list.add(contact);

            } while (c.moveToNext());
        } else {
            //toast no contacts to fetch
        }

        if (c != null) c.close();


        return list;
    }


    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecyclerViewAdapter(this
                , new CountryResponse(countries)
                , this, getScreenWidth() / 2); //width / 2 as span is 2
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countries == null || countries.size() == 0) presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //safely clear any leftover garbage from presenter
        presenter.onStop();

        if (presenter != null) presenter = null;
        if (retroClient != null) retroClient = null;
        if (dataLoader != null) dataLoader = null;
        if (fileWriter != null) fileWriter = null;
        if (disposable != null) disposable = null;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PERSISTANT_LIST, countries);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == READ_CONTACT_REQUEST || requestCode == STORAGE_READ_REQUEST || requestCode == STORAGE_WRITE_REQUEST) {

            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                prepareFetchingContactList();
            else Toast.makeText(this
                    , getString(R.string.contact_permission_denied)
                    , Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLoadSuccess(CountryResponse response) {

        List<Country> countryList = response.getCountryList();
        countries = (ArrayList<Country>) countryList;
        bindDataToRecyclerView(countryList);
    }


    @Override
    public void onLoadFailure() {
        Toast.makeText(this, "Error Loading Results", Toast.LENGTH_SHORT).show();
    }

    private void bindDataToRecyclerView(List<Country> countryList) {
        ((RecyclerViewAdapter) adapter).addItems(countryList);
    }

    @Override
    public void setLoadingIndicator(boolean b) {
        findViewById(R.id.home_progress_bar).setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onErrorSavingCsvToExternalStorage() {
        showSnackBar(getString(R.string.zip_save_unsuccessful));
    }

    @Override
    public void onSuccessSavingCsvToExternalStorage() {
        showSnackBar(getString(R.string.zip_save_successful));
    }

    public void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.home_top_container), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(HomeActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onHomeRecyclerItemClick(Country country, ImageView view) {

        view.setTransitionName(getString(R.string.transition_1));

        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, view, getString(R.string.transition_1)
        ).toBundle();

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra(COUNTRY_ITEM, country);

        startActivity(intent, bundle);

    }

    public int getScreenWidth() {
        int[] screenSize = GeneralUtils.getScreenSize(getWindowManager().getDefaultDisplay());
        return screenSize[0];
    }
}
