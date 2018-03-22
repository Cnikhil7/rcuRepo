package com.cnnc.android.redcarpetuprepo.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.cnnc.android.redcarpetuprepo.R;
import com.cnnc.android.redcarpetuprepo.data.DataLoader;
import com.cnnc.android.redcarpetuprepo.data.retrofit.RetroClient;
import com.cnnc.android.redcarpetuprepo.model.pojo.Contacts;
import com.cnnc.android.redcarpetuprepo.model.pojo.Country;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;
import com.cnnc.android.redcarpetuprepo.view.detail.ImageDetailActivity;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by NIKHIL on 3/19/2018.
 */

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.View,
        RecyclerViewAdapter.HomeRecyclerInterface {

    public static final String COUNTRY_ITEM = "countryPoster";

    private static final int READ_CONTACT_REQUEST = 0;

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

        // "this" here is HomeActivityContract.View interface reference, not the activity context.
        setPresenter(new HomePresenter(this, retroClient, dataLoader));

        setRecyclerView();
        setUpButtons();
    }

    private void setUpButtons() {
        findViewById(R.id.home_fab).setOnClickListener(a -> fetchContactList());
    }

    private void fetchContactList() {


        if (!checkContactReadPermission()) requestForContactReadAccess();
        else {
            continueFetchingContactList();
//            presenter.fetchContacts();

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


            checkStorageReadPermission();
            //now to csv part

            String fileName = "contactsRCUR.csv";
            ICsvBeanWriter writer = null;

            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {

                File root = new File(Environment
                        .getExternalStorageDirectory() + "/" + getString(R.string.app_name));

                boolean mkdir = true;

                if (!root.exists()) {
                    mkdir = root.mkdir();
                }

                File file = new File(root, fileName);

                if (mkdir) {

                    try {
                        writer = new CsvBeanWriter(new FileWriter(file),
                                CsvPreference.STANDARD_PREFERENCE);

                        String[] mapping = {"contactId", "contactName", "contactPhone", "contactTimesContacted"};
                        String[] header = {"Contact Id", "Contact Name", "Contact Phone Number", "Contact Times Contacted"};
                        writer.writeHeader(header);
                        for (Contacts contact : list) {
                            writer.write(contact, mapping);
                        }

                        writer.flush();
                        writer.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ///

                    try {
                        FileOutputStream zipFile = new FileOutputStream(new File(root, "contactsRCURA.zip"));
                        ZipOutputStream zos = new ZipOutputStream(zipFile);
                        ICsvBeanWriter listWriter = new CsvBeanWriter(new OutputStreamWriter(zos, "UTF-8")
                                , CsvPreference.STANDARD_PREFERENCE);


                        ZipEntry zip = new ZipEntry("contactsRCURA.csv");
                        zos.putNextEntry(zip);


                        String[] mapping = {"contactId", "contactName", "contactPhone", "contactTimesContacted"};
                        String[] header = {"Contact Id", "Contact Name", "Contact Phone Number", "Contact Times Contacted"};

                        listWriter.writeHeader(header);
                        for (Contacts co : list) listWriter.write(co, mapping);

                        listWriter.flush();
                        zos.flush();
                        zos.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    private void checkStorageReadPermission() {

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

    private void continueFetchingContactList() {
        //Have read access, continue retrieving the list.

    }

    private void requestForContactReadAccess() {
        //Could also explain user the need for read access here.
        //for now just continuing with request permission.

        ActivityCompat.requestPermissions(this
                , new String[]{Manifest.permission.READ_CONTACTS}
                , READ_CONTACT_REQUEST);
    }

    private boolean checkContactReadPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RecyclerViewAdapter(this
                , new CountryResponse(new ArrayList<>())
                , this, getScreenWidth() / 2); //width / 2 as span is 2
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //safely clear any leftover garbage from presenter
        presenter.onStop();

        if (presenter != null) presenter = null;
        if (retroClient != null) retroClient = null;
        if (dataLoader != null) dataLoader = null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACT_REQUEST) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                continueFetchingContactList();
            } else Toast.makeText(this,
                    getString(R.string.contact_read_permission_denied)
                    , Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLoadSuccess(CountryResponse response) {
        bindDataToRecyclerView(response.getCountryList());
    }

    private void bindDataToRecyclerView(List<Country> countryList) {
        ((RecyclerViewAdapter) adapter).addItems(countryList);
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
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        return point.x;
    }
}
