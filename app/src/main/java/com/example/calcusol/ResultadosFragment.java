package com.example.calcusol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultadosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultadosFragment extends Fragment {

    TextView diaJuliano, lat, longitud, amanecer,ocaso, duracion, gmt, razon, mediodia, rb, altura;

    String diaSaved;
    String longitudSaved;
    String latitudSaved;
    String savedGMT;
    String razonSaved;
    String mediodiaSaved;
    String rbSaved;
    String maxAltura;
    Double savedAmanecer, savedOcaso;


    public ResultadosFragment() {
        // Required empty public constructor
    }


    public static ResultadosFragment newInstance(String param1, String param2) {
        ResultadosFragment fragment = new ResultadosFragment();
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

        View view = inflater.inflate(R.layout.fragment_resultados, container, false);

        diaJuliano = view.findViewById(R.id.tvDiaJuliano);
        lat = view.findViewById(R.id.tvLat);
        longitud = view.findViewById(R.id.tvLong);
        duracion = view.findViewById(R.id.tvDuracion);
        amanecer = view.findViewById(R.id.tvAmanecer);
        ocaso = view.findViewById(R.id.tvOcaso);
        gmt = view.findViewById(R.id.tvGMT);
        razon = view.findViewById(R.id.razonIrradiancia);
        mediodia = view.findViewById(R.id.tvMediodia);
        rb = view.findViewById(R.id.Rb);
        altura = view.findViewById(R.id.tvAltura);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        diaSaved = sharedPref.getString(getString(R.string.saved_dia), "1");
        longitudSaved = sharedPref.getString(getString(R.string.saved_longitud), "59.1");
        latitudSaved = sharedPref.getString(getString(R.string.saved_latitud), "-36.03");
        savedGMT = sharedPref.getString(getString(R.string.saved_gmt), "-3");
        razonSaved = sharedPref.getString(getString(R.string.saved_razon_irradiancia), "0.0");
        mediodiaSaved = sharedPref.getString(getString(R.string.saved_mediodia), "0.0");
        rbSaved = sharedPref.getString(getString(R.string.saved_rb), "0.0");
        maxAltura = sharedPref.getString(getString(R.string.saved_max_altura), "0.0");

        savedAmanecer = Double.parseDouble(sharedPref.getString(getString(R.string.saved_amanercer), "0"));
        savedOcaso = Double.parseDouble(sharedPref.getString(getString(R.string.saved_ocaso), "0"));

        diaJuliano.setText(diaSaved);
        lat.setText(latitudSaved+"°");
        longitud.setText(longitudSaved+"°");
        gmt.setText(savedGMT);

        amanecer.setText(parseToTime(savedAmanecer)+" hs");
        ocaso.setText(parseToTime(savedOcaso)+" hs");
        duracion.setText(parseToTime(savedOcaso-savedAmanecer)+" hs");

        razon.setText(razonSaved);
        mediodia.setText(mediodiaSaved+ "hs");
        rb.setText(rbSaved);
        altura.setText(maxAltura+"°");

        return view;
    }


    private String parseToTime(double hora){
        BigDecimal bd = new BigDecimal(String.valueOf(hora));
        BigDecimal iPart = new BigDecimal(bd.toBigInteger());
        BigDecimal fPart = bd.remainder( BigDecimal.ONE );
        BigDecimal fPartToInt = bd.subtract(bd.setScale(0, RoundingMode.FLOOR)).movePointRight(bd.scale());

        BigInteger rounded = fPartToInt.toBigInteger();


        double parcial = hora;

        if(hora<1){
            if(hora/0.01666666667<10){
                return "00:"+"0"+(int)(hora/0.01666666667);

            }

            else{
                return "00:"+ (int)(hora/0.01666666667);
            }

        }

        else{

            int incremente = 0;
            double minutos  =((hora -(int)hora)) /0.01666666667;

            int as = (int) minutos;


            if (minutos<10){
                return (int)(hora+incremente)+":0"+((int)minutos) ;
            }
            else{
                return (int)(hora+incremente)+":"+((int)minutos) ;
            }

        }
    }

    private double truncate(double num){
        return Math.floor(num*100) / 100;
    }

    private double gete(int dia){

        Double result;
        result = 1 + 0.033 *Math.cos(2*Math.PI* (double)dia/365);
        return truncate(result);
    }

    private double getDeclinacion(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        Double result;
        result = 23.45 * Math.sin(Math.toRadians(360*(284+DiaJuliano))/365);
        return result;
    }

    private double getSalidaSol(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        double LAT = Double.parseDouble(String.valueOf(latitudSaved));
        double LONG = Double.parseDouble(String.valueOf(longitudSaved));
        double LONGOBS = LONG - (LONG%15);


        double GMT= Double.parseDouble(String.valueOf(gmt.getText()));
        double result ,resultParcial, resultRad;
        Double dec = getDeclinacion();
        double termino1 =  -Math.tan(Math.toRadians(LAT)) * Math.tan(Math.toRadians(dec.doubleValue()));
        double ws = Math.toDegrees(Math.acos(termino1));
        resultParcial =  12- ws/15;
        double resultts = resultParcial;
        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG) + gete(DiaJuliano))/60;

        //double resulttsof = resultts - (-1) + (4*((1*15 * GMT)-(1*LONG))+gete(DiaJuliano))/60;
        //double resulttsof = resultts - (-1) + ((4*(15 * GMT)-LONG) +gete(DiaJuliano))/60;

        return resulttsof;

    }


    private double getPuestaSol(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        double LAT = Double.parseDouble(String.valueOf(latitudSaved));
        double LONG = Double.parseDouble(String.valueOf(longitudSaved));
        double LONGOBS = LONG - (LONG%15);
        double result ,resultParcial, resultRad;

        Double dec = getDeclinacion();

        double termino1 =  -Math.tan(Math.toRadians(LAT)) * Math.tan(Math.toRadians(dec.doubleValue()));
        double ws = Math.toDegrees(Math.acos(termino1));

        resultParcial =  12 + ws/15;

        double resultts = resultParcial;

        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG) + gete(DiaJuliano))/60;



        return resulttsof;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            diaSaved = sharedPref.getString(getString(R.string.saved_dia), "1");
            longitudSaved = sharedPref.getString(getString(R.string.saved_longitud), "59.1");
            latitudSaved = sharedPref.getString(getString(R.string.saved_latitud), "-36.03");
            savedGMT = sharedPref.getString(getString(R.string.saved_gmt), "-3");
            razonSaved = sharedPref.getString(getString(R.string.saved_razon_irradiancia), "0.0");
            mediodiaSaved = sharedPref.getString(getString(R.string.saved_mediodia), "0.0");
            rbSaved = sharedPref.getString(getString(R.string.saved_rb), "0.0");
            maxAltura = sharedPref.getString(getString(R.string.saved_max_altura), "0.0");

            savedAmanecer = Double.parseDouble(sharedPref.getString(getString(R.string.saved_amanercer), "0"));
            savedOcaso = Double.parseDouble(sharedPref.getString(getString(R.string.saved_ocaso), "0"));

            diaJuliano.setText(diaSaved);
            lat.setText(latitudSaved+"°");
            longitud.setText(longitudSaved+"°");
            gmt.setText(savedGMT);

            amanecer.setText(parseToTime(savedAmanecer)+" hs");
            ocaso.setText(parseToTime(savedOcaso)+" hs");
            duracion.setText(parseToTime(savedOcaso-savedAmanecer)+" hs");

            razon.setText(razonSaved);
            mediodia.setText(mediodiaSaved+ "hs");
            rb.setText(rbSaved);
            altura.setText(maxAltura+"°");


            if (getFragmentManager() != null) {

                getFragmentManager()
                        .beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
            }
        }
    }
}