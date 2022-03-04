package com.tallytest.tablayout.modals;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.tallytest.tablayout.R;

public class ExportModal extends DialogFragment {




    private TextInputEditText tvName;
    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.export_modal,null);


        builder.setTitle("Exportar datos");
        builder.setView(view);

        tvName = view.findViewById(R.id.tvName);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mOnInputSelected.sendName(String.valueOf(tvName.getText()));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExportModal.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public interface FileNameSelected {
        void sendName(String name);
    }

    public FileNameSelected mOnInputSelected;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (FileNameSelected) context;
        }catch (ClassCastException e) {
            Log.e("ERror", e.getMessage());
        }
    }
}