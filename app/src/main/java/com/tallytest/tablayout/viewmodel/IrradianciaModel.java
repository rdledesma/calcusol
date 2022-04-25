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


    private MutableLiveData<String> amanecer = new MutableLiveData<>();
    private MutableLiveData<String> ocaso = new MutableLiveData<>();

    private MutableLiveData<String> mediodiaSolar = new MutableLiveData<>();
    private MutableLiveData<String> maxAlturaSolar = new MutableLiveData<>();

    private MutableLiveData<Integer> granularity= new MutableLiveData<>();
    private MutableLiveData<Double> rbMediodia= new MutableLiveData<>();
    private MutableLiveData<Double> razonI = new MutableLiveData<>();
    private MutableLiveData<Double> kt = new MutableLiveData<>();


    public MutableLiveData<Double> getKt() {
        return kt;
    }

    public void setKt(Double kt) {
        this.kt.setValue(kt);
    }

    public MutableLiveData<String> getDuracion() {
        return duracion;
    }


    public MutableLiveData<Double> getRbMediodia() {
        return rbMediodia;
    }

    public void setRbMediodia(Double rbMediodia) {
        this.rbMediodia.setValue(rbMediodia);
    }

    public MutableLiveData<Double> getRazonI() {
        return razonI;
    }

    public void setRazonI(Double razonI) {
        this.razonI.setValue(razonI);
    }

    public void setDuracion(String duracion) {
        this.duracion.setValue(duracion);
    }

    private MutableLiveData<String> duracion = new MutableLiveData<>();


    public MutableLiveData<String> getMediodiaSolar() {
        return mediodiaSolar;
    }

    public void setMediodiaSolar(String mediodiaSolar) {
        this.mediodiaSolar.setValue(mediodiaSolar);
    }

    public MutableLiveData<String> getMaxAlturaSolar() {
        return maxAlturaSolar;
    }

    public void setMaxAlturaSolar(String maxAlturaSolar) {
        this.maxAlturaSolar.setValue(maxAlturaSolar);
    }

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


    public MutableLiveData<String> getAmanecer() {
        return amanecer;
    }

    public void setAmanecer(String amanecer) {
        this.amanecer.setValue(amanecer);
    }

    public MutableLiveData<String> getOcaso() {
        return ocaso;
    }

    public void setOcaso(String ocaso) {
        this.ocaso.setValue(ocaso);
    }


    public void setGranularity(Integer granularity){
        this.granularity.setValue(granularity);
    }

    public MutableLiveData<Integer>  getGranularity(){
        return this.granularity;
    }
}
