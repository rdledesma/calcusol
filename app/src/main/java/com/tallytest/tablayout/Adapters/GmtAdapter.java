package com.tallytest.tablayout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tallytest.tablayout.Clases.GmtItem;
import com.tallytest.tablayout.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GmtAdapter extends ArrayAdapter <GmtItem> {


    public GmtAdapter(@NonNull Context context, ArrayList<GmtItem> gmtList) {
        super(context, 0, gmtList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);


    }

    @Override
    public View getDropDownView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull @NotNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.gmt_spinner_row, parent, false
            );

        }

            TextView textView = convertView.findViewById(R.id.textViewGmt);


            GmtItem currentItem = getItem(position);

            if (currentItem != null) {
                textView.setText("GMT "+currentItem.getGmt());
            }

        return convertView;
    }

}
