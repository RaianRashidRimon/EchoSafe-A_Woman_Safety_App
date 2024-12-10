package com.example.echosafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberInput, pinInput;
    private Button loginButton;

    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("EchoSafePrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToHomeScreen();
        }
        TextView doNotHaveAccountTextView = findViewById(R.id.does_not_have_account);
        doNotHaveAccountTextView.setOnClickListener(v -> redirectToRegistration());
        // Bind views
        phoneNumberInput = findViewById(R.id.phone_number_input);
        pinInput = findViewById(R.id.pin_input);
        loginButton = findViewById(R.id.login_button);

        // Set click listener
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void redirectToRegistration(){
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String pin = pinInput.getText().toString().trim();

        if (validateInputs(phoneNumber, pin)) {
            // Fetch user data from Firebase
            databaseReference.child(phoneNumber).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    String storedPin = task.getResult().child("pin").getValue(String.class);
                    if (storedPin != null && storedPin.equals(pin)) {
                        saveLoginState(phoneNumber);
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        redirectToHomeScreen();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInputs(String phoneNumber, String pin) {
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberInput.setError("Phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(pin)) {
            pinInput.setError("PIN is required");
            return false;
        }
        return true;
    }

    private void saveLoginState(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userPhone", phoneNumber);
        editor.apply();
    }

    private void redirectToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
