package com.tallytest.tablayout.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IrradianciaModel extends ViewModel {


    private MutableLiveData<String> gmt = new MutableLiveData<>();
    private MutableLiveData<String> diaJuliano = new MutableLiveData<>();
    private MutableLiveData<String> latitud = new MutableLiveData<>();
    private MutableLiveData<String> longitud = new MutableLiveData<>();
    private MutableLiveData<String> beta = new MutableLiveData<>();
    private MutableLiveData<String> gamma = new MutableLiveData<>();
    private MutableLiveData<String> altitud = new MutableLiveData<>();


    public MutableLiveData<String> getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt.setValue(gmt);
    }

    public MutableLiveData<String> getDiaJuliano() {
        return diaJuliano;
    }

    public void setDiaJuliano(String diaJuliano) {
        this.diaJuliano.setValue(diaJuliano);
    }

    public MutableLiveData<String> getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud.setValue(latitud);
    }

    public MutableLiveData<String> getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud.setValue(longitud);
    }

    public MutableLiveData<String> getBeta() {
        return beta;
    }

    public void setBeta(String beta) {
        this.beta.setValue(beta);
    }

    public MutableLiveData<String> getGamma() {
        return gamma;
    }

    public void setGamma(String gamma) {
        this.gamma.setValue(gamma);
    }

    public MutableLiveData<String> getAltitud() {
        return altitud;
    }

    public void setAltitud(String altitud) {
        this.altitud.setValue(altitud);
    }
}
