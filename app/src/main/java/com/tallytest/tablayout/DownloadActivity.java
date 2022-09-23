package com.tallytest.tablayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tallytest.tablayout.Adapters.FilesAdapter;

import java.io.File;
import java.util.ArrayList;
import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class DownloadActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<File> archivos = new ArrayList<>();
    private FilesAdapter customAdapter = new FilesAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        listView = findViewById(R.id.list_items);

        customAdapter = new FilesAdapter(getApplicationContext(), archivos);

        cargarArchivos();
    }

    private void cargarArchivos() {
        archivos.clear();


        String path = Environment.getExternalStorageDirectory().toString() + "/CALCUSOL";

        if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
            path = Environment.getExternalStorageDirectory().getPath() + "//CALCUSOL";

        } else {
            path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + "//CALCUSOL";
        }


        File directory = new File(path);
        File[] files = directory.listFiles();


        for (int i = 0; i < files.length; i++) {
            archivos.add(files[i]);
        }

        if (archivos != null) {

            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    File item = (File) customAdapter.getItem(i);


                    Uri fileUri = FileProvider.getUriForFile(DownloadActivity.this, getApplicationContext().getPackageName() + ".provider", item);


                    String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(item).toString());
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);


                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setDataAndType(fileUri, mimetype);
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    getApplicationContext().startActivity(sendIntent);

                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    File item = (File) customAdapter.getItem(i);
                    item.delete();

                    cargarArchivos();

                    return false;
                }
            });
        }
    }

}