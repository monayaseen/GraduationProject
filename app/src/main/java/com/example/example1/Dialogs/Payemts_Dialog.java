package com.example.example1.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;

import java.util.HashMap;

import static com.example.example1.Actitvity.LogIn.Id;
import static com.example.example1.Actitvity.LogIn.MyPREFERENCES;

public class Payemts_Dialog {
    EditText useraccount;
    ProgressDialog progressDialog;
    private APIConnection connections;
    SharedPreferences sharedpreferences;

    public void showDialog(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment);
        useraccount=dialog.findViewById(R.id.useraccount);
        Intent intent=activity.getIntent();

        String FilterToDate=intent.getStringExtra("ToDate");
        String FilterFromDate=intent.getStringExtra("FromDate");
        String prod_Id=intent.getStringExtra("prod_Id");

        sharedpreferences = activity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String userId = sharedpreferences.getString(Id, null);

        Button dialogButton =  dialog.findViewById(R.id.confirm_payments);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(useraccount.getText().toString().length() != 13
                        || useraccount.getText().toString().isEmpty() )
                    new ErrorDialog().showDialog(activity,"Card Number should be at least 13 digits");*/

                APIRequest(activity,userId,prod_Id,FilterFromDate,FilterToDate);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    private HashMap<String, String> HeadersUserIdAndProdId(String userId,
                                                           String prod_Id,
                                                           String FilterFromDate,
                                                           String FilterToDate
                                                           ) {
        HashMap<String,String > headers=new HashMap<String,String>();
        System.out.println("prodId: "+prod_Id);
        headers.put("userId", userId);
        headers.put("prodId", prod_Id);
        headers.put("FilterFromDate", FilterFromDate);
        headers.put("FilterToDate", FilterToDate);

        return headers;
    }
    private void APIRequest(final Activity activity,
                            String userId,
                            String prodId ,String FilterFromDate,
                            String FilterToDate)
    {
        final SuccessDialog dialog = new  SuccessDialog();
        final ErrorDialog errorDialog = new  ErrorDialog();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        connections= new APIConnection(activity);
        connections.servicesConnection(Request.Method.POST, Url.rentItemUrl, HeadersUserIdAndProdId(
                userId,prodId,FilterFromDate,FilterToDate), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : "+result);
                try {
                    String response =result.trim();

                    if (response.equals("success")){
                        dialog.showDialog(activity,"success");

                    }else if (response.equals("This item is already Rented")){
                        errorDialog.showDialog(activity,"This item is already Rented");
                    }else{
                        errorDialog.showDialog(activity,"Error occurred, please try again");
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(activity,"Error occurred, please try again");
                }
            }
        });
    }
}

