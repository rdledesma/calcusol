package com.example.calcusol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.core.content.MimeTypeFilter;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.calcusol.adapters.FilesAdapter;

import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DescargasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescargasFragment extends Fragment {

    private ListView listView;

    public DescargasFragment() {
        // Required empty public constructor
    }


    public static DescargasFragment newInstance() {
        DescargasFragment fragment = new DescargasFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_descargas, container, false);

        listView = root.findViewById(R.id.list_items);


        String path = Environment.getExternalStorageDirectory().toString()+"/CALCUSOL";

        if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
            path = Environment.getExternalStorageDirectory().getPath()+"//CALCUSOL";

        } else {
            path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath()+ "//CALCUSOL";
        }





        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);




        ArrayList<File> archivos = new ArrayList<>();

        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            archivos.add(files[i]);

        }

        if(archivos!=null){
            final FilesAdapter customAdapter = new FilesAdapter(getContext(), archivos);
            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    File item = (File) customAdapter.getItem(i);






                    Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()+".provider", item);


                    String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(item).toString());
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);


                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setDataAndType(fileUri, mimetype);
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    getContext().startActivity(sendIntent);

                }
            });
        }




        return root;
    }
}