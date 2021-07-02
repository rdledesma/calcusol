package com.example.calcusol;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndexFragment extends Fragment {


    private TextView tvVersion;


    protected static final String TAG = "SlidableActivity";
    private static final int ACTION_TYPE_DEFAULT = 0;
    private static final int ACTION_TYPE_UP = 1;
    private static final int ACTION_TYPE_RIGHT = 2;
    private static final int ACTION_TYPE_DOWN = 3;
    private static final int ACTION_TYPE_LEFT = 4;
    private static final int SLIDE_RANGE = 100;
    private float mTouchStartPointX;
    private float mTouchStartPointY;
    private int mActionType = ACTION_TYPE_DEFAULT;

    public IndexFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_index, container, false);

        tvVersion = view.findViewById(R.id.tvVersion);

        //version
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            tvVersion.setText("V " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }







        return view;
    }





}