package com.cnnc.android.rcurepo.data;

import android.os.Environment;

import com.cnnc.android.rcurepo.model.pojo.Contacts;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by NIKHIL on 3/23/2018.
 */

public class ExternalFileWriter implements fileSaver {

    private CompositeDisposable disposable;

    @Override
    public void writeThisAsCsvToStorage(ArrayList<Contacts> contacts, String appName, Callback callback) {

        if (disposable == null) disposable = new CompositeDisposable();
        Single.fromCallable(() -> zipAndSaveTheCsvOnExternalStorage(contacts, appName))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(a -> onSaveComplete(a, callback));

    }

    @Override
    public void onDestroy() {
        if(disposable !=null)disposable.clear();
    }


    private int zipAndSaveTheCsvOnExternalStorage(ArrayList<Contacts> list, String appName) {

        String fileName = "contactsRCUR.csv";

        ICsvBeanWriter writer = null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File root = new File(Environment
                    .getExternalStorageDirectory() + "/" + appName);

            boolean directoryExists = true;

            if (!root.exists()) {
                directoryExists = root.mkdir();
            }

            File file = new File(root, fileName);

            if (directoryExists) {

                try {
                    FileOutputStream zipFile = new FileOutputStream(new File(root, "contactsRCURA.zip"));
                    ZipOutputStream zos = new ZipOutputStream(zipFile);
                    ICsvBeanWriter contactWriter = new CsvBeanWriter(new OutputStreamWriter(zos, "UTF-8")
                            , CsvPreference.STANDARD_PREFERENCE);

                    ZipEntry zip = new ZipEntry("contactsRCURA.csv");
                    zos.putNextEntry(zip);

                    String[] mapping = {"contactId", "contactName", "contactPhone", "contactTimesContacted"};
                    String[] header = {"Contact Id", "Contact Name", "Contact Phone Number", "Contact Times Contacted"};

                    contactWriter.writeHeader(header);
                    for (Contacts co : list) contactWriter.write(co, mapping);

                    contactWriter.flush();
                    zos.flush();
                    zos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }

            } else return -1;

        } else return -1;

        return 0;

    }


    private void onSaveComplete(int a, Callback callback) {
        if (a == -1) callback.onFailure();
        else callback.onSuccess();
    }
}
