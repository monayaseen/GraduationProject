package com.example.example1.Actitvity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.example1.Dialogs.DateFromDateToDialog;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.Dialogs.SuccessDialog;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class ViewItem extends AppCompatActivity {

    TextView view_item_name, tvDescription, view_item_price, tvNoOfRent;
    RatingBar rate;
    ImageView prod_image;
    ImageButton Rate_btn, AddToCart, rent;
    ProgressDialog progressDialog;
    private APIConnection connections;
    float rateValue;
    int CountRates=1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        view_item_name = findViewById(R.id.view_item_name);
        tvDescription = findViewById(R.id.tvDescription);
        view_item_price = findViewById(R.id.view_item_price);
        rate = findViewById(R.id.ratingBar);
        prod_image = findViewById(R.id.prod_image);
        rent = findViewById(R.id.Rent);
        AddToCart = findViewById(R.id.AddToCart);
        tvNoOfRent = findViewById(R.id.tvNoOfRent);
        Rate_btn = findViewById(R.id.Rate_btn);
        final DateFromDateToDialog Datedialog = new DateFromDateToDialog(ViewItem.this);

        Intent i = getIntent();

        String name = i.getStringExtra("prod_name");
        String prod_id = i.getStringExtra("prod_id");
        String user_id = i.getStringExtra("user_id");

        view_item_name.setText(name);
        tvDescription.setText(i.getStringExtra("description"));
        view_item_price.setText(i.getStringExtra("price"));
        rate.setRating(rateValue);

        String nameId = name.replace(" ", "").toLowerCase().trim();
        try {
            int id = getResources().getIdentifier(nameId, "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            prod_image.setBackground(drawable);
        } catch (Exception e) {

            prod_image.setImageDrawable(getDrawable(R.drawable.test));

        }
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequest(ViewItem.this, user_id, prod_id);
            }
        });

        AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Datedialog.showDialog(ViewItem.this, prod_id);
            }
        });
        Rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequestRate(ViewItem.this, String.valueOf(rate.getRating()), prod_id);
            }
        });
        APIRequestNoOfRent(this, user_id, prod_id);


    }

    private HashMap<String, String> HeadersUserIdAndProdId(String userId, String prodId) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userId", userId);
        headers.put("prodId", prodId);

        return headers;
    }

    private HashMap<String, String> Header(String userId, String prodId) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("user_id", userId);
        headers.put("prod_id", prodId);

        return headers;
    }

    private void APIRequestNoOfRent(final Context context, String userId, String prodId) {
        final SuccessDialog dialog = new SuccessDialog();
        final ErrorDialog errorDialog = new ErrorDialog();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.NoOfRentItemUrl, Header(userId, prodId), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("items");
                    for (int i = 0; i < array.length(); i++) {
                        String noOfRents = array.getJSONObject(i).getString("count");
                        tvNoOfRent.setText("No of Rents: " + noOfRents);
                    }
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(ViewItem.this, "Error occurred, please try again");
                }
            }
        });
    }

    private void APIRequest(final Context context, String userId, String prodId) {
        final SuccessDialog dialog = new SuccessDialog();
        final ErrorDialog errorDialog = new ErrorDialog();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.cartItemUrl, HeadersUserIdAndProdId(userId, prodId), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    String response = result.trim();

                    if (response.equals("success")) {
                        dialog.showDialog(ViewItem.this, "success");

                    } else if (response.equals("This item is already in your cart")) {
                        errorDialog.showDialog(ViewItem.this, "This item is already in your cart");
                    } else {
                        errorDialog.showDialog(ViewItem.this, "Error occurred, please try again");
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(ViewItem.this, "Error occurred, please try again");
                }
            }
        });
    }

    private HashMap<String, String> HeaderRate(String rate, String prodId) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("rate", rate);
        headers.put("prodId", prodId);

        return headers;
    }

    private void APIRequestRate(final Context context, String rate, String prodId) {
        final SuccessDialog dialog = new SuccessDialog();
        final ErrorDialog errorDialog = new ErrorDialog();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.rateItemUrl, HeaderRate(rate, prodId), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    String response = result.trim();

                    if (response.equals("Done")) {
                        dialog.showDialog(ViewItem.this, "Rated successfully");
                        CountRates += 1;
             //         rateValue = Float.parseFloat(i.getStringExtra("rate"))/(float)CountRates;


                    } else {
                        errorDialog.showDialog(ViewItem.this, "Error occurred, please try again");
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errorDialog.showDialog(ViewItem.this, "Error occurred, please try again");
                }
            }
        });
    }


}