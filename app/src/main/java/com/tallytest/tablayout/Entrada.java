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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputEditText;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.tallytest.tablayout.Adapters.GmtAdapter;
import com.tallytest.tablayout.Clases.GmtItem;
import com.tallytest.tablayout.viewmodel.IrradianciaModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Entrada#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Entrada extends Fragment {


    private TextInputEditText dia, latitudEditText, longitudEditText, betaEditText, gammaEditText;
    private Button btnLatitud, btnLongitud, btnGamma;
    private IrradianciaModel model;
    private LineChart lineChartToa, lineChartCC;
    private ArrayList <GmtItem> gmtItems;
    private GmtAdapter gmtAdapter;
    private Integer signoLatitud, signoLongitud, signoGamma, altitud;


    private double latitud, longitud;
    private int diaJuliano, gmt, beta, gamma;

    public Entrada() {
        // Required empty public constructor
    }


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





        return view;
    }


    private void setRootView(View view){

        latitudEditText = view.findViewById(R.id.latitud);
        btnLatitud = view.findViewById(R.id.btnLatitud);


        longitudEditText = view.findViewById(R.id.longitud);
        btnLongitud = view.findViewById(R.id.btnLongitud);

        SeekBar seekBar  = view.findViewById(R.id.altitudSeekBar);
        dia = view.findViewById(R.id.dia);


        betaEditText = view.findViewById(R.id.beta);


        gammaEditText = view.findViewById(R.id.gamma);
        btnGamma = view.findViewById(R.id.btnGamma);

        lineChartToa = view.findViewById(R.id.grap_toa);
        lineChartCC = view.findViewById(R.id.grap_toa_cc);

        signoLatitud = 1;
        signoLongitud = 1;
        signoGamma = 1;

        listenerButtons();
        listenerText();

        latitud  =  Double.parseDouble(String.valueOf(latitudEditText.getText()));
        longitud = Double.parseDouble(String.valueOf(longitudEditText.getText()));
        gmt = 0;
        beta = 0;
        gamma = 0;
        altitud = 0;



        model.getDiaJuliano().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dia.setText(s);
                diaJuliano = Integer.parseInt(String.valueOf(dia.getText()));
                //loadGrapCieloClaro();
                loadGrap();
                loadGrapCieloClaro();
                updateModelObserver();
            }
        });



        initList();

        Spinner spinnerGmt = view.findViewById(R.id.spinner_gmt);
        TextView textAltitud = view.findViewById(R.id.textAltitud);


        gmtAdapter = new GmtAdapter(getContext(), gmtItems);
        spinnerGmt.setAdapter(gmtAdapter);
        spinnerGmt.setSelection(12);

        spinnerGmt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gmt = gmtAdapter.getItem(i).getGmt();
                loadGrap();
                loadGrapCieloClaro();
                updateModelObserver();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValue = progress;
                textAltitud.setText(""+progressChangedValue+" msn");
                altitud = progressChangedValue;
                loadGrapCieloClaro();
                updateModelObserver();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textAltitud.setText(""+progressChangedValue+" msn");
                altitud = progressChangedValue;
                loadGrapCieloClaro();
                updateModelObserver();
            }
        });

    }


    private void initList(){
        gmtItems = new ArrayList<>();
        for (int i=-12; i<=12; i++){
            gmtItems.add(new GmtItem(i));
        }


    }

    /*private void setRootView(View view){

        gmtEditText = view.findViewById(R.id.gmt);
        dia = view.findViewById(R.id.dia);
        betaEditText = view.findViewById(R.id.beta);
        gammaEditText = view.findViewById(R.id.gamma);


        lineChartToa = view.findViewById(R.id.grap_toa);
        lineChartCC = view.findViewById(R.id.grap_toa_cc);


        gmt = Integer.parseInt(String.valueOf(gmtEditText.getText()));








    }*/


    private void updateModelObserver(){
        model.setGmt(""+gmt);
        model.setLatitud(""+latitud);
        model.setLongitud(""+longitud);
        model.setBeta(""+beta);
        model.setGamma(""+gamma);
        model.setAltitud(""+altitud);
    }


    private void listenerButtons(){

        btnLatitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoLatitud = signoLatitud * (-1);
                btnLatitud.setText(((signoLatitud < 0) ? "-" : "+"));
                latitud = latitud * (-1);
                loadGrap();
                updateModelObserver();
            }
        });

        btnLongitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoLongitud = signoLongitud * (-1);
                btnLongitud.setText(((signoLongitud < 0) ? "-" : "+"));
                longitud = longitud * (-1);
                loadGrap();
                updateModelObserver();
            }
        });



        btnGamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoGamma = signoGamma * (-1);
                btnGamma.setText(((signoGamma < 0) ? "-" : "+"));
                gamma = gamma * (-1);
                loadGrap();
                updateModelObserver();
            }
        });






    }



    private void listenerText(){


        latitudEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    latitud =  Double.parseDouble(String.valueOf(latitudEditText.getText())) * signoLatitud;;
                    loadGrap();
                    updateModelObserver();
                    //loadGrapCieloClaro();
                }


            }
        });





        longitudEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    longitud =  Double.parseDouble(String.valueOf(longitudEditText.getText())) * signoLongitud;;
                    loadGrap();
                    updateModelObserver();
                    //loadGrapCieloClaro();
                }


            }
        });



        betaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    beta =  Integer.parseInt(String.valueOf(betaEditText.getText()));
                    loadGrap();
                    updateModelObserver();
                }
            }
        });

        gammaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    gamma =  Integer.parseInt(String.valueOf(gammaEditText.getText())) * signoGamma;
                    loadGrap();
                    loadGrapCieloClaro();
                    updateModelObserver();
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




    /*private boolean validateAltitud(){

        Integer Altitud =  Integer.parseInt(String.valueOf(altitud.getText()));
        if (Altitud< 0 || Altitud>8848){
            Toast.makeText(getContext(), "Altitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }*/


    private boolean validateInputs(){
        if(validateLatitud() && validateLongitud() && validateBeta() && validateGamma()){
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

        dataSets.add(setInclinado);
        dataSets.add(setParaleo);




        LineData data = new LineData(dataSets);

        lineChartToa.setData(data);
    }


    private void loadGrapCieloClaro() {

        Log.d("Ejecuta Beta ", ""+beta);
        lineChartCC.clear();
        ArrayList<Entry> values = new ArrayList<>();


        for (double i = 0; i <24; i=i+(0.01666666667 * 10)) {
            values.add(new Entry( (float)i , (float) ghicc(i) ));
        }

        LineDataSet setValues = new LineDataSet(values, "data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setValues);


        LineData data = new LineData(dataSets);

        lineChartCC.setData(data);



    }


    public double getCosenoTita(double horaReloj){
        double result=0;





        double cosBeta = Math.cos(Math.toRadians(beta));
        double senBeta = Math.sin(Math.toRadians(gamma));

        double titaZero = Math.acos(getCosTitaZero(horaReloj));




        double senTitaZero = Math.sin(titaZero);



        if (getCosTitaZero(horaReloj)<0) {

            return 0;
        }
        else{

            //result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(Gamma);
            result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(getAzimutdelSol(horaReloj) - Math.toRadians(gamma));

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


    private double masaAire(double horaReloj){



        Double cosTitaZ = getCosTitaZero(horaReloj);

        Double titaZ = Math.toDegrees(Math.acos(cosTitaZ)) ;



        Double med = 0.15 *  Math.pow(93.885 - titaZ, -1.253);
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




}