package com.tallytest.tablayout;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputEditText;
import com.tallytest.tablayout.viewmodel.IrradianciaModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Entrada#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Entrada extends Fragment {


    private TextInputEditText dia, latitudEditText, longitudEditText,gmtEditText, betaEditText, gammaEditText;
    private IrradianciaModel model;
    private LineChart lineChartToa, lineChartCC;



    private double latitud, longitud;
    private int diaJuliano, gmt, beta, gamma;

    public Entrada() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Entrada.
     */
    // TODO: Rename and change types and number of parameters
    public static Entrada newInstance(String param1, String param2) {
        Entrada fragment = new Entrada();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(IrradianciaModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_entrada, container, false);





        setRootView(view);
        listenerText();

        loadGrap();
        //loadGrapCieloClaro();

        return view;
    }

    private void setRootView(View view){
        latitudEditText = view.findViewById(R.id.latitud);
        longitudEditText = view.findViewById(R.id.longitud);
        gmtEditText = view.findViewById(R.id.gmt);
        dia = view.findViewById(R.id.dia);
        betaEditText = view.findViewById(R.id.beta);
        gammaEditText = view.findViewById(R.id.gamma);


        lineChartToa = view.findViewById(R.id.grap_toa);
        lineChartCC = view.findViewById(R.id.grap_toa_cc);

        latitud  =  Double.parseDouble(String.valueOf(latitudEditText.getText()));
        longitud = Double.parseDouble(String.valueOf(longitudEditText.getText()));
        gmt = Integer.parseInt(String.valueOf(gmtEditText.getText()));



        model.getName().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dia.setText(s);
                diaJuliano = Integer.parseInt(String.valueOf(dia.getText()));
                loadGrapCieloClaro();
            }
        });



    }


    private void listenerText(){


        latitudEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


                if(validateInputs()){
                    latitud =  Double.parseDouble(String.valueOf(latitudEditText.getText()));
                    loadGrap();
                    loadGrapCieloClaro();
                }
            }
        });





        longitudEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    longitud =  Double.parseDouble(String.valueOf(longitudEditText.getText()));
                    loadGrap();
                    loadGrapCieloClaro();
                }
            }
        });

        gmtEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    gmt =  Integer.parseInt(String.valueOf(gmtEditText.getText()));
                    loadGrap();
                    loadGrapCieloClaro();
                }
            }
        });

        betaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    beta =  Integer.parseInt(String.valueOf(betaEditText.getText()));
                    loadGrap();
                    loadGrapCieloClaro();
                    Log.d("Change beta", "Change");
                }
            }
        });

        gammaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    gamma =  Integer.parseInt(String.valueOf(gammaEditText.getText()));
                    loadGrap();
                }
            }
        });


    }

    private boolean validateGamma(){
        if(gammaEditText.getText().length() == 0){
            Toast.makeText(getContext(), "Gamma inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double GAMMA =  Double.parseDouble(String.valueOf(gammaEditText.getText()));
            if (GAMMA<-180 || GAMMA>180){
                Toast.makeText(getContext(), "Gamma inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateBeta(){
        if(betaEditText.getText().length() == 0){
            Toast.makeText(getContext(), "Beta inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double BETA =  Double.parseDouble(String.valueOf(betaEditText.getText()));
            if (BETA<0 || BETA>90){
                Toast.makeText(getContext(), "Beta inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }



    private boolean validateLongitud(){
        if(longitudEditText.getText().length() == 0){
            Toast.makeText(getContext(), "Logitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double LONG =  Double.parseDouble(String.valueOf(longitudEditText.getText()));
            if (LONG<-180 || LONG>180){
                Toast.makeText(getContext(), "Longitud inválida", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateLatitud(){
        if(latitudEditText.getText().length() == 0){
            Toast.makeText(getContext(), "Latitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double LAT =  Double.parseDouble(String.valueOf(latitudEditText.getText()));
            if (LAT < -66 || LAT>66){
                Toast.makeText(getContext(), "Latitud inválida", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateGMT(){
        if(gmtEditText.getText().length() == 0){
            Toast.makeText(getContext(), "GMT inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            int GMT =  Integer.parseInt(String.valueOf(gmtEditText.getText()));
            double LONG =  Double.parseDouble (String.valueOf(gmtEditText.getText()));
            if (GMT<-12 || GMT>12){
                Toast.makeText(getContext(), "GMT inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            /*
            if (GMT<0 && LONG>=0 ){
                Toast.makeText(getContext(), "GMT y/o LONG inválidos", Toast.LENGTH_SHORT).show();
                return false;
            } */

            return true;
        }
    }



    /*private boolean validateAltitud(){

        Integer Altitud =  Integer.parseInt(String.valueOf(altitud.getText()));
        if (Altitud< 0 || Altitud>8848){
            Toast.makeText(getContext(), "Altitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }*/


    private boolean validateInputs(){
        if(validateGMT() && validateLatitud() && validateLongitud() && validateBeta() && validateGamma()){
            return true;
        }else{
            return false;
        }
    }


    private void loadGrap() {
        lineChartToa.clear();
        ArrayList<Entry> paraleloValues = new ArrayList<>();
        for (double i = 0; i <24; i=i+(0.01666666667 * 10)) {

            paraleloValues.add(new Entry((float) i, (float) irradianciaPlanoParalelo(i)));
        }


        LineDataSet setParaleo = new LineDataSet(paraleloValues, "Irr. Plano Paralelo");
        setParaleo.setFillAlpha(100);

        ArrayList<Entry> inclinadoValues = new ArrayList<>();
        for (double i = 0; i <24; i=i+(0.01666666667 * 10)) {

            inclinadoValues.add(new Entry((float) i, (float) irradianciaPlanoInclinado(i)));
        }


        LineDataSet setInclinado = new LineDataSet(inclinadoValues, "Irr. Plano Inclinado");
        setInclinado.setLineWidth(2);
        setInclinado.setColor(Color.GRAY);
        setInclinado.setCircleColor(Color.GRAY);
        setInclinado.setFillColor(Color.GRAY);
        setInclinado.setCircleHoleColor(Color.GRAY);
        setInclinado.setFillAlpha(100);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setParaleo);
        dataSets.add(setInclinado);





        LineData data = new LineData(dataSets);

        lineChartToa.setData(data);
    }


    private void loadGrapCieloClaro() {
        lineChartCC.clear();
        ArrayList<Entry> values = new ArrayList<>();

        float latF = (float)latitud;

        Log.d("LAT ", ""+latF);

        for (double i = 0; i <24; i=i+(0.01666666667 * 10)) {
            values.add(new Entry( (float)i , (float) latitud ));
        }

        LineDataSet setValues = new LineDataSet(values, "data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setValues);


        LineData data = new LineData(dataSets);

        lineChartCC.setData(data);



    }


    public double getCosenoTita(double horaReloj){
        double result=0;





        double cosBeta = Math.cos(Math.toRadians(45));
        double senBeta = Math.sin(Math.toRadians(45));

        double titaZero = Math.acos(getCosTitaZero(horaReloj));




        double senTitaZero = Math.sin(titaZero);



        if (getCosTitaZero(horaReloj)<0) {

            return 0;
        }
        else{

            //result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(Gamma);
            result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(getAzimutdelSol(horaReloj) - Math.toRadians(90));

            return result;
        }


    }


    private double getAzimutdelSol(double horaReloj){
        double result;

        double senLatRad = Math.sin(latitud);
        double senDecRad = Math.sin(Math.toRadians(getDeclinacion()));
        double titaZ = Math.acos(getCosTitaZero(horaReloj));


        result = signo(getW(horaReloj)) * Math.abs(Math.acos((getCosTitaZero(horaReloj)* senLatRad -senDecRad)/ Math.sin(titaZ) * Math.cos(latitud)));

        result = signo(getW(horaReloj));

        result = Math.acos(getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latitud)) - Math.sin(Math.toRadians(getDeclinacion())));

        result = (getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latitud) - Math.sin(Math.toRadians(getDeclinacion()))));

        double termino1 = (getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latitud)) - Math.sin(Math.toRadians(getDeclinacion())));
        double termino2 = (Math.sin(titaZ)*Math.cos(Math.toRadians(latitud)));




        result =  signo(getW(horaReloj)) * Math.abs(Math.acos(termino1/termino2));

        return result;
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


        double A = 1;
        if(gmt<0){
            A = -1;
        }

        Double result;
        //result = horaReloj + (4*((-15 * GMT)-LONG)+getEot())/60;
        result = horaReloj + (4*((A*15 * gmt)-(A* longitud))+getEot())/60;
        return result;
    }

    private double mediodiaSolar() {
        return 12 - getHoraSolar(0);
        //return getHoraSolar(0);
    }


        private double getRazonMediodia(){
        double cosTita, cosTitaZ;
        cosTita = getCosenoTita(mediodiaSolar());
        cosTitaZ = getCosTitaZero(mediodiaSolar());
        if(cosTitaZ>0){

            double mediodia = mediodiaSolar();

            return getCosenoTita(truncate(mediodia))/getCosTitaZero(truncate(mediodia));
        }
        return 0;

    }




    private double getW(double horaReloj){
        Double result;
        result =  15.00 * (12 - getHoraSolar(horaReloj));
        return result;
    }


    private double getCosTitaZero(double horaReloj){


        //Dec bien
        Double DEC = Math.toRadians(getDeclinacion());
        Double ANG = Math.toRadians(getW(horaReloj));
        Double result;
        //result = (Math.cos(LAT)*Math.cos(DEC)*Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC));
        result = (Math.cos(Math.toRadians(latitud)) * Math.cos(DEC) *Math.cos(ANG)) + (Math.sin(Math.toRadians(latitud)) * Math.sin(DEC)) ;

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

    private double irradianciaPlanoInclinado(double horaReloj){

        if(getCosTitaZero(horaReloj)<0 || getCosenoTita(horaReloj)<0 ){
            return 0;
        }
        else{
            return truncate( 1367.00 * gete(diaJuliano) * getCosenoTita(horaReloj));
        }
    }



}