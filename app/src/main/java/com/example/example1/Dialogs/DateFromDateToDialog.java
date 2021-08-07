package com.example.example1.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.example1.Pay;
import com.example.example1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFromDateToDialog {
    EditText ToDate,FromDate;
    private SimpleDateFormat dateFormatter;
    Context context;
    String FilterToDate,FilterFromDate;

    public DateFromDateToDialog(Context Context) {
    this.context=Context;
    }

    public void showDialog(final Activity activity,String prod_Id){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_datepicker);

        ToDate=dialog.findViewById(R.id.ToDate);
        FromDate=dialog.findViewById(R.id.FromDate);
        dialog.getWindow().setLayout(1000 ,1000 ); // Add  Width and Height
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        ToDate.setInputType(InputType.TYPE_NULL);
        FromDate.setInputType(InputType.TYPE_NULL);
        ToDate.requestFocus();
        Log.i("prod_Id",prod_Id);
        Button SendDateBtn =  dialog.findViewById(R.id.SendDateBtn);

        SendDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterToDate = ToDate.getText().toString().trim();
                FilterFromDate = FromDate.getText().toString().trim();
                Date ToDate = null;
                Date FromDate= null;
                try {
                     ToDate = format.parse(FilterToDate);
                     FromDate = format.parse(FilterFromDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(FilterFromDate.isEmpty() || FilterToDate.isEmpty()|| ToDate.after( FromDate) )
                {
                    new ErrorDialog().showDialog(activity,"Please Confirm the Dates");
                }else {
                    activity.startActivity(new Intent(activity, Pay.class)
                            .putExtra("FromDate",FilterFromDate)
                            .putExtra("ToDate",FilterFromDate)
                            .putExtra("prod_Id",prod_Id)
                    );
                }

            }
        });
        ToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //todo
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, month, dayOfMonth);
                                ToDate.setText(dateFormatter.format(newDate.getTime()));

                            }
                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        FromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //todo
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, month, dayOfMonth);
                                FromDate.setText(dateFormatter.format(newDate.getTime()));

                            }
                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

}
