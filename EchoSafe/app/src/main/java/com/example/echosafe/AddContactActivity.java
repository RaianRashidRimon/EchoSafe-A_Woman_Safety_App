package com.example.echosafe;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameInput, relationInput, numberInput;
    private Button saveButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");

        // Bind UI components
        nameInput = findViewById(R.id.name_input);
        relationInput = findViewById(R.id.relation_input);
        numberInput = findViewById(R.id.number_input);
        saveButton = findViewById(R.id.save_button);

        // Handle save button click
        saveButton.setOnClickListener(v -> saveContact());
    }

    private void saveContact() {
        String name = nameInput.getText().toString().trim();
        String relation = relationInput.getText().toString().trim();
        String number = numberInput.getText().toString().trim();

        // Validate all fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(relation) || TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save contact to Firebase using the phone number directly as the key
        Contact contact = new Contact(name, relation, number);

        databaseReference.child(number).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save contact.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
