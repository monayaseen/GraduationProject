package com.example.example1.Actitvity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.example1.Dialogs.ErrorDialog;
import com.example.example1.R;
import com.example.example1.Url;
import com.example.example1.api.APIConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import org.jetbrains.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LogIn extends AppCompatActivity {


    EditText mEmail, mPassword;
    Button Login;
    FloatingActionButton aboutUs;
    TextView mNewAccount, NewAccount;
    ProgressDialog progressDialog;
    private APIConnection connections;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Id = "Id";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        NewAccount = findViewById(R.id.NewAccount);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        mNewAccount = findViewById(R.id.NewAccount);
        Login = findViewById(R.id.SignIn);
        aboutUs = findViewById(R.id.aboutus);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedpreferences.getString(Id, null);

        if (IsLogginIn()) {
            startActivity(new Intent(this, MainActivity.class).putExtra("userId", id));
        }
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);


                builder.setMessage("About Us Text");
                builder.setTitle("About Us");
                builder.show();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required ! ");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required ! ");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("At least 6 Characters !");
                    return;
                }
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIRequest(LogIn.this);
            }
        });
        NewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, SignUp.class));
            }
        });

    }

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("email", mEmail.getText().toString().trim());
        headers.put("password", mPassword.getText().toString().trim());
        return headers;
    }

    private void APIRequest(final Context context) {
        final ErrorDialog dialog = new ErrorDialog();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        connections = new APIConnection(context);
        connections.servicesConnection(Request.Method.POST, Url.mainUrl, getHeaders(), new com.example.example1.VolleyCallback() {

            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("user");
                    System.out.println("result : " + array.toString());
                    for (int i = 0; i < array.length(); i++) {

                        String id = array.getJSONObject(i).getString("user_id");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Id, id);
                        editor.commit();
                        startActivity(new Intent(context, MainActivity.class).putExtra("userId", id));
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    dialog.showDialog(LogIn.this, "Login failed, please try again");
                }
            }
        });
    }

    private boolean IsLogginIn() {
        String id = sharedpreferences.getString(Id, null);
        if (id == null)
            return false;
        return true;
    }

}