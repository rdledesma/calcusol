package com.tallytest.tablayout.Controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tallytest.tablayout.Calendario;
import com.tallytest.tablayout.Entrada;
import com.tallytest.tablayout.Inicio;
import com.tallytest.tablayout.IrradianciaTOA;

public class PagerController extends FragmentPagerAdapter {
    int numoftabs;

    public PagerController(@NonNull @org.jetbrains.annotations.NotNull FragmentManager fm,  int behavior) {
        super(fm, behavior);
        this.numoftabs = behavior;
    }


    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Inicio();
            case 1:
                return  new Entrada();
            case 2:
                return  new Calendario();
            case 3:
                return new IrradianciaTOA();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return this.numoftabs;
    }
}