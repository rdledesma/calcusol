package com.tallytest.tablayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.tallytest.tablayout.Controller.PagerController;
import com.tallytest.tablayout.modals.ExportModal;
import com.tallytest.tablayout.viewmodel.IrradianciaModel;

public class MainActivity extends AppCompatActivity implements ExportModal.FileNameSelected {

    TabLayout tabLayout;
    ViewPager viewPager;
    PagerController pagerAdapter;
    TabItem tab1, tab2, tab3;
    Toolbar toolbar;
    private IrradianciaModel model;
    public Integer gran;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        gran  = 5;
        model = new ViewModelProvider(MainActivity.this).get(IrradianciaModel.class);

        setContentView(R.layout.activity_main);

        model.setGranularity(5);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageView imageView = findViewById(R.id.imgDownload);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportModal exportModal = new ExportModal();
                exportModal.show(getSupportFragmentManager() , "asd");
            }
        });

        TextView granularity = findViewById(R.id.tvGranularity);
        granularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                switch (gran){
                    case 5:
                        granularity.setText("10");
                        model.setGranularity(10);
                        gran = 10;
                        break;
                    case 10:
                        granularity.setText("15");
                        model.setGranularity(15);
                        gran = 15;
                        break;
                    case 15:
                        granularity.setText("5");
                        model.setGranularity(5);
                        gran = 5;
                        break;
                }

            }
        });




        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new PagerController(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition() == 2){
                    pagerAdapter.notifyDataSetChanged();
                }
                if(tab.getPosition() == 3){
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    @Override
    public void sendName(String name) {
        Log.d("LOG", name);
    }
}
