package com.ekspeace.buddystaff.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.ekspeace.buddystaff.R;

public class Splash extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed((Runnable) () -> {
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}