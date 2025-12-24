package com.example.echosafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DELAY = 2000; // 2 seconds delay
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize SharedPreferences safely
        sharedPreferences = getSharedPreferences("EchoSafePrefs", MODE_PRIVATE);
        if (sharedPreferences == null) {
            Log.e("SplashActivity", "SharedPreferences is null. Check SharedPreferences initialization.");
        }

        // Log SharedPreferences existence and initial value
        Log.d("SplashActivity", "SharedPreferences initialized successfully.");
        Log.d("SplashActivity", "isLoggedIn default value: " + sharedPreferences.getBoolean("isLoggedIn", false));

        // Add delay for splash screen effect and handle navigation
        new android.os.Handler().postDelayed(() -> navigateUser(), SPLASH_SCREEN_DELAY);
    }

    private void navigateUser() {
        try {
            // Safely retrieve the SharedPreferences key for navigation
            if (sharedPreferences == null) {
                Log.e("SplashActivity", "SharedPreferences still null during navigation.");
                return;
            }

            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Log.d("SplashActivity", "isLoggedIn value from SharedPreferences: " + isLoggedIn);

            Intent intent;
            if (isLoggedIn) {
                Log.d("SplashActivity", "User is logged in. Navigating to HomeScreenActivity.");
                intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
            } else {
                Log.d("SplashActivity", "User is not logged in. Navigating to RegistrationActivity.");
                intent = new Intent(SplashActivity.this, RegistrationActivity.class);
            }

            startActivity(intent);
            Log.d("SplashActivity", "Navigation intent started successfully.");
            finish();
        } catch (Exception e) {
            Log.e("SplashActivity", "Error during navigation or SharedPreferences access", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SplashActivity", "onStart called.");
    }
}
