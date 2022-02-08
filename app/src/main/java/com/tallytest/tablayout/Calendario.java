package com.tallytest.tablayout;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendario extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private DatePicker calendarView;
    private TextView declinacion, dia;
    long days;
    public Calendario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Calendario.
     */
    // TODO: Rename and change types and number of parameters
    public static Calendario newInstance(String param1, String param2) {
        Calendario fragment = new Calendario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        dia = view.findViewById(R.id.dia);
        declinacion = view.findViewById(R.id.declinacion);

        calendarView.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");

                int monthSelected = i1 +1;
                int yearSelected = i;
                int daySelected = i2;
                String startDate = "1/1/"+yearSelected;
                String endDate = daySelected+"/"+monthSelected+"/"+yearSelected;

                LocalDate startDateValue = LocalDate.parse(startDate, dateFormatter);
                LocalDate endDateValue = LocalDate.parse(endDate, dateFormatter);
                days = ChronoUnit.DAYS.between(startDateValue, endDateValue) + 1;

                if(yearSelected % 4 ==0 && days == 60 ){
                    Toast.makeText(getContext(), "Día Inválido", Toast.LENGTH_LONG).show();
                    dia.setText("DIA: ");
                    declinacion.setText("");

                }
                else{
                    dia.setText("DIA: "+days);

                    declinacion.setText(""+getDeclinacionSpencer(Integer.parseInt(String.valueOf(days)))+"°");
                }
            }
        });






        return view;
    }


    private double truncate(double num){
        return Math.floor(num*100) / 100;
    }


    private double getDeclinacion(int diaJuliano){

        Double result;
        result = 23.45 * Math.sin(Math.toRadians(360*(284+diaJuliano))/365);
        return truncate(result);
    }

    private double getDeclinacionSpencer(int diaJuliano){
        double gamma = 2 * Math.PI * (diaJuliano - 1) / 365;

        double result  = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257 * Math.sin(gamma) - 0.006758 * Math.cos(2*gamma) + 0.000907 * Math.sin(2*gamma) - 0.002697 * Math.cos(3 * gamma) + 0.00148 * Math.sin(3*gamma);

        result =  Math.toDegrees(result);

        String pattern = "#.###";
        DecimalFormat decimalFormat =  new DecimalFormat(pattern);
        String formattedDouble = decimalFormat.format(result);
        System.out.println("Formatted double d = "+formattedDouble);


        return Double.parseDouble(formattedDouble);
    }
}
