package com.tallytest.tablayout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tallytest.tablayout.R;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FilesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> archivos;

    public FilesAdapter (){

    }


    public FilesAdapter (Context context, ArrayList<File> archivos){
        this.context = context;
        this.archivos = archivos;
    }




    @Override
    public int getCount() {
        return archivos.size();
    }

    @Override
    public Object getItem(int position) {
        return archivos.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Copiamos la vista
        View v = convertView;
        //Inflamos la vista con nuestro propio layout
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        v= layoutInflater.inflate(R.layout.item_list, null);
        // Valor actual según la posición
        File currentFile  = archivos.get(position);
        TextView item, fecha;

        item = v.findViewById(R.id.fileId);
        fecha = v.findViewById(R.id.Fecha);
        item.setText(currentFile.getName());
        Date createdAt = new Date(currentFile.lastModified());
        fecha.setText(""+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdAt));



        //Devolvemos la vista inflada
        return v;
    }
}
