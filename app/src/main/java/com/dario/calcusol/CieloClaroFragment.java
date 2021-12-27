package com.dario.calcusol;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CieloClaroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CieloClaroFragment extends Fragment {


    private LineChart grafica;
    private ArrayList<Double> irradianciaCieloClaro= new ArrayList<Double>();
    private ArrayList<String> horaArray = new ArrayList<String>();
    SharedPreferences shared;
    ArrayList<String> arrPackage;



    private Double latitud, longitud;
    private Integer diaJuliano, gmt, altitud;


    private double constGranularity = 1;

    public CieloClaroFragment() {
        // Required empty public constructor
    }


    public static CieloClaroFragment newInstance() {
        CieloClaroFragment fragment = new CieloClaroFragment();
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
        View view = inflater.inflate(R.layout.fragment_cielo_claro, container, false);






        grafica = view.findViewById(R.id.graficaCieloClaro);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        longitud = Double.parseDouble(sharedPref.getString(getString(R.string.saved_longitud), "0"));
        latitud = Double.parseDouble(sharedPref.getString(getString(R.string.saved_latitud), "0"));
        diaJuliano = Integer.parseInt(sharedPref.getString(getString(R.string.saved_dia), "1"));
        gmt = Integer.parseInt(sharedPref.getString(getString(R.string.saved_gmt), "0"));
        altitud = Integer.parseInt(sharedPref.getString(getString(R.string.saved_alt), "0"));

        setData(24);

        setHasOptionsMenu(true);

        return view;
    }


    private void setData(int count) {

        grafica.clear();

        ArrayList<Entry> values = new ArrayList<>();


        for (double i = 0; i <count; i=i+(0.01666666667 * constGranularity)) {

            double val = ghicc(i);

            irradianciaCieloClaro.add(val);
            horaArray.add(parseToTime(i));

            values.add(new Entry((float) i, (float) val));




        }

        LineDataSet set1;
        LineDataSet setInclinado;


        if (grafica.getData() != null && grafica.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) grafica.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();

            setInclinado = (LineDataSet) grafica.getData().getDataSetByIndex(1);
            setInclinado.notifyDataSetChanged();

            grafica.getData().notifyDataChanged();
            grafica.notifyDataSetChanged();

        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Modelo Cielo Claro ARGP");

            set1.setDrawIcons(false);
            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            // black lines and points
            set1.setColor(Color.rgb(255,140,0));
            set1.setCircleColor(Color.rgb(255,140,0));
            set1.setFillColor(Color.rgb(255,140,0));
            set1.setCircleHoleColor(Color.rgb(255,140,0));




            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data


            // data has AxisDependency.LEFT
            grafica.getAxisRight().setEnabled(false); // no right axis
            grafica.getAxisLeft().setAxisMaximum(1600);








            XAxis myXAxis = grafica.getXAxis();

            myXAxis.setValueFormatter(new CieloClaroFragment.DateValueFormatter());
            myXAxis.setGranularity((float)(0.016666667*constGranularity));


            YAxis myYAxis = grafica.getAxisLeft();

            myYAxis.setValueFormatter(new CieloClaroFragment.MyYAxisValueFormatter());
            myYAxis.setGranularity(10);

            data.setHighlightEnabled(true);


            grafica.setHighlightPerTapEnabled(true);


            grafica.setData(data);






        }
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








    private double gete(int dia){

        Double result;
        result = 1 + 0.033 *Math.cos(2*Math.PI* (double)dia/365);
        return truncate(result);
    }



    private double getDeclinacion(){
        Double result;
        result = 23.45 * Math.sin(Math.toRadians(360*(284+diaJuliano))/365);
        return result;
    }


    //angulo diario
    private double getAnguloDiario(){
        Double result;
        result = 2*Math.PI * (diaJuliano-1)/365;
        return result;
    }


    private double getEot(){
        //H angulo diario
        Double result = (0.000075+0.001868*Math.cos(getAnguloDiario())-0.032077*Math.sin(getAnguloDiario())-0.014615*Math.cos(2*getAnguloDiario())-0.04089*Math.sin(2*getAnguloDiario()))*229.2;
        return result;

    }

    private double getHoraSolar(double horaReloj){

        Double GMT = Double.valueOf(gmt);
        Double LONG = longitud;

        double A = 1;
        if(GMT<0){
            A = -1;
        }

        Double result;
        //result = horaReloj + (4*((-15 * GMT)-LONG)+getEot())/60;
        result = horaReloj + (4*((A*15 * GMT)-(A*LONG))+getEot())/60;
        return result;
    }







    private double getW(double horaReloj){
        Double result;
        result =  15.00 * (12 - getHoraSolar(horaReloj));
        return result;
    }


    private double getCosTitaZero(double horaReloj){

        //LAT Bien
        Double LAT = latitud;
        //Dec bien
        Double DEC = Math.toRadians(getDeclinacion());
        Double ANG = Math.toRadians(getW(horaReloj));
        Double result;
        //result = (Math.cos(LAT)*Math.cos(DEC)*Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC));
        result = (Math.cos(Math.toRadians(LAT)) * Math.cos(DEC) *Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC)) ;

        return result;
    }

    private double irradianciaPlanoParalelo(double horaReloj){



        if(getCosTitaZero(horaReloj)<0){
            return 0;
        }
        else{
            //return truncate( 1367.00 * gete(DiaJuliano) * getCosTitaZero(13.00));


            double z = 1367.00;
            double e = gete(diaJuliano);
            double o = getCosTitaZero(horaReloj);

            return  truncate(z*e*o);
        }
    }







    private double masaAire(double horaReloj){
        Double cosTitaZ = getCosTitaZero(horaReloj);
        Double med = 0.15 *  Math.pow(93.885 - cosTitaZ, -1.253);
        return 1 / (cosTitaZ + med);
    }

    private double ktr(){


        double ktr = 0.7 + 1.6391 * Math.pow(10,-3) * Math.pow( altitud , 0.5500);
        return  ktr;
    }

    private double ghicc(double horaReloj){
        double med = Math.pow(masaAire(horaReloj), 0.678);
        return irradianciaPlanoParalelo(horaReloj) * Math.pow(ktr(), med);
    }







    private int signo(double val){
        if (val==0){
            return 0;
        }
        else if (val>0){
            return 1;
        }
        else {
            return -1;
        }
    }

    private double truncate(double num){
        return Math.floor(num*100) / 100;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.gran1:{
                constGranularity = 1.0;
                setData(24);
                return true;
            }
            case R.id.gran5:{
                constGranularity = 5.0;
                setData(24);
                return true;
            }
            case R.id.gran10:{
                constGranularity = 10.0;
                setData(24);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public class DateValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return parseToTime(value);
        }



    }


    private class MyYAxisValueFormatter extends ValueFormatter {


        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            //return super.getAxisLabel(value, axis);
            //return value+"Wm2";
            return  (int)value +" Wm\u00B2";
        }


        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value);
        }
    }
}