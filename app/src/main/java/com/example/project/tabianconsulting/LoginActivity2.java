package com.example.project.tabianconsulting;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity2 extends Activity {


    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private TextView mResendVerification;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        mLogin = findViewById(R.id.login_button);
        mResendVerification = findViewById(R.id.resend_verify);

        setupFirebaseAuth();

        mLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid(mEmail.getText().toString()) && isValid(mPassword.getText().toString())) {
                    if (testEmail(mEmail.getText().toString()) && testPassword(mPassword.getText().toString())) {

                        Toast.makeText(LoginActivity2.this, "success", Toast.LENGTH_SHORT).show();
                        final ProgressDialog dialog = ProgressDialog.show(LoginActivity2.this, "Loading", "Please wait...", true);

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity2.this, "Error while authentication try again", Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {

                        Toast.makeText(LoginActivity2.this, "Email or Password is wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(LoginActivity2.this, "You must fill all the fields properly !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mResendVerification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private boolean isValid(String text) {
        return !TextUtils.isEmpty(text) && !text.contains(" ");
    }

    private boolean testEmail(String email) {
        return email.contains(".") && email.contains("@");
    }

    private boolean testPassword(String pass) {
        return pass.length() >= 6;
    }

    /*
    ------------------------------------------------ FIREBASE FUNCTIONS -------------------------------------
     */

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()){
                        Toast.makeText(LoginActivity2.this, "User logged in : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    } else{
                        Toast.makeText(LoginActivity2.this, "Please verify your account: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                } else {
                    Toast.makeText(LoginActivity2.this, "No signed in", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
