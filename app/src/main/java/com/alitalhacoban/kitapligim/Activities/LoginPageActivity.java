package com.alitalhacoban.kitapligim.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.Classes.InternetConnection;
import com.alitalhacoban.kitapligim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginPageActivity extends AppCompatActivity {

    Firebase firebase;

    TextInputLayout email, password;

    String emailText, passwordText, getLocate, getReAu;

    ProgressBar progressBar;


    @Override
    public void onStart() {
        super.onStart();
        getLocate = getIntent().getStringExtra("getLocate");
        getReAu = getIntent().getStringExtra("getReAu");
        if (getReAu != null) {

        } else if (getLocate == null) {
            firebase = new Firebase();

            if (firebase.getFirebaseAuth().getCurrentUser() != null) {

                if (firebase.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        firebase = new Firebase();
        progressBar = findViewById(R.id.spinner);
        progressBar.setVisibility(View.INVISIBLE);

        getLocate = getIntent().getStringExtra("getLocate");


        email = findViewById(R.id.emailTextInLogin);
        password = findViewById(R.id.passwordTextInLogin);

    }

    public void makeNewAccount(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterPageActivity.class));
        finish();
    }

    public void signIn(View view) {

        if (InternetConnection.isConnected(getApplicationContext())) {

            emailText = email.getEditText().getText().toString();
            passwordText = password.getEditText().getText().toString();

            boolean isOK = true;

            if (emailText.isEmpty()) {
                email.setError("Zorunlu alan!");
                isOK = false;
            }
            if (passwordText.isEmpty()) {
                password.setError("Zorunlu alan!");
                isOK = false;
            }

            if (isOK) {
                progressBar.setVisibility(View.VISIBLE);
                signInExistingUsers(emailText, passwordText);
            }


        } else {
            Toast.makeText(getApplicationContext(), "İnternete bağlı olduğunuzdan emin olun", Toast.LENGTH_LONG).show();
        }
    }

    private void signInExistingUsers(String email, String password) {

        firebase.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        if (firebase.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                            if (getReAu == null) {
                                Toast.makeText(getApplicationContext(), "Giriş işlemi başarılıyla gerçekleşti",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                setToProfile();
                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Giriş yapmak için, önce email adresinize gelen doğrulama linkine tıklamanız gerekiyor.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);

                String errorCode;

                if (e instanceof FirebaseAuthInvalidUserException) {


                    errorCode =
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                        Toast.makeText(getApplicationContext(), "Girdiğiniz email adresi veya şifre yanlış.Kontrol edip tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Girdiğiniz email adresi veya şifre yanlış.Kontrol edip tekrar deneyiniz", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void forgotPassword(View view) {

        emailText = email.getEditText().getText().toString();

        if (emailText.isEmpty()) {
            email.setError("");
            email.requestFocus();
            Toast.makeText(LoginPageActivity.this, "Şifre sıfırlama linkini gönderebilmemiz için lüftern email adresini girin.", Toast.LENGTH_LONG).show();
            return;
        } else {

            Toast.makeText(LoginPageActivity.this, "Şifreni sıfırlama linki email adresine gönderildi!", Toast.LENGTH_LONG).show();
            firebase.getFirebaseAuth().sendPasswordResetEmail(emailText)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginPageActivity.this, "Şifreni sıfırlama linki email adresine gönderildi!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginPageActivity.this, "Bir şeyler ters gitti,tekrar dene!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void setToProfile() {

        String m = email.getEditText().toString();
        String p = password.getEditText().getText().toString();


        Intent intent = new Intent(LoginPageActivity.this, Profile.class);
        intent.putExtra("getReAu", "true");
        intent.putExtra("m", m);
        intent.putExtra("p", p);

        Toast.makeText(getApplicationContext(), "Yeniden giriş başarılı.", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }
}