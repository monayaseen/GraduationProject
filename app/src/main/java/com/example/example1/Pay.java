package com.example.example1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.example1.Dialogs.Payemts_Dialog;

public class Pay extends AppCompatActivity {
    private Button paypal, visacard;
    private EditText account_number, account_password;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        paypal = findViewById(R.id.paypal);
        visacard = findViewById(R.id.visacard);
        Payemts_Dialog Payemts_Dialog = new Payemts_Dialog();


        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payemts_Dialog.showDialog(Pay.this);

            }
        });
        visacard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Payemts_Dialog.showDialog(Pay.this);
            }
        });


    }
}