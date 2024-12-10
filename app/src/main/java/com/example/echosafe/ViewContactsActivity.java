package com.example.echosafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewContactsActivity extends AppCompatActivity {

    private LinearLayout profilesContainer;
    private Button addContactButton;
    private DatabaseReference databaseReference;
    private ArrayList<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");

        // Bind views
        profilesContainer = findViewById(R.id.profiles_container);
        addContactButton = findViewById(R.id.button_add_contact);

        // Load contacts from database
        loadContacts();

        // Set click listener for Add Contact button
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewContactsActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    private void loadContacts() {
        Log.d("ViewContactsActivity", "Starting loadContacts...");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ViewContactsActivity", "onDataChange triggered");

                contactList.clear();
                profilesContainer.removeAllViews(); // Clear the views for new data

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Contact contact = snapshot.getValue(Contact.class);

                        if (contact != null) {
                            Log.d("ViewContactsActivity", "Contact fetched: " + contact.name + ", phone: " + contact.number);
                            contactList.add(contact);
                            addContactView(contact);
                        } else {
                            Log.e("ViewContactsActivity", "Error deserializing contact.");
                        }
                    }
                } else {
                    Log.d("ViewContactsActivity", "No contacts found in database.");
                    Toast.makeText(ViewContactsActivity.this, "No contacts found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewContactsActivity.this, "Failed to load contacts.", Toast.LENGTH_SHORT).show();
                Log.e("ViewContactsActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addContactView(Contact contact) {
        Log.d("ViewContactsActivity", "Creating contact view for: " + contact.name);

        LinearLayout contactView = new LinearLayout(this);
        contactView.setOrientation(LinearLayout.VERTICAL);
        contactView.setPadding(16, 16, 16, 16);
        contactView.setBackgroundResource(R.drawable.contact_card_bg);

        // Display name
        android.widget.TextView nameView = new android.widget.TextView(this);
        nameView.setText(contact.name);
        nameView.setTextSize(18);
        nameView.setTextColor(getResources().getColor(android.R.color.black));

        // Display relation
        android.widget.TextView relationView = new android.widget.TextView(this);
        relationView.setText(contact.relation);
        relationView.setTextSize(14);
        relationView.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Display phone number
        android.widget.TextView numberView = new android.widget.TextView(this);
        numberView.setText(contact.number);
        numberView.setTextSize(14);
        numberView.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Add delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> deleteContact(contact.number));

        contactView.addView(nameView);
        contactView.addView(relationView);
        contactView.addView(numberView);
        contactView.addView(deleteButton);

        profilesContainer.addView(contactView);
        Log.d("ViewContactsActivity", "Contact view added.");
    }

    private void deleteContact(String phoneNumber) {
        Log.d("ViewContactsActivity", "Deleting contact: " + phoneNumber);

        databaseReference.child(phoneNumber).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ViewContactsActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Log.d("DeleteContact", "Deleted contact successfully.");
            } else {
                Toast.makeText(ViewContactsActivity.this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
                Log.e("DeleteContact", "Error: " + task.getException());
            }
        });
    }
}

class Contact {
    public String name;
    public String relation;
    public String number;

    public Contact() {
        // Required empty constructor for Firebase
    }

    public Contact(String name, String relation, String number) {
        this.name = name;
        this.relation = relation;
        this.number = number;
    }
}
