package com.example.example1.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.example.example1.Dialogs.*;
import com.example.example1.R;

public class LoadingDialog {

    private Context context ;
    private View view ;
    private AlertDialog dialog ;

    public LoadingDialog(Context context, View view) {
        this.context = context;
        this.view = view;
        this.dialog = dialog;
    }

    public void show()
    {
        setupDialog();
        defineViews();

    }

    // setup alert dialog
    public void setupDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(view);
        alertDialog.setCancelable(false) ;
        dialog = alertDialog.create();
        dialog.show();

    }

    public void defineViews()
    {

        ViewHolder.deleteFlipper = view.findViewById(R.id.loading_flipper);
        ViewHolder. progressBar = view.findViewById(R.id.spin_kit);

    }
    static class ViewHolder{
        public static ProgressBar progressBar;
        public static ViewFlipper deleteFlipper ;
    }
}
