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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterPageActivity extends AppCompatActivity {

    TextInputLayout email,password,userName;

    String emailText,passwordText,userNameText;

    ProgressBar spinner;

    Firebase firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebase = new Firebase();

        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);

        email = findViewById(R.id.emailTextInRegister);
        password = findViewById(R.id.passwordTextInRegistter);
        userName = findViewById(R.id.userNameTextInRegister);

    }

    @SuppressWarnings("ConstantConditions")
    public void signUp(View view){

        if (InternetConnection.isConnected(getApplicationContext())){
            emailText = email.getEditText().getText().toString();
            passwordText = password.getEditText().getText().toString();
            userNameText = userName.getEditText().getText().toString();

            boolean isOK = true;


            if (userNameText.replaceAll("\\s+","").isEmpty()){
                userName.setError("Kullanıcı adınızı girin.");
            }
            if (emailText.replaceAll("\\s+","").isEmpty()){
                email.setError("Mail adresinizi girin.");
                isOK = false;
            }
            if (passwordText.replaceAll("\\s+","").isEmpty()){
                password.setError("Şifrenizi girin.");
                isOK = false;
            }

            if (isOK){
                spinner.setVisibility(View.VISIBLE);
                createNewUsersAccount(emailText,passwordText);

            }
        }else {
            Toast.makeText(getApplicationContext(),"İternet bağlantınızı kontrol edin",Toast.LENGTH_LONG).show();
        }
    }

    private void createNewUsersAccount(String email,String password){

         firebase = new Firebase();

        HashMap<String,Object> hashMap  = new HashMap<>();
        hashMap.put("userName",userNameText);
        hashMap.put("userProfileImage","-");
        hashMap.put("userProfileImageName","-");


        firebase.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebase.getFirebaseFirestore().collection(emailText).document("user").set(hashMap).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    spinner.setVisibility(View.INVISIBLE);


                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                        sendEmail();

                                        spinner.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(),"Kayıt işlemi başarıyla gerçekleşti.",Toast.LENGTH_SHORT).show();
                                        Intent intentToLogin = new Intent(getApplicationContext(),LoginPageActivity.class);
                                        intentToLogin.putExtra("getLocate","fromRegister");
                                        startActivity(intentToLogin);
                                        finish();

                                }
                            });

                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                spinner.setVisibility(View.INVISIBLE);

                String errorCode = "";

                if (e instanceof FirebaseAuthWeakPasswordException){

                    Toast.makeText(getApplicationContext(),"Şifreniz en az 6 karaketer içermelidir",Toast.LENGTH_LONG).show();
                }else if (e instanceof FirebaseAuthUserCollisionException){
                     errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();

                    if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                        Toast.makeText(getApplicationContext(),"Girdiğiniz mail adresine ait zaten  geçerli bir hesap bulunmaktadır .Başka bir mail adresiyle tekrar deneyin",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    public void haveAnAccount(View view){
        startActivity(new Intent(getApplicationContext(),LoginPageActivity.class));
        finish();
    }

    public void sendEmail(){

                firebase.getFirebaseAuth().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Doğrulama linki email adresine gönderildi,lütfen kontrol edin.",Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getApplicationContext(),"Bir hata oluştu! Lüften tekrar deneyiniz.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}