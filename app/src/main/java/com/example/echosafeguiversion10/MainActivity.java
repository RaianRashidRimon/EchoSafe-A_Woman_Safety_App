package com.example.echosafeguiversion10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets for full-screen experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up buttons and their click listeners
        Button viewContacts = findViewById(R.id.viewContactsButton);
        Button selfDefense = findViewById(R.id.selfDefenseButton);
        Button helpline = findViewById(R.id.helplineButton);

        // Navigate to ViewContactsActivity
        viewContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewContactsActivity.class);
            startActivity(intent);
        });

        // Navigate to SelfDefenseActivity
        selfDefense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SelfDefenseActivity.class);
            startActivity(intent);
        });

        // Navigate to HelplineActivity
        helpline.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelplineActivity.class);
            startActivity(intent);
        });
    }
}
