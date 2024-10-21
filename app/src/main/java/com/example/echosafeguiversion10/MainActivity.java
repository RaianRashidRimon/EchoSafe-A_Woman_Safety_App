package com.example.echosafeguiversion10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        Button initiateFakeCallButton = findViewById(R.id.initiatefakecallButton);
        Button whereAmIButton = findViewById(R.id.whereamiButton);
        // Record Audio button click listener
        Button recordAudioButton = findViewById(R.id.addContactButton);
        recordAudioButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordAudioActivity.class);
            startActivity(intent);
        });


        // Button click listeners
        viewContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewContactsActivity.class);
            startActivity(intent);
        });

        selfDefense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SelfDefenseActivity.class);
            startActivity(intent);
        });

        helpline.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelplineActivity.class);
            startActivity(intent);
        });

        initiateFakeCallButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FakeCallActivity.class);
            intent.putExtra("CALLER_NAME", "Father");
            startActivity(intent);
        });

        // Get the location when "Where am I?" button is clicked
        whereAmIButton.setOnClickListener(v -> checkLocationPermissions());
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation(); // Call the function to get the last known location if permission is already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(); // Call the function to get the last known location if permission is granted
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<android.location.Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnSuccessListener(this, location -> {
            if (location != null) {
                openMaps(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(MainActivity.this, "Please turn on location sharing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMaps(double latitude, double longitude) {
        String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Current+Location)";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // This ensures it opens in Google Maps.
        startActivity(intent);
    }
}