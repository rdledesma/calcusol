package com.tallytest.tablayout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tallytest.tablayout.viewmodel.IrradianciaModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class IrradianciaTOA extends Fragment {

    private IrradianciaModel model;

    public IrradianciaTOA() {
        // Required empty public constructor
    }


    public static IrradianciaTOA newInstance(String param1, String param2) {
        IrradianciaTOA fragment = new IrradianciaTOA();
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
        View view = inflater.inflate(R.layout.fragment_irradiancia_t_o_a, container, false);





        model.getDiaJuliano().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                TextView diaJuliano = view.findViewById(R.id.diaJuliano);
                diaJuliano.setText(s);
            }
        });

        model.getGmt().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView gmt = view.findViewById(R.id.gmt);
                gmt.setText(s);
            }
        });

        model.getLatitud().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView lat = view.findViewById(R.id.lat);
                lat.setText(s +"°");


                TextView alturaSolar = view.findViewById(R.id.alturaSolar);


                Double result = MaxAlturaSolar( model.getDiaJuliano().getValue() , s);
                String pattern = "#.##";
                DecimalFormat decimalFormat =  new DecimalFormat(pattern);
                String formattedDouble = decimalFormat.format(result);
                alturaSolar.setText(""+formattedDouble+"°");


            }
        });
        model.getLongitud().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView longitud = view.findViewById(R.id.longitud_tv);
                longitud.setText(s +"°");
            }
        });


        model.getAltitud().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView altitud = view.findViewById(R.id.altitud);
                altitud.setText(s+ " m s n m");
            }
        });


        model.getMediodiaSolar().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView mediodia = view.findViewById(R.id.mediodia);
                double hora = Double.parseDouble(s);
                mediodia.setText(parseToTime(hora) + " hs");
            }
        });

        model.getAmanecer().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView amanecer = view.findViewById(R.id.amanecer);
                double hora = Double.parseDouble(s);
                amanecer.setText(parseToTime(hora) + " hs");
            }
        });



        model.getDuracion().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView duracion = view.findViewById(R.id.duracion);
                double hora = Double.parseDouble(s);
                duracion.setText(parseToTime(hora) + " hs");
            }
        });

        model.getOcaso().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView ocaso = view.findViewById(R.id.ocaso);
                double hora = Double.parseDouble(s);
                ocaso.setText(parseToTime(hora) + " hs");
            }
        });


        model.getRbMediodia().observe(requireActivity(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                TextView rb = view.findViewById(R.id.rbMediodia);


                String pattern = "#.##";
                DecimalFormat decimalFormat =  new DecimalFormat(pattern);
                String formattedDouble = decimalFormat.format(aDouble);
                rb.setText(""+formattedDouble);
            }
        });



        model.getRazonI().observe(requireActivity(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                TextView razonI = view.findViewById(R.id.razonI);


                String pattern = "#.##";
                DecimalFormat decimalFormat =  new DecimalFormat(pattern);
                String formattedDouble = decimalFormat.format(aDouble);
                razonI.setText(""+formattedDouble);


            }
        });


        ImageView imageView = view.findViewById(R.id.imgDownload);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DownloadActivity.class));
            }
        });



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

    private double delta(int n){
        double gamma = 2 * Math.PI * (n- 1)/365;
        double delta = 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257 * Math.sin(gamma) - 0.006758 * Math.cos(2*gamma) + 0.000907* Math.sin(2*gamma) - 0.002697* Math.cos(3*gamma)+ 0.00148*Math.sin(3*gamma);
        return delta;
    }


    private double MaxAlturaSolar(String dia, String lat){

        double maxAltSol = 90 - titaZmin( Integer.parseInt(dia)  , Double.parseDouble(lat));
        //return Math.toDegrees(maxAltSol);

        return  maxAltSol;
    }

    private double titaZmin(int n, double lat){
        return Math.abs(delta(n) - lat);
    }





}