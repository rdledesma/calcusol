package com.example.calcusol;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.calcusol.ui.ExportModal;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.Duration;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IrradianciaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IrradianciaFragment extends Fragment implements ExportModal.OnInputSelected{



    private double constGranularity = 1;
    private static List<Object> DATA;
    private Double I, DECLINACION, EOT;
    private int DiaJuliano;
    private TextInputEditText diaJuliano, gmt, longitud, latitud, beta, gamma;
    private LineChart grafica;
    private LineGraphSeries seriePlanoParalelo;
    private TextView debugCosTita, debugCosTitaZero;

    private EditText editTextExcel;
    String root = Environment.getExternalStorageDirectory().toString();



    private boolean tienePermisoAlmacenamiento = false;

    private static final int CODIGO_PERMISOS_ALMACENAMIENTO = 2;

    private ArrayList<Double> irradianciaSolidario = new ArrayList<Double>();
    private ArrayList<Double> irradianciaInclinado = new ArrayList<Double>();
    private ArrayList<Double> costTitaZero = new ArrayList<Double>();
    private ArrayList<Double> costTita = new ArrayList<Double>();
    private ArrayList<String> horaArray = new ArrayList<String>();


    public IrradianciaFragment() {
        // Required empty public constructor
    }

    public static IrradianciaFragment newInstance(String param1, String param2) {
        IrradianciaFragment fragment = new IrradianciaFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irradiancia, container, false);



        diaJuliano = view.findViewById(R.id.juliano);
        gmt = view.findViewById(R.id.gmt);
        longitud = view.findViewById(R.id.longitud);
        latitud = view.findViewById(R.id.latitud);
        beta = view.findViewById(R.id.beta);
        gamma = view.findViewById(R.id.gamma);

        debugCosTitaZero = view.findViewById(R.id.debugCosTitaZero);
        debugCosTita = view.findViewById(R.id.debugCosTita);
        grafica = view.findViewById(R.id.grafica);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String highScore = sharedPref.getString(getString(R.string.saved_high_score_key), "1");
        String longitudSaved = sharedPref.getString(getString(R.string.saved_longitud), "0");
        String latitudSaved = sharedPref.getString(getString(R.string.saved_latitud), "0");
        String gmtSaved = sharedPref.getString(getString(R.string.saved_gmt), "0");

        diaJuliano.setText(highScore);
        gmt.setText(gmtSaved);
        latitud.setText(latitudSaved);
        longitud.setText(longitudSaved);
        beta.setText("0");
        gamma.setText("0");
        //declinacion = view.findViewById(R.id.declinancion);
        //eot = view.findViewById(R.id.eot);
        // Enlazamos al XML



        setDataParalelo(24);

        diaJuliano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_high_score_key), ""+diaJuliano.getText());
                    editor.commit();
                }

            }
        });






        gmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_gmt), ""+gmt.getText());
                    editor.commit();
                }

            }
        });

       /* latitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                }

            }
        });
        */

        latitud.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_latitud), ""+latitud.getText());
                    editor.commit();
                }

            }
        });



        longitud.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.saved_longitud), ""+longitud.getText());
                    editor.commit();
                }


            }
        });


        /*longitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                }

            }
        });*/

        beta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                }

            }
        });

        gamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsIrradiancia()){
                    setDataParalelo(24);
                }

            }
        });










        //buttonCreateExcel(view);
        setHasOptionsMenu(true);
        return  view;
    }




    private void setDataParalelo(int count) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        grafica.clear();

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> valuesInclinado = new ArrayList<>();

        double razonIrradiancia = 0;
        double sumInclinado=0;
        double sumParalelo=0;

        Double LONG =  Double.parseDouble(String.valueOf(longitud.getText()));


        for (double i = 0; i <count; i=i+(0.01666666667 * constGranularity)) {

            double val = irradianciaPlanoParalelo(i);
            double valInclinado = irradianciaPlanoInclinado(i);
            irradianciaSolidario.add(val);
            irradianciaInclinado.add(valInclinado);
            costTita.add(getCosenoTita(i));
            costTitaZero.add(getCosTitaZero(i));
            horaArray.add(parseToTime(i));

            valuesInclinado.add(new Entry((float)i, (float) valInclinado));
            values.add(new Entry((float) i, (float) val));

            sumInclinado = sumInclinado + valInclinado;
            sumParalelo = sumParalelo + val;


            if(LONG>=0){
                if(getCosTitaZero(i)<0 && getCosTitaZero(i+(0.01666666667 * constGranularity))>=0){
                    editor.putString(getString(R.string.saved_amanercer), ""+ i);
                }

                if(getCosTitaZero(i)>0 && getCosTitaZero(i+(0.01666666667 * constGranularity))<=0){
                    editor.putString(getString(R.string.saved_ocaso), ""+ i);
                }
            }
            else{
                if(getCosTitaZero(i)>0 && getCosTitaZero(i+(0.01666666667 * constGranularity))<=0){
                    editor.putString(getString(R.string.saved_amanercer), ""+i);
                }
                if(getCosTitaZero(i)<0 && getCosTitaZero(i+(0.01666666667 * constGranularity))>=0){
                    editor.putString(getString(R.string.saved_ocaso), ""+i);
                }
            }







        }

        LineDataSet set1;
        LineDataSet setInclinado;


        if (grafica.getData() != null && grafica.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) grafica.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();

            setInclinado = (LineDataSet) grafica.getData().getDataSetByIndex(1);
            setInclinado.setValues(valuesInclinado);
            setInclinado.notifyDataSetChanged();

            grafica.getData().notifyDataChanged();
            grafica.notifyDataSetChanged();

        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Plano paralelo");
            setInclinado = new LineDataSet(valuesInclinado, "Plano Inclinado");

            set1.setDrawIcons(false);
            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            // black lines and points
            set1.setColor(Color.rgb(255,140,0));
            set1.setCircleColor(Color.rgb(255,140,0));
            set1.setFillColor(Color.rgb(255,140,0));
            set1.setCircleHoleColor(Color.rgb(255,140,0));

            setInclinado.setLineWidth(2);
            setInclinado.setColor(Color.GRAY);
            setInclinado.setCircleColor(Color.GRAY);
            setInclinado.setFillColor(Color.GRAY);
            setInclinado.setCircleHoleColor(Color.GRAY);


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            dataSets.add(setInclinado);
            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data


            // data has AxisDependency.LEFT
            grafica.getAxisRight().setEnabled(false); // no right axis
            grafica.getAxisLeft().setAxisMaximum(1600);




            XAxis myXAxis = grafica.getXAxis();

            myXAxis.setValueFormatter(new DateValueFormatter());
            myXAxis.setGranularity((float)(0.016666667*constGranularity));






            YAxis myYAxis = grafica.getAxisLeft();

            myYAxis.setValueFormatter(new MyYAxisValueFormatter());
            myYAxis.setGranularity(10);

            data.setHighlightEnabled(true);


            grafica.setHighlightPerTapEnabled(true);


            grafica.setData(data);

            CustomMarkerView mv = new CustomMarkerView (getContext(), R.layout.custom_marker_view_layout);
            grafica.setMarker(mv);

            razonIrradiancia = sumParalelo == 0 ? 0 : sumInclinado/sumParalelo;

            editor.putString(getString(R.string.saved_razon_irradiancia), ""+razonIrradiancia);
            editor.putString(getString(R.string.saved_mediodia), parseToTime(mediodiaSolar()));
            editor.putString(getString(R.string.saved_rb), ""+ truncate(getRazonMediodia()));
            editor.putString(getString(R.string.saved_max_altura), ""+ truncate(maxAlturaSolar()));


            editor.commit();




        }
    }

    public class CustomMarkerView extends MarkerView{

        private TextView tvContent;
        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText("Rb " +  truncate(getCosenoTita(e.getX())/getCosTitaZero(e.getX())));

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }



    private boolean validateGamma(){
        if(gamma.getText().length() == 0){
            Toast.makeText(getContext(), "Gamma inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double GAMMA =  Double.parseDouble(String.valueOf(gamma.getText()));
            if (GAMMA<-180 || GAMMA>180){
                Toast.makeText(getContext(), "Gamma inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateBeta(){
        if(beta.getText().length() == 0){
            Toast.makeText(getContext(), "Beta inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double BETA =  Double.parseDouble(String.valueOf(beta.getText()));
            if (BETA<0 || BETA>90){
                Toast.makeText(getContext(), "Beta inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }



    private boolean validateLongitud(){
        if(longitud.getText().length() == 0){
            Toast.makeText(getContext(), "Logitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double LONG =  Double.parseDouble(String.valueOf(longitud.getText()));
            if (LONG<-180 || LONG>180){
                Toast.makeText(getContext(), "Longitud inválida", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateLatitud(){
        if(latitud.getText().length() == 0){
            Toast.makeText(getContext(), "Latitud inválida", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            Double LAT =  Double.parseDouble(String.valueOf(latitud.getText()));
            if (LAT < -66 || LAT>66){
                Toast.makeText(getContext(), "Latitud inválida", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private boolean validateGMT(){
        if(gmt.getText().length() == 0){
            Toast.makeText(getContext(), "GMT inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            int GMT =  Integer.parseInt(String.valueOf(gmt.getText()));
            double LONG =  Double.parseDouble (String.valueOf(gmt.getText()));
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


    private boolean validateDiaJuliano(){
        if(diaJuliano.getText().length() == 0){
            Toast.makeText(getContext(), "Día juliano inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
            if (DiaJuliano<= 0 || DiaJuliano>366){
                Toast.makeText(getContext(), "Día juliano inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }


    private boolean validateInputsIrradiancia(){
        if(validateDiaJuliano() && validateGMT() && validateLatitud() && validateLongitud() && validateBeta() && validateGamma()){
            return true;
        }else{
            return false;
        }
    }

    private double gete(int dia){

        Double result;
        result = 1 + 0.033 *Math.cos(2*Math.PI* (double)dia/365);
        return truncate(result);
    }



    private double getDeclinacion(){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        Double result;
        result = 23.45 * Math.sin(Math.toRadians(360*(284+DiaJuliano))/365);



        return result;
    }


    //angulo diario
    private double getAnguloDiario(){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        Double result;
        result = 2*Math.PI * (DiaJuliano-1)/365;
        return result;
    }


    private double getEot(){
        //H angulo diario
         Double result = (0.000075+0.001868*Math.cos(getAnguloDiario())-0.032077*Math.sin(getAnguloDiario())-0.014615*Math.cos(2*getAnguloDiario())-0.04089*Math.sin(2*getAnguloDiario()))*229.2;
        return result;

    }

    private double getHoraSolar(double horaReloj){

        Double GMT = Double.valueOf(String.valueOf((gmt.getText())));
        Double LONG = Double.valueOf(String.valueOf((longitud.getText())));

        double A = 1;
        if(GMT<0){
            A = -1;
        }


        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        Double result;
        //result = horaReloj + (4*((-15 * GMT)-LONG)+getEot())/60;
        result = horaReloj + (4*((A*15 * GMT)-(A*LONG))+getEot())/60;
        return result;
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
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        Double result;
        result =  15.00 * (12 - getHoraSolar(horaReloj));
        return result;
    }


    private double getCosTitaZero(double horaReloj){

        //LAT Bien
        Double LAT = Double.valueOf(String.valueOf((latitud.getText())));
        //Dec bien
        Double DEC = Math.toRadians(getDeclinacion());
        Double ANG = Math.toRadians(getW(horaReloj));
        Double result;
        //result = (Math.cos(LAT)*Math.cos(DEC)*Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC));
        result = (Math.cos(Math.toRadians(LAT)) * Math.cos(DEC) *Math.cos(ANG)) + (Math.sin(Math.toRadians(LAT)) * Math.sin(DEC)) ;

        return result;
    }

    private double irradianciaPlanoParalelo(double horaReloj){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));


        if(getCosTitaZero(horaReloj)<0){
            return 0;
        }
        else{
            //return truncate( 1367.00 * gete(DiaJuliano) * getCosTitaZero(13.00));


            double z = 1367.00;
            double e = gete(DiaJuliano);
            double o = getCosTitaZero(horaReloj);

            return  truncate(z*e*o);
        }
    }

    private double irradianciaPlanoInclinado(double horaReloj){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));

        if(getCosTitaZero(horaReloj)<0 || getCosenoTita(horaReloj)<0 ){
            return 0;
        }
        else{
            return truncate( 1367.00 * gete(DiaJuliano) * getCosenoTita(horaReloj));
        }
    }


    public double getCosenoTita(double horaReloj){
        double result=0;

        double Gamma =  Double.valueOf(String.valueOf((gamma.getText())));

        double Beta = Double.valueOf(String.valueOf((beta.getText())));



        double cosBeta = Math.cos(Math.toRadians(Beta));
        double senBeta = Math.sin(Math.toRadians(Beta));

        double titaZero = Math.acos(getCosTitaZero(horaReloj));




        double senTitaZero = Math.sin(titaZero);



        if (getCosTitaZero(horaReloj)<0) {

            return 0;
        }
        else{

            //result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(Gamma);
            result = getCosTitaZero(horaReloj) * cosBeta + senTitaZero * senBeta * Math.cos(getAzimutdelSol(horaReloj) - Math.toRadians(Gamma) );

            return result;
        }


    }


    private double getAzimutdelSol(double horaReloj){
        double result;

        double latRad = Double.valueOf(String.valueOf((latitud.getText())));
        double senLatRad = Math.sin(latRad);
        double senDecRad = Math.sin(Math.toRadians(getDeclinacion()));
        double titaZ = Math.acos(getCosTitaZero(horaReloj));


        result = signo(getW(horaReloj)) * Math.abs(Math.acos((getCosTitaZero(horaReloj)* senLatRad -senDecRad)/ Math.sin(titaZ) * Math.cos(latRad)));

        result = signo(getW(horaReloj));

        result = Math.acos(getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latRad)) - Math.sin(Math.toRadians(getDeclinacion())));

        result = (getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latRad) - Math.sin(Math.toRadians(getDeclinacion()))));

        double termino1 = (getCosTitaZero(horaReloj)*Math.sin(Math.toRadians(latRad)) - Math.sin(Math.toRadians(getDeclinacion())));
        double termino2 = (Math.sin(titaZ)*Math.cos(Math.toRadians(latRad)));




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

    /*private double getSalidaSol(){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        double LAT = -36.03;
        double LONG = 59.1;
        double LONGOBS = 60;
        double result ,resultParcial, resultRad;
        //result = Math.acos(-Math.tan(LAT)* Math.tan(getDeclinacion()));
        resultRad =  Math.toRadians(-Math.tan(LAT)* Math.tan(-getDeclinacion()));
        resultParcial = Math.acos(resultRad);
        resultParcial =  truncate(12- Math.toDegrees(resultParcial)/15);

        double resultts = 12 - resultParcial;



        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG)/60) + gete(DiaJuliano)/60;



        return resulttsof;

    }


    private double getPuestaSol(){
        DiaJuliano =  Integer.parseInt(String.valueOf(diaJuliano.getText()));
        double LAT = -36.03;
        double LONG = 59.1;
        double LONGOBS = 60;
        double result ,resultParcial, resultRad;
        //result = Math.acos(-Math.tan(LAT)* Math.tan(getDeclinacion()));
        resultRad =  Math.toRadians(-Math.tan(LAT)* Math.tan(-getDeclinacion()));
        resultParcial = Math.acos(resultRad);
        resultParcial =  truncate(12- Math.toDegrees(resultParcial)/15);

        double resultts = 12 + resultParcial;



        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG)/60) + gete(DiaJuliano)/60;



        return resulttsof;

    }*/


   /* private double duracionDia(){
        return getPuestaSol() - getSalidaSol();
    }*/


    private double mediodiaSolar(){
        return 12-getHoraSolar(0);
        //return getHoraSolar(0);
    }


    private double maxAlturaSolar(){
        return 90 - Math.toDegrees(Math.acos(getCosTitaZero(mediodiaSolar())));
    }


    public void buttonCreateExcel(String name){

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

        HSSFCell hora = rowHeader.createCell(0);
        hora.setCellValue("Hora");

        HSSFCell cellCosTitaZero = rowHeader.createCell(1);
        cellCosTitaZero.setCellValue("Cos Tita Zero");


        HSSFCell cellCosTita= rowHeader.createCell(2);
        cellCosTita.setCellValue("Cos Tita");

        HSSFCell cellIrradianciaSolidaria= rowHeader.createCell(3);
        cellIrradianciaSolidaria.setCellValue("Irrad. plano paralelo");

        HSSFCell cellIrradianciaInclinado= rowHeader.createCell(4);
        cellIrradianciaInclinado.setCellValue("Irrad. plano inclinado");

        for (int i = 1; i <irradianciaSolidario.size()+1; i=i+1) {
            HSSFRow hssfRow2 = hssfSheet.createRow(i);


            HSSFCell horaCell = hssfRow2.createCell(0);
            HSSFCell cosTitaZero = hssfRow2.createCell(1);
            HSSFCell cosTita = hssfRow2.createCell(2);
            HSSFCell irradianciaSolidaria = hssfRow2.createCell(3);
            HSSFCell irradianciaInclinada = hssfRow2.createCell(4);


            horaCell.setCellValue(horaArray.get(i-1));
            cosTitaZero.setCellValue(costTitaZero.get(i-1));
            cosTita.setCellValue(costTita.get(i-1));
            irradianciaSolidaria.setCellValue(irradianciaSolidario.get(i-1));
            irradianciaInclinada.setCellValue(irradianciaInclinado.get(i-1));

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

    @Override
    public void sendName(String name) {
        buttonCreateExcel(name);
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





    public void openIDDialog(){
        ExportModal newFragment = new ExportModal();
        newFragment.setTargetFragment(IrradianciaFragment.this, 1);
        newFragment.show(getFragmentManager(), "open id");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                openIDDialog();
                return true;
            }
            case R.id.gran1:{
                constGranularity = 1.0;
                setDataParalelo(24);
                return true;
            }
            case R.id.gran5:{
                constGranularity = 5.0;
                setDataParalelo(24);
                return true;
            }
            case R.id.gran10:{
                constGranularity = 10.0;
                setDataParalelo(24);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }



}

