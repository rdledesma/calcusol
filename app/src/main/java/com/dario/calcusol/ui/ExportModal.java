package com.dario.calcusol.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dario.calcusol.R;
import com.google.android.material.textfield.TextInputEditText;

public class ExportModal extends DialogFragment {
    private View rootView;
    private TextInputEditText archivo;


    public interface OnInputSelected {
        void sendName(String name);
    }

    public OnInputSelected mOnInputSelected;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exportar datos a excel");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.dialog_export,null);
        archivo = rootView.findViewById(R.id.archivo);



        builder.setView(rootView)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(archivo.getText().length()>0){
                            mOnInputSelected.sendName(archivo.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e) {
            Log.e("ERror", e.getMessage());
        }
    }
}
