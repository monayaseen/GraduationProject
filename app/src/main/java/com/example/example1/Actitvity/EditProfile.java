package com.example.example1.Actitvity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.example1.Config;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.Dialogs.RegisterDialog;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;
import com.example.example1.modal.Jobs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.example1.Config.TAG_job_Id;
import static com.example.example1.Config.TAG_job_name;

public class EditProfile extends AppCompatActivity {
    String userId;
    ProgressDialog progressDialog;
    EditText newname, newmail, newphone, newpasswor, pass;
    String n_name, n_mail, n_phone, n_password, cn_pass,n_job;
    Button Add;
    Jobs j;
    private ArrayList<String> jobs;

    Spinner job_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        newname = findViewById(R.id.newname);
        newmail = findViewById(R.id.newmail);
        newphone = findViewById(R.id.newphone);
        newpasswor = findViewById(R.id.newpassword);
        job_spinner = findViewById(R.id.job_spinner);

        pass = findViewById(R.id.pass);
        Add = findViewById(R.id.Add);
        jobs = new ArrayList<String>();

        getJobsData();
        Intent i = getIntent();
        ErrorDialog dialog = new ErrorDialog();

        userId = i.getStringExtra("userId");

        APIRequest(this, newname, newmail, newphone);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n_name = newname.getText().toString();
                n_mail = newmail.getText().toString();
                n_phone = newphone.getText().toString();
                n_password = newpasswor.getText().toString();
                cn_pass = pass.getText().toString();
                n_job=job_spinner.getSelectedItem().toString();
                System.out.println(n_job);
                if (n_name.isEmpty() ||
                        n_mail.isEmpty() ||
                        n_phone.isEmpty() ||
                        n_password.isEmpty() ||
                        cn_pass.isEmpty()) {

                    dialog.showDialog(EditProfile.this, "Please make sure to fill all the information");
                }
                if (!n_password.equals(cn_pass)) {
                    dialog.showDialog(EditProfile.this, "Password and confirm password not match");
                }if (! n_mail.contains("@")){
                    dialog.showDialog(EditProfile.this, " Please confirm your Email");
                }
                else {
                    APIRequestUpdate(EditProfile.this, n_name, n_mail, n_phone, n_password, userId,n_job);
                }

            }
        });
    }

    private HashMap<String, String> getProfileHeaders(String name,
                                                      String email, String phone_number
            , String password, String userId,String job) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Name", name);
        headers.put("email", email);
        headers.put("phone_number", phone_number);
        headers.put("password", password);
        headers.put("userId", userId);
        headers.put("job", job);

        return headers;
    }

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userId", userId);

        return headers;
    }
    private void getJobsData() {
        //Creating a string request
        j = new Jobs();
        ProgressDialog progressDialogJob = new ProgressDialog(EditProfile.this);
        progressDialogJob.setMessage("Loading....");
        progressDialogJob.show();
        StringRequest stringRequest = new StringRequest(Config.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //Parsing the fetched Json String to JSON Object
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                j.setJob_name(array.getJSONObject(i).getString(TAG_job_name));
                                j.setJob_id(array.getJSONObject(i).getString(TAG_job_Id));


                                jobs.add(j.getJob_name());
                            }
                            job_spinner.setAdapter(new ArrayAdapter<String>(EditProfile.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    jobs));
                            progressDialogJob.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialogJob.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void APIRequest(final Context context, EditText newname, EditText newmail, EditText newphone) {
        final ErrorDialog dialog = new ErrorDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        APIConnection connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.getUserURL, getHeaders(), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("user");
                    System.out.println("result : " + array.toString());
                    for (int i = 0; i < array.length(); i++) {

                        String user_name = array.getJSONObject(i).getString("user_name");
                        String Email = array.getJSONObject(i).getString("Email");
                        String phone = array.getJSONObject(i).getString("phone");

                        newname.setText(user_name);
                        newmail.setText(Email);
                        newphone.setText(phone);

                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(EditProfile.this, "Error occurred");
                }
            }
        });
    }

    private void APIRequestUpdate(final Context context, String name,
                                  String email, String phone_number
            , String password, String userId,String job) {
        final ErrorDialog dialog = new ErrorDialog();
        final RegisterDialog registerDialog = new RegisterDialog();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        APIConnection connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.updateInfoURL, getProfileHeaders(name, email, phone_number, password, userId,job)
                , new com.example.example1.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {

                        System.out.println(result);

                        progressDialog.dismiss();
                        if (result.equals("updated successfully")) {
                            registerDialog.showDialog(EditProfile.this, "updated successfully");

                        } else {
                            dialog.showDialog(EditProfile.this, "Error occurred");
                        }

                    }
                });
    }
}