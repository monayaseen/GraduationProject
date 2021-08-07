package com.example.example1.Actitvity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.example1.Config;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.Dialogs.RegisterDialog;
import com.example.example1.modal.Jobs;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.example1.Config.TAG_job_Id;
import static com.example.example1.Config.TAG_job_name;

public class SignUp extends AppCompatActivity {

    EditText mName, mEmail, mPassword, mPhone;
    Button mSignUp;
    TextView mHavingAccount;
    FirebaseAuth fAuth;
    ErrorDialog dialog = new ErrorDialog();

    private Spinner spinner;
    private Spinner Address;
    private ArrayList<String> jobs;
    ProgressDialog progressDialog;
    private APIConnection connections;
    Jobs j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mName = findViewById(R.id.Name);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mPhone = findViewById(R.id.Phone);
        mSignUp = findViewById(R.id.signUp);
        mHavingAccount = findViewById(R.id.havingAccount);
        spinner = (Spinner) findViewById(R.id.jobstatus);
        fAuth = FirebaseAuth.getInstance();
        jobs = new ArrayList<String>();
        Address = findViewById(R.id.Address);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!! ");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required!! ");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("At least 6 Characters!!");
                    return;
                }


            }
        });

        mHavingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });
        getData();
        getCountries();
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String jobName = spinner.getSelectedItem().toString();
                if ( mEmail.getText().toString()==""||mPassword.getText().toString()=="") {

                    dialog.showDialog(SignUp.this, "Please make sure to fill all the information");
                }
                Register(SignUp.this, mName.getText().toString(),
                        mEmail.getText().toString(),
                        mPhone.getText().toString(),
                        mPassword.getText().toString(), jobName, Address.getSelectedItem().toString());
            }
        });

    }

    private void getData() {
        //Creating a string request
        j = new Jobs();
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
                            spinner.setAdapter(new ArrayAdapter<String>(SignUp.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    jobs));

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void getCountries() {
        String[] countery = {
                "Afghanistan", "Albania"
                , "Algeria"
                , "Andorra"
                , "Angola"
                , "Anguilla"
                , "Antigua & Barbuda"
                , "Argentina"
                , "Armenia"
                , "Australia"
                , "Austria"
                , "Azerbaijan"
                , "Bahamas"
                , "Bahrain"
                , "Bangladesh"
                , "Barbados"
                , "Belarus"
                , "Belgium"
                , "Belize"
                , "Benin"
                , "Bermuda"
                , "Bhutan"
                , "Bolivia"
                , "Bosnia & Herzegovina"
                , "Botswana"
                , "Brazil"
                , "Brunei Darussalam"
                , "Bulgaria"
                , "Burkina Faso"
                , "Myanmar/Burma"
                , "Burundi"
                , "Cambodia"
                , "Cameroon"
                , "Canada"
                , "Cape Verde"
                , "Cayman Islands"
                , "Central African Republic"
                , "Chad"
                , "Chile"
                , "China"
                , "Colombia"
                , "Comoros"
                , "Congo"
                , "Costa Rica"
                , "Croatia"
                , "Cuba"
                , "Cyprus"
                , "Czech Republic"
                , "Democratic Republic of the Congo"
                , "Denmark"
                , "Djibouti"
                , "Dominican Republic"
                , "Dominica"
                , "Ecuador"
                , "Egypt"
                , "El Salvador"
                , "Equatorial Guinea"
                , "Eritrea"
                , "Estonia"
                , "Ethiopia"
                , "Fiji"
                , "Finland"
                , "France"
                , "French Guiana"
                , "Gabon"
                , "Gambia"
                , "Georgia"
                , "Germany"
                , "Ghana"
                , "Great Britain"
                , "Greece"
                , "Grenada"
                , "Guadeloupe"
                , "Guatemala"
                , "Guinea"
                , "Guinea-Bissau"
                , "Guyana"
                , "Haiti"
                , "Honduras"
                , "Hungary"
                , "Iceland"
                , "India"
                , "Indonesia"
                , "Iran"
                , "Iraq"
                , "Israel and the Occupied Territories"
                , "Italy"
                , "Ivory Coast (Cote d'Ivoire)"
                , "Jamaica"
                , "Japan"
                , "Jordan"
                , "Kazakhstan"
                , "Kenya"
                , "Kosovo"
                , "Kuwait"
                , "Kyrgyz Republic (Kyrgyzstan)"
                , "Laos"
                , "Latvia"
                , "Lebanon"
                , "Lesotho"
                , "Liberia"
                , "Libya"
                , "Liechtenstein"
                , "Lithuania"
                , "Luxembourg"
                , "Republic of Macedonia"
                , "Madagascar"
                , "Malawi"
                , "Malaysia"
                , "Maldives"
                , "Mali"
                , "Malta"
                , "Martinique"
                , "Mauritania"
                , "Mauritius"
                , "Mayotte"
                , "Mexico"
                , "Moldova, Republic of Monaco"
                , "Mongolia"
                , "Montenegro"
                , "Montserrat"
                , "Morocco"
                , "Mozambique"
                , "Namibia"
                , "Nepal"
                , "Netherlands"
                , "New Zealand"
                , "Nicaragua"
                , "Niger"
                , "Nigeria"
                , "Korea, Democratic Republic of (North Korea)"
                , "Norway"
                , "Oman"
                , "Pacific Islands"
                , "Pakistan"
                , "Panama"
                , "Papua New Guinea"
                , "Paraguay"
                , "Palestine"
                , "Peru"
                , "Philippines"
                , "Poland"
                , "Portugal"
                , "Puerto Rico"
                , "Qatar"
                , "Reunion"
                , "Romania"
                , "Russian Federation"
                , "Rwanda"
                , "Saint Kitts and Nevis"
                , "Saint Lucia"
                , "Saint Vincent's & Grenadines"
                , "Samoa"
                , "Sao Tome and Principe"
                , "Saudi Arabia"
                , "Senegal"
                , "Serbia"
                , "Seychelles"
                , "Sierra Leone"
                , "Singapore"
                , "Slovak Republic "
                , "Slovenia"
                , "Solomon Islands"
                , "Somalia"
                , "South Africa"
                , "Korea, Republic of (South Korea)"
                , "South Sudan"
                , "Spain"
                , "Sri Lanka"
                , "Sudan"
                , "Suriname"
                , "Swaziland"
                , "Sweden"
                , "Switzerland"
                , "Syria"
                , "Tajikistan"
                , "Tanzania"
                , "Thailand"
                , "Timor Leste"
                , "Togo"
                , "Trinidad & Tobago"
                , "Tunisia"
                , "Turkey"
                , "Turkmenistan"
                , "Turks & Caicos Islands"
                , "Uganda"
                , "Ukraine"
                , "United Arab Emirates"
                , "United States of America (USA)"
                , "Uruguay"
                , "Uzbekistan"
                , "Venezuela"
                , "Vietnam"
                , "Virgin Islands (UK)"
                , "Virgin Islands (US)"
                , "Yemen"
                , "Zambia"
                , "Zimbabwe"};
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countery);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Address.setAdapter(aa);
    }

    private HashMap<String, String> getHeaders(String name,
                                               String email, String phone_number
            , String password, String jobstatus, String address) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Name", name);
        headers.put("email", email);
        headers.put("phone_number", phone_number);
        headers.put("password", password);
        headers.put("jobstatus", jobstatus);
        headers.put("address", address);
        return headers;
    }

    private void Register(final Activity activity, String name,
                          String email, String phone_number
            , String password, String jobstatus, String address) {

        final ErrorDialog errordialog = new ErrorDialog();
        final RegisterDialog registerDialog = new RegisterDialog();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        connections = new APIConnection(activity);
        connections.servicesConnection(Request.Method.POST, Url.RegisterURL, getHeaders(name,
                email, phone_number, password, jobstatus, address), new com.example.example1.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                System.out.println("result : " + result);
                try {
                    String response = result.trim();

                    if (response.equals("success")) {
                        registerDialog.showDialog(activity, "The user has been registered successfully!");


                    } else if (response.equals("This user already registered")) {
                        errordialog.showDialog(activity, "This user already registered");
                    } else {
                        errordialog.showDialog(activity, "Error occurred, please try again");
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    errordialog.showDialog(activity, "Error occurred, please try again");
                }
            }
        });
    }
}

