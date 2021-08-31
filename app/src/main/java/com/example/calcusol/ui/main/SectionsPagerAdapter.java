package com.example.calcusol.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.calcusol.DescargasFragment;
import com.example.calcusol.HoraSolarFragment;
import com.example.calcusol.IndexFragment;
import com.example.calcusol.IrradianciaFragment;
import com.example.calcusol.ManualFragment;
import com.example.calcusol.R;
import com.example.calcusol.ResultadosFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {



        switch (position){

            case 0:
                HoraSolarFragment horaSolarFragment = new HoraSolarFragment();
                return horaSolarFragment;
            case 1:
                IrradianciaFragment irradianciaFragment = new IrradianciaFragment();
                return irradianciaFragment;
            case 2:
                ResultadosFragment resultadosFragment= new ResultadosFragment();
                return resultadosFragment;
            case 3:
                DescargasFragment descargasFragment = new DescargasFragment();
                return descargasFragment;
            case 4:
                 ManualFragment manualFragment = new ManualFragment();
                return manualFragment;
        }

        return null;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String label = "";
        switch (position){
            case 0:
                label = "Fecha de calculo";
                break;
            case 1:
                label = "Irradiancia";
                break;
            case 2:
                label = "Resultados";
                break;
            case 3:
                label = "Descargas";
                break;


        }

        return label;

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 4;
    }
}