
package com.tallytest.tablayout;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Inicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Inicio extends Fragment {


    public Inicio() {
        // Required empty public constructor
    }


    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        //version
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            TextView tvVersion   = view.findViewById(R.id.tvVersion);

            tvVersion.setText("V " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return view;
    }
}