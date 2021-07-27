package com.example.calcusol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.calcusol.R;
import com.example.calcusol.models.Archivo;

import java.io.File;
import java.util.ArrayList;

public class FilesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> archivos;



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


        TextView item;

        item = v.findViewById(R.id.fileId);

        item.setText(currentFile.getName());


        //Devolvemos la vista inflada
        return v;
    }
}
