package com.example.echosafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // For debugging
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameInput, addressInput, occupationInput, phoneNumberInput, pinInput;
    private DatabaseReference databaseReference; // Reference to Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Bind views
        nameInput = findViewById(R.id.name_input);
        addressInput = findViewById(R.id.address_input);
        occupationInput = findViewById(R.id.occupation_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        pinInput = findViewById(R.id.pin_input);
        Button continueButton = findViewById(R.id.continue_button);
        TextView alreadyHaveAccountTextView = findViewById(R.id.already_have_account);

        Log.d("RegistrationActivity", "onCreate - Views are ready");

        // Set click listeners
        continueButton.setOnClickListener(v -> {
            Log.d("RegistrationActivity", "Continue button clicked.");
            registerUser();
        });

        alreadyHaveAccountTextView.setOnClickListener(v -> {
            Log.d("RegistrationActivity", "Redirect to login clicked.");
            redirectToLogin();
        });
    }

    private void registerUser() {
        Log.d("RegistrationActivity", "registerUser() called.");

        String name = nameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String occupation = occupationInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String pin = pinInput.getText().toString().trim();

        Log.d("RegistrationActivity", "Inputs: Name=" + name + ", Address=" + address + ", Occupation=" + occupation + ", PhoneNumber=" + phoneNumber + ", PIN=" + pin);

        if (validateInputs(name, address, occupation, phoneNumber, pin)) {
            // Prepare user data
            User user = new User(name, address, occupation, phoneNumber, pin);

            Log.d("RegistrationActivity", "Attempting to save user data to Firebase...");
            databaseReference.child(phoneNumber).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RegistrationActivity", "User data saved successfully.");
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            redirectToLogin();
                        } else {
                            Log.e("RegistrationActivity", "Firebase save failed.", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.d("RegistrationActivity", "Input validation failed.");
        }
    }

    private boolean validateInputs(String name, String address, String occupation, String phoneNumber, String pin) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(occupation) ||
                TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(pin) || pin.length() < 4) {
            Toast.makeText(this, "All fields must be filled correctly", Toast.LENGTH_SHORT).show();
            Log.d("RegistrationActivity", "Validation failed: Missing or invalid inputs.");
            return false;
        }
        Log.d("RegistrationActivity", "Inputs validated successfully.");
        return true;
    }

    private void redirectToLogin() {
        Log.d("RegistrationActivity", "Redirecting to LoginActivity...");
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close RegistrationActivity to prevent navigating back
    }

    static class User {
        public String name, address, occupation, phoneNumber, pin;

        public User(String name, String address, String occupation, String phoneNumber, String pin) {
            this.name = name;
            this.address = address;
            this.occupation = occupation;
            this.phoneNumber = phoneNumber;
            this.pin = pin;
        }
    }
}
