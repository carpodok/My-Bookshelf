package com.alitalhacoban.kitapligim.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.R;



public class SplasPageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    Animation animImg,animAppTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splas_page);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        animImg = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.img_anim);

        animAppTitle = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.title_anim);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        imageView.setAnimation(animImg);
        textView.setAnimation(animAppTitle);

        animImg.start();

        animImg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(),LoginPageActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}