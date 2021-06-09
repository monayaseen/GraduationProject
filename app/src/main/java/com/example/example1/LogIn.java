package com.example.example1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//import org.jetbrains.annotations.NotNull;
import androidx.annotation.NonNull;
public class LogIn extends AppCompatActivity {


    EditText mEmail,mPassword;
    Button mSignIn;
    FirebaseAuth fAuth;
    TextView mNewAccount;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.Password);
        mNewAccount=findViewById(R.id.NewAccount);
        mSignIn=findViewById(R.id.SignIn);
        fAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is required!! ");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is required!! ");
                    return;
                }
                if (password.length()<6)
                {
                    mPassword.setError("At least 6 Characters!!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);




                //authenticate the user : check if it is already in firebase or not:

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "Logged in Successfully ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(LogIn.this, "Error !!" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });



            }
        });

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));

            }
        });

    }
}