package com.example.calcusol;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HoraSolarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HoraSolarFragment extends Fragment {

    private DatePicker calendar;
    private TextView dia, declinacion;
    private TextInputEditText longitud, latitud, salida, puesta, duracion;




    public HoraSolarFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HoraSolarFragment newInstance() {
        HoraSolarFragment fragment = new HoraSolarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hora_solar, container, false);
        calendar =  view.findViewById(R.id.calendar);

        dia = view.findViewById(R.id.diaJuliano);
        declinacion = view.findViewById(R.id.textDeclinacion);
        /*longitud = view.findViewById(R.id.longitud);
        latitud = view.findViewById(R.id.latitud);
        salida = view.findViewById(R.id.horaSalida);
        puesta = view.findViewById(R.id.horaPuesta);
        duracion = view.findViewById(R.id.duracion);*/


        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String diaJuliano = sharedPref.getString(getString(R.string.saved_high_score_key), "1");




        dia.setText("1");
        declinacion.setText("Declinación: "+getDeclinacion()+"°");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_high_score_key), ""+1);
        editor.commit();




        /*longitud.setText(longitudSaved);
        latitud.setText(latitudSaved);
        salida.setText(""+ parseToTime(getSalidaSol()));
        puesta.setText(""+parseToTime(getPuestaSol()));
        duracion.setText(""+ parseToTime(getPuestaSol()-getSalidaSol()));*/




        /*longitud.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_longitud), ""+longitud.getText());
                editor.commit();
            }
        });

        latitud.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_latitud), ""+latitud.getText());
                editor.commit();
            }
        });*/


        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2021,1,1);


        calendar.init(2021, 0, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");

                int monthSelected = month +1;
                int yearSelected = year;
                int daySelected = dayOfMonth;
                String startDate = "1/1/"+yearSelected;
                String endDate = daySelected+"/"+monthSelected+"/"+yearSelected;

                LocalDate startDateValue = LocalDate.parse(startDate, dateFormatter);
                LocalDate endDateValue = LocalDate.parse(endDate, dateFormatter);
                long days = ChronoUnit.DAYS.between(startDateValue, endDateValue) + 1;

                if(yearSelected % 4 ==0 && days == 60 ){
                    Toast.makeText(getContext(), "Día Inválido", Toast.LENGTH_LONG).show();
                    dia.setVisibility(View.INVISIBLE);
                    declinacion.setVisibility(View.INVISIBLE);
                }
                else{
                    dia.setVisibility(View.VISIBLE);
                    declinacion.setVisibility(View.VISIBLE);
                    dia.setText(""+days);
                    declinacion.setText("Declinación: "+getDeclinacion()+"°");
                }


                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_dia), ""+days);
                editor.commit();
            }
        });


        /*calendar.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");

                int monthSelected = month +1;
                int yearSelected = year;
                int daySelected = dayOfMonth;
                String startDate = "1/1/"+yearSelected;
                String endDate = daySelected+"/"+monthSelected+"/"+yearSelected;

                LocalDate startDateValue = LocalDate.parse(startDate, dateFormatter);
                LocalDate endDateValue = LocalDate.parse(endDate, dateFormatter);
                long days = ChronoUnit.DAYS.between(startDateValue, endDateValue) + 1;

                if(yearSelected % 4 ==0 && days == 60 ){
                    Toast.makeText(getContext(), "Día Inválido", Toast.LENGTH_LONG).show();
                    dia.setVisibility(View.INVISIBLE);
                    declinacion.setVisibility(View.INVISIBLE);
                }
                else{
                    dia.setVisibility(View.VISIBLE);
                    declinacion.setVisibility(View.VISIBLE);
                    dia.setText(""+days);
                    declinacion.setText("Declinación: "+getDeclinacion()+"°");
                }






                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_high_score_key), ""+days);
                editor.commit();


                *//*salida.setText(""+ parseToTime(getSalidaSol()));
                puesta.setText(""+parseToTime(getPuestaSol()));
                duracion.setText(""+ parseToTime(getPuestaSol()-getSalidaSol()));*//*


            }
        });*/


        return view;
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
    private double gete(int dia){

        Double result;
        result = 1 + 0.033 *Math.cos(2*Math.PI* (double)dia/365);
        return truncate(result);
    }

    private double getDeclinacion(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(dia.getText()));
        Double result;
        result = 23.45 * Math.sin(Math.toRadians(360*(284+DiaJuliano))/365);



        return truncate(result);
    }

    /*private double getSalidaSol(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(dia.getText()));


        double LAT = -36.03;
        double LONG = 59.1;
        double LONGOBS = 60;
        double result ,resultParcial, resultRad;

        Double dec = getDeclinacion();

        double termino1 =  -Math.tan(Math.toRadians(LAT)) * Math.tan(Math.toRadians(dec.doubleValue()));
        double ws = Math.toDegrees(Math.acos(termino1));

        resultParcial =  12- ws/15;

        double resultts = resultParcial;

        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG) + gete(DiaJuliano))/60;



        return resulttsof;

    }

    private double getPuestaSol(){
        int DiaJuliano =  Integer.parseInt(String.valueOf(dia.getText()));


        double LAT = -36.03;
        double LONG = 59.1;
        double LONGOBS = 60;
        double result ,resultParcial, resultRad;

        Double dec = getDeclinacion();

        double termino1 =  -Math.tan(Math.toRadians(LAT)) * Math.tan(Math.toRadians(dec.doubleValue()));
        double ws = Math.toDegrees(Math.acos(termino1));

        resultParcial =  12 + ws/15;

        double resultts = resultParcial;

        double resulttsof = resultts - (-1) - (4*(LONGOBS-LONG) + gete(DiaJuliano))/60;



        return resulttsof;

    }*/


    /*private double duracionDia(){
        return getPuestaSol() - getSalidaSol();
    }*/



}