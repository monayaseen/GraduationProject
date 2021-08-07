package com.example.example1.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.VolleyCallback;

import java.util.HashMap;
import java.util.Map;

public class APIConnection {
    private Context context;

    private ErrorDialog errorDialog = new ErrorDialog();

    public APIConnection(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;

    }

    public APIConnection(Context context) {
        this.context = context;
    }

    public void mainConnection(int method,
                               String URL,
                               final HashMap<String, String> Header,
                               final VolleyCallback callback) {

        StringRequest request = new StringRequest(method, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                callback.onSuccessResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", error.getMessage().toString());
                errorDialog.showDialog((Activity) context, "Error in the connection please try again!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return Header;
            }
        };
        Volley.newRequestQueue(context, new HurlStack(null)).add(request);

    }

    public void servicesConnection(int method,
                                   String URL,
                                   final HashMap<String, String> Header,
                                   final VolleyCallback callback) {

        StringRequest request = new StringRequest(method, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", error.getMessage().toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return Header;
            }
        };
        Volley.newRequestQueue(context, new HurlStack(null)).add(request);

    }
}
