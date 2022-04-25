package com.tallytest.tablayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tallytest.tablayout.viewmodel.IrradianciaModel;

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
    public Integer DiaJ;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private IrradianciaModel model;
    private CalendarView calendarView;
    private TextView declinacion, dia, irradianciaNormal;
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

        model = new ViewModelProvider(requireActivity()).get(IrradianciaModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        TextView textView10 = view.findViewById(R.id.textView10);
        textView10.setText("G\u2080:");



        calendarView = view.findViewById(R.id.calendarView);
        dia = view.findViewById(R.id.dia);
        declinacion = view.findViewById(R.id.declinacion);
        irradianciaNormal = view.findViewById(R.id.irradianciaNormal);

        dia.setText("DIA: "+diaJuliano());
        declinacion.setText(""+getDeclinacionSpencer(diaJuliano())+"°");
        model.setDiaJuliano(""+ diaJuliano());




        irradianciaNormal.setText(""+ IrraTOAN(diaJuliano()));

        ImageView imageView = view.findViewById(R.id.imgDownload);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DownloadActivity.class));
            }
        });



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                int Year = year;
                int Month = month;
                int Day = dayOfMonth;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");
                String startDate = "1/1/"+year;
                String endDate = dayOfMonth+"/"+(month+1)+"/"+year;

                LocalDate startDateValue = LocalDate.parse(startDate, dateFormatter);
                LocalDate endDateValue = LocalDate.parse(endDate, dateFormatter);
                days = ChronoUnit.DAYS.between(startDateValue, endDateValue) + 1;


                if(year % 4 ==0 && days == 60 ){
                    Toast.makeText(getContext(), "Día Inválido", Toast.LENGTH_LONG).show();
                    dia.setText("DIA: ");
                    declinacion.setText("");
                    model.setDiaJuliano(""+1);
                    irradianciaNormal.setText(""+ IrraTOAN(1));

                }
                else{
                    dia.setText("DIA: "+days);
                    declinacion.setText(""+getDeclinacionSpencer(Integer.parseInt(String.valueOf(days)))+"°");
                    model.setDiaJuliano(""+days);
                    irradianciaNormal.setText(""+ IrraTOAN((int) days));
                }
            }
        });



        return view;
    }

    



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String IrraTOAN(int n){
        double gamma = 2 * Math.PI * (n - 1)/365;


        double result =  1.000110 + 0.034221*Math.cos(gamma) + 0.001280*Math.sin(gamma) + 0.000719*Math.cos(2*gamma) + 0.000077*Math.sin(2*gamma);



        result = result * 1361;
        String pattern = "#.##";
        DecimalFormat decimalFormat =  new DecimalFormat(pattern);
        String formattedDouble = decimalFormat.format(result);
        return formattedDouble+""+"Whm\u00B2";

    }


    private double getDeclinacionSpencer(int diaJuliano){
        double gamma = 2 * Math.PI * (diaJuliano - 1) / 365;

        double result  = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257 * Math.sin(gamma) - 0.006758 * Math.cos(2*gamma) + 0.000907 * Math.sin(2*gamma) - 0.002697 * Math.cos(3 * gamma) + 0.00148 * Math.sin(3*gamma);

        result =  Math.toDegrees(result);

        String pattern = "#.##";
        DecimalFormat decimalFormat =  new DecimalFormat(pattern);
        String formattedDouble = decimalFormat.format(result);

        return Double.parseDouble(formattedDouble);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private int diaJuliano(){
        int diaJuliano;

        long date = calendarView.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH)+1;
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        //customize According to Your requirement
        String finalDate=Year+"/"+Month+"/"+Day;



        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/u");
        String startDate = "1/1/"+Year;
        String endDate = Day+"/"+Month+"/"+Year;

        LocalDate startDateValue = LocalDate.parse(startDate, dateFormatter);
        LocalDate endDateValue = LocalDate.parse(endDate, dateFormatter);
        days = ChronoUnit.DAYS.between(startDateValue, endDateValue) + 1;

        return (int) days;
    }



}
