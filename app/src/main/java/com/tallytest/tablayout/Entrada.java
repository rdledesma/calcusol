package com.tallytest.tablayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.DeadObjectException;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tallytest.tablayout.Adapters.GmtAdapter;
import com.tallytest.tablayout.Clases.GmtItem;
import com.tallytest.tablayout.viewmodel.IrradianciaModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Entrada#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Entrada extends Fragment {

    private Double totalGHI;
    private TextInputEditText dia, latitudEditText, longitudEditText, betaEditText, gammaEditText;
    private Button btnLatitud, btnLongitud, btnGamma, btnConfirmar;
    private IrradianciaModel model;
    private LineChart lineChartToa, lineChartCC;
    private ArrayList <GmtItem> gmtItems;
    private GmtAdapter gmtAdapter;
    private Integer signoLatitud, signoLongitud, signoGamma, altitud;
    public Integer gran;
    private EditText editTextExcel;
    String root = Environment.getExternalStorageDirectory().toString();



    private boolean tienePermisoAlmacenamiento = false;

    private static final int CODIGO_PERMISOS_ALMACENAMIENTO = 2;
    private double latitud, longitud;
    private int diaJuliano, gmt, granularidad;

    double beta, gamma;

    private ArrayList<Double> irradianciaSolidario = new ArrayList<Double>();
    private ArrayList<Double> irradianciaInclinado = new ArrayList<Double>();
    private ArrayList<Double> irradianciaCieloClaro = new ArrayList<Double>();
    private ArrayList<String> horaArray = new ArrayList<String>();
    private ArrayList<Double> cosTitaZerorray = new ArrayList<Double>();
    private ArrayList<Double> cosTitaArray = new ArrayList<Double>();
    private ArrayList<Double> ktArray = new ArrayList<Double>();
    private ArrayList<Double> wArray = new ArrayList<Double>();


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
        gran  = 5;
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

        btnConfirmar = view.findViewById(R.id.btnConfirmar);



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
        beta = Double.parseDouble(String.valueOf(betaEditText.getText()));
        gamma = Double.parseDouble(String.valueOf(gammaEditText.getText()));
        altitud = 0;

        model.getGranularity().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                granularidad = integer;
                loadGrap();
                loadGrapCieloClaro();
            }
        });

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
                loadGrap();
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

        ImageView imageView = view.findViewById(R.id.imgDownload);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DownloadActivity.class));
            }
        });


        ImageView imageSave  = view.findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                ExportExcel(currentDateandTime);
            }
        });


        TextView granularity = view.findViewById(R.id.tvGranularity);
        granularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*switch (gran){
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
                }*/

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
                        granularity.setText("1");
                        model.setGranularity(1);
                        gran = 1;
                        break;

                    case 1:
                        granularity.setText("5");
                        model.setGranularity(5);
                        gran = 5;
                        break;
                }

                loadGrap();

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
        model.setMediodiaSolar(""+mediodiaSolar());
        model.setAmanecer(""+getSalidaSol());
        model.setOcaso(""+getPuestaSol());
        model.setDuracion(""+(getPuestaSol()-getSalidaSol()));
        model.setRbMediodia(getRazonMediodia());
    }


    private void listenerButtons(){

        btnLatitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoLatitud = signoLatitud * (-1);
                btnLatitud.setText(((signoLatitud < 0) ? "-" : "+"));
                latitud = latitud * (-1);
                loadGrap();
                loadGrapCieloClaro();
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
                loadGrapCieloClaro();
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


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGrap();
                loadGrapCieloClaro();
                updateModelObserver();
            }
        });



    }



    private void listenerText(){

        /*
        latitudEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    latitud =  Double.parseDouble(String.valueOf(latitudEditText.getText())) * signoLatitud;;
                    loadGrap();
                    loadGrapCieloClaro();
                    updateModelObserver();
                    //loadGrapCieloClaro();
                }


            }
        });*/




        /*
        longitudEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    longitud =  Double.parseDouble(String.valueOf(longitudEditText.getText())) * signoLongitud;;
                    //loadGrap();
                    //loadGrapCieloClaro();
                    //updateModelObserver();
                    //loadGrapCieloClaro();
                }


            }
        });
        */





        /*
        betaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    beta =  Double.parseDouble(String.valueOf(betaEditText.getText()));
                    //loadGrap();
                    //updateModelObserver();
                }
            }
        }); */

        /*
        gammaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    gamma =  Double.parseDouble(String.valueOf(gammaEditText.getText())) * signoGamma;
                    //loadGrap();
                    //updateModelObserver();
                }
            }
        });*/


        latitudEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(validateInputs()){
                    latitud =  Double.parseDouble(String.valueOf(latitudEditText.getText())) * signoLatitud;;
                }
            }
        });

        longitudEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(validateInputs()){
                    longitud =  Double.parseDouble(String.valueOf(longitudEditText.getText())) * signoLongitud;;
                }
            }
        });


        betaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(validateInputs()){
                    beta =  Double.parseDouble(String.valueOf(betaEditText.getText()));
                }
            }
        });

        gammaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(validateInputs()){
                    gamma =  Double.parseDouble(String.valueOf(gammaEditText.getText())) * signoGamma;
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



    private boolean validateInputs(){
        if(validateLatitud() && validateLongitud() && validateBeta() && validateGamma()){
            return true;
        }else{
            return false;
        }
    }


    private void loadGrap() {
        Double sumInclinado = 0.0;
        Double sumParalelo = 0.0;
        Double sumArgp = 0.0;

        horaArray.clear();
        cosTitaZerorray.clear();
        cosTitaArray.clear();
        irradianciaSolidario.clear();
        irradianciaInclinado.clear();
        irradianciaCieloClaro.clear();
        ktArray.clear();
        wArray.clear();

        lineChartToa.clear();
        ArrayList<Entry> paraleloValues = new ArrayList<>();
        for (double i = 0; i <24; i=i+(0.01666666667 * granularidad)) {


            double mHora = i;
            if (granularidad>1){
                mHora = (i + (0.01666666667 * granularidad/2));
            }

            horaArray.add(""+mHora);
            wArray.add(getW(mHora));
            double irra = irradianciaPlanoParalelo(mHora);
            cosTitaZerorray.add(getCosTitaZ(mHora));
            irradianciaSolidario.add(irra);
            paraleloValues.add(new Entry((float) mHora, (float) irradianciaPlanoParalelo(mHora)));
            sumParalelo = sumParalelo + irradianciaPlanoParalelo(mHora);
        }


        LineDataSet setParaleo = new LineDataSet(paraleloValues, "Irr. Plano Paralelo");
        setParaleo.setLineWidth(2);
        setParaleo.setColor(Color.BLUE);
        setParaleo.setCircleColor(Color.BLUE);
        setParaleo.setFillColor(Color.BLUE);
        setParaleo.setCircleHoleColor(Color.BLUE);
        setParaleo.setFillAlpha(100);

        ArrayList<Entry> inclinadoValues = new ArrayList<>();
        for (double i = 0; i <24; i=i+(0.01666666667 * granularidad)) {
            double mHora = i;
            if (granularidad>1){
                mHora = (i + (0.01666666667 * granularidad/2));
            }


            irradianciaInclinado.add(irradianciaPlanoInclinado(mHora));
            cosTitaArray.add(getCosenoTita(mHora));

            inclinadoValues.add(new Entry((float) mHora, (float) irradianciaPlanoInclinado(mHora)));
            sumInclinado = sumInclinado + irradianciaPlanoInclinado(mHora);



        }



        totalGHI = 0.0;
        ArrayList<Entry> ghiccValues = new ArrayList<>();
        for (double i = 0; i <24; i=i+(0.01666666667 * granularidad)) {


            double mHora = i;
            if (granularidad>1){
                mHora = (i + (0.01666666667 * granularidad/2));
            }




            ghiccValues.add(new Entry((float) mHora, (float) ghicc(mHora)));


            Float mGHIcc =  (float) ghicc(mHora);

            String mSum = ""+mGHIcc ;





            if(mSum.equals("NaN")){
                sumArgp = sumArgp + 0;
                irradianciaCieloClaro.add(0.0);

            }
            else{
                sumArgp = sumArgp + mGHIcc;
                irradianciaCieloClaro.add(ghicc(mHora));

                if(mGHIcc>=0){
                    totalGHI = totalGHI +   mGHIcc;
                }


            }

        }



        LineDataSet setGHIcc = new LineDataSet(ghiccValues, "GHI CC");
        setGHIcc.setLineWidth(4);
        setGHIcc.setColor(Color.parseColor("Green"));
        setGHIcc.setCircleColor(Color.GREEN);
        setGHIcc.setFillColor(Color.GREEN);
        setGHIcc.setCircleHoleColor(Color.GREEN);
        setGHIcc.setFillAlpha(10);


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
        dataSets.add(setGHIcc);



        LineData data = new LineData(dataSets);

        lineChartToa.setData(data);
        lineChartToa.getAxisRight().setEnabled(false); // no right axis
        lineChartToa.getAxisLeft().setAxisMaximum(1500);

        YAxis myYAxis = lineChartToa.getAxisLeft();
        myYAxis.setValueFormatter(new MyYAxisValueFormatter());

        Double razonSobrePlanos = 0.0;
        razonSobrePlanos = sumParalelo == 0 ? 0 : sumInclinado/sumParalelo;
        model.setRazonI(razonSobrePlanos);


        Double kt =0.0;
        kt = sumArgp/sumParalelo;
        model.setKt(kt);

        model.setIrradiacion(totalGHI/1000/(60/gran));


        int n = irradianciaSolidario.size()-1;

        for (int i=0; i<=n; i++){
            if(irradianciaSolidario.get(i)!=0){
                Double result = irradianciaCieloClaro.get(i) / irradianciaSolidario.get(i);
                ktArray.add(result);
            }
        }


    }


    private void loadGrapCieloClaro() {

        lineChartCC.clear();
        ArrayList<Entry> values = new ArrayList<>();


        for (double i = 0; i <24; i=i+(0.01666666667 * granularidad)) {
            values.add(new Entry( (float)i , (float) ghicc(i) ));
        }

        LineDataSet setValues = new LineDataSet(values, "GHI Cielco Claro");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setValues);


        LineData data = new LineData(dataSets);

        lineChartCC.setData(data);
        lineChartCC.getAxisRight().setEnabled(false); // no right axis
        lineChartCC.getAxisLeft().setAxisMaximum(1500);
        YAxis myYAxis = lineChartCC.getAxisLeft();
        myYAxis.setValueFormatter(new MyYAxisValueFormatter());


    }


    public double
    getCosenoTita(double horaReloj){
        double result=0;


        Double mBeta = Double.valueOf(beta);
        Double mGamma = Double.valueOf(gamma);


        double cosBeta = Math.cos(Math.toRadians(mBeta));
        double senBeta = Math.sin(Math.toRadians(mBeta));
        double titaZero = Math.acos(getCosTitaZ(horaReloj));
        double senTitaZero = Math.sin(titaZero);



        if (getCosTitaZ(horaReloj)<0) {

            return 0;
        }
        else{

            //result = getCosTitaZ(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(Gamma);
            result = getCosTitaZ(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(getAzimutdelSol(horaReloj) - Math.toRadians(mGamma));

            return result;
        }


    }


    private double getAzimutdelSol(double horaReloj){
        double result;

        double senLatRad = Math.sin(latitud);
        double senDecRad = Math.sin(Math.toRadians(getDeclinacion()));
        double titaZ = Math.acos(getCosTitaZ(horaReloj));


        result = signo(getW(horaReloj)) * Math.abs(Math.acos((getCosTitaZ(horaReloj)* senLatRad -senDecRad)/ Math.sin(titaZ) * Math.cos(latitud)));

        result = signo(getW(horaReloj));

        result = Math.acos(getCosTitaZ(horaReloj)*Math.sin(Math.toRadians(latitud)) - Math.sin(Math.toRadians(getDeclinacion())));

        result = (getCosTitaZ(horaReloj)*Math.sin(Math.toRadians(latitud) - Math.sin(Math.toRadians(getDeclinacion()))));

        double termino1 = (getCosTitaZ(horaReloj)*Math.sin(Math.toRadians(latitud)) - Math.sin(Math.toRadians(getDeclinacion())));
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

        Double mGmt = Double.valueOf(gmt);

        Double A = 1.0;

        if(mGmt<0){
            A = -1.0;
        }

        Double result;
        //result = horaReloj + (4*((-15 * GMT)-LONG)+getEot())/60;
        result = horaReloj + (4*((A*15 * mGmt   )-(A * longitud))+getEot())/60;
        return result;
    }




    private double mediodiaSolar() {


        Double LUTC = 15.00 * Double.valueOf(gmt);
        Double L0 =  Double.valueOf(longitud);

        Double correcion = (LUTC - L0) / 15;

        Double TUTC = 12 + correcion - getEot()/60 ;

        return TUTC;

    }



        private double getRazonMediodia(){
        double cosTita, cosTitaZ;
        cosTita = getCosenoTita(mediodiaSolar());
        cosTitaZ = getCosTitaZ(mediodiaSolar());
        if(cosTitaZ>0){

            double mediodia = mediodiaSolar();

            return getCosenoTita(truncate(mediodia))/getCosTitaZ(truncate(mediodia));
        }
        return 0;

    }




    private double getW(double horaReloj){
        Double result;
        result =  15.00 * (12 - getHoraSolar(horaReloj));
        return result;
    }


    private double getCosTitaZ(double horaReloj){


        //Dec bien
        Double DEC = Math.toRadians(getDeclinacion());

        //Angulo MAL
        Double ANG = Math.toRadians(getW(horaReloj));



        Double COSLAT =  Math.cos(Math.toRadians(latitud));
        Double SENLAT =  Math.sin(Math.toRadians(latitud));
        Double result;
        //result = (Math.cos(LAT)*Math.cos(DEC)*Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC));
        //result = (Math.cos(Math.toRadians(latitud)) * Math.cos(DEC) *Math.cos(ANG)) + (Math.sin(Math.toRadians(latitud)) * Math.sin(DEC)) ;
        result = (COSLAT * Math.cos(DEC) * Math.cos(ANG))  + ( SENLAT  * Math.sin(DEC) ) ;

        return result;
    }

    private double irradianciaPlanoParalelo(double horaReloj){


        if(getCosTitaZ(horaReloj)<0){
            return 0;
        }
        else{
            //return truncate( 1367.00 * gete(DiaJuliano) * getCosTitaZ(13.00));


            double z = 1367.00;
            double e = gete(diaJuliano);
            double o = getCosTitaZ(horaReloj);



            return  truncate(z*e*o);
        }
    }

    private double irradianciaPlanoInclinado(double horaReloj){
        if(getCosTitaZ(horaReloj)<0 || getCosenoTita(horaReloj)<0 ){
            return 0;
        }
        else{
            return 1367.00 * gete(diaJuliano) * getCosenoTita(horaReloj);
        }
    }





    private double masaAire(double horaReloj){

        Double A  = Double.valueOf(altitud);

        Double presion =  Math.pow(288.15/(288.15 - 0.0065 * A), -5.255877);

        Double cosTitaZ = getCosTitaZ(horaReloj);
        Double titaZ = Math.toDegrees(Math.acos(cosTitaZ)) ;

        Double AMk = 1/(cosTitaZ + 0.15 * Math.pow((93.885-  titaZ ), -1.253));

        Double AMc = AMk * presion;


        return AMc;
    }

    private double masAireKastenYoung(double horaReloj){
        Double A  = Double.valueOf(altitud);
        Double cosTitaZ = getCosTitaZ(horaReloj);
        Double titaZ = Math.toDegrees(Math.acos(cosTitaZ)) ;

        Double AMky = (Math.exp(-0.0001184 *A))/(cosTitaZ + 0.5057 * Math.pow(( 96.080 - titaZ ), -1.634));
        Double presion =  288.15/(288.15 - 0.0065 * A);
        return AMky;
    }





    private double ktr(){

        Double A = Double.valueOf(altitud);
        double ktrp;
        if(A< 1000.00){
            ktrp = 0.7570 + 1.0112 * Math.pow(10,-5) * Math.pow( A , 1.1067);
        }
        else{
            ktrp = 0.7 + 1.6391 * Math.pow(10,-3) * Math.pow( A , 0.5500);
        }
        return ktrp;
    }


    private double ghicc(double horaReloj){

        Double result = 0.0;
        result = irradianciaPlanoParalelo(horaReloj) * Math.pow(ktr(), Math.pow(masaAire(horaReloj), 0.678));



        return result;
        //return irradianciaPlanoParalelo(horaReloj) * Math.pow(ktr(), Math.pow(masAireKastenYoung(horaReloj), 0.678));





    }



    private double getSalidaSol(){

        Double diaJ= 0.0 + diaJuliano;
        Double LUTC = 15.00 * gmt;
        Double L0 = longitud;
        Double declinacion = Math.toRadians(23.45 * Math.sin(Math.toRadians(360*(284+diaJ))/365));


        Double correcion = (LUTC - L0) / 15;

        //Double wss = Math.acos(- Math.tan(getDeclinacion()) * Math.tan( Math.toRadians(latitud) )) * 180 / Math.PI;
        //Double ws = Math.acos(-Math.tan( getDeclinacion() ) * (Math.tan(Math.toRadians(-34.90395))));
        Double ws = -Math.acos(-(Math.tan(declinacion)) * (Math.tan(Math.toRadians(latitud))));



        Double TUTC = 12 * (1 + (ws/Math.PI))  + correcion - getEot()/60;

        return TUTC;

    }

    private double getPuestaSol(){

        Double diaJ= 0.0 + diaJuliano;
        Double LUTC = 15.00 * gmt;
        Double L0 = longitud;
        Double declinacion = Math.toRadians(23.45 * Math.sin(Math.toRadians(360*(284+diaJ))/365));


        Double correcion = (LUTC - L0) / 15;

        //Double wss = Math.acos(- Math.tan(getDeclinacion()) * Math.tan( Math.toRadians(latitud) )) * 180 / Math.PI;
        //Double ws = Math.acos(-Math.tan( getDeclinacion() ) * (Math.tan(Math.toRadians(-34.90395))));
        Double ws = Math.acos(-(Math.tan(declinacion)) * (Math.tan(Math.toRadians(latitud))));



        Double TUTC = 12 * (1 + (ws/Math.PI))  + correcion - getEot()/60;

        return TUTC;

    }






    public void ExportExcel(String name){

        final File dir;
        if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
            dir = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//CALCUSOL");
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath()
                    + "//CALCUSOL");
        }

        if (!dir.exists())
            dir.mkdir();




        File filePath = new File( dir.getPath()+"/"+name+".xls");


        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("Custom Sheet");

        HSSFRow rowHeader = hssfSheet.createRow(0);


        HSSFCell horaReloj = rowHeader.createCell(0);
        horaReloj.setCellValue("Hora Reloj (inicio del intervalo)");

        HSSFCell hora = rowHeader.createCell(1);
        hora.setCellValue("Hora");

        HSSFCell cellWs = rowHeader.createCell(2);
        cellWs.setCellValue("w");


        HSSFCell cellCosTitaZero = rowHeader.createCell(3);
        cellCosTitaZero.setCellValue("Cos Tita Zero");
        HSSFCell cellCosTita= rowHeader.createCell(4);
        cellCosTita.setCellValue("Cos Tita");

        HSSFCell cellIrradianciaSolidaria= rowHeader.createCell(5);
        cellIrradianciaSolidaria.setCellValue("Irrad. plano paralelo");

        HSSFCell cellIrradianciaInclinado= rowHeader.createCell(6);
        cellIrradianciaInclinado.setCellValue("Irrad. plano inclinado");

        HSSFCell cellIrradianciaCC= rowHeader.createCell(7);
        cellIrradianciaCC.setCellValue("Irrad. cc");

        ArrayList<String> HoraRelojArray = new ArrayList<String>();


        for(int i=0; i< 24 * (60/granularidad) ; i++){
            HoraRelojArray.add(ConvertMinutesTimeToHHMMString(i*granularidad));
        }



        for (int i = 1; i <irradianciaSolidario.size()+1; i=i+1) {
            HSSFRow hssfRow2 = hssfSheet.createRow(i);

            HSSFCell horaRelojCell = hssfRow2.createCell(0);
            HSSFCell horaCell = hssfRow2.createCell(1);
            HSSFCell w = hssfRow2.createCell(2);
            HSSFCell cosTitaZero = hssfRow2.createCell(3);
            HSSFCell cosTita = hssfRow2.createCell(4);
            HSSFCell irradianciaSolidaria = hssfRow2.createCell(5);
            HSSFCell irradianciaInclinada = hssfRow2.createCell(6);
            HSSFCell irradianciaCC = hssfRow2.createCell(7);




            horaCell.setCellValue(horaArray.get(i-1));
            w.setCellValue(wArray.get(i-1));
            irradianciaSolidaria.setCellValue(irradianciaSolidario.get(i-1));
            cosTitaZero.setCellValue(cosTitaZerorray.get(i-1));
            cosTita.setCellValue(cosTitaArray.get(i-1));
            irradianciaInclinada.setCellValue(irradianciaInclinado.get(i-1));
            irradianciaCC.setCellValue(irradianciaCieloClaro.get(i-1));
            horaRelojCell.setCellValue(HoraRelojArray.get(i-1));


        }


        try {
            if (!filePath.exists()){
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream= new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(getContext(), "Guardado en CALCUSOl/"+name, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_ALMACENAMIENTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeAlmacenamientoConcedido();
                } else {
                    permisoDeAlmacenamientoDenegado();
                }
                break;

            // Aquí más casos dependiendo de los permisos
            // case OTRO_CODIGO_DE_PERMISOS...

        }
    }

    private void permisoDeAlmacenamientoConcedido() {
        // Aquí establece las banderas o haz lo que
        // ibas a hacer cuando el permiso se concediera. Por
        // ejemplo puedes poner la bandera en true y más
        // tarde en otra función comprobar esa bandera
        Toast.makeText(getContext(), "El permiso para el almacenamiento está concedido",
                Toast.LENGTH_SHORT).show();
        tienePermisoAlmacenamiento = true;
    }

    private void permisoDeAlmacenamientoDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(getContext(), "El permiso para el almacenamiento está denegado", Toast.LENGTH_SHORT).show();
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

    public static String ConvertMinutesTimeToHHMMString(int minutesTime) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(timeZone);
        String time = df.format(new Date(minutesTime * 60 * 1000L));

        return time;
    }

}