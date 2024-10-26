package com.example.echosafeguiversion10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SMS_PERMISSION_REQUEST_CODE = 2;
    private static final String PHONE_NUMBER = "+8801873843456"; //
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button viewContacts = findViewById(R.id.viewContactsButton);
        Button selfDefense = findViewById(R.id.selfDefenseButton);
        Button helpline = findViewById(R.id.helplineButton);
        Button initiateFakeCallButton = findViewById(R.id.initiatefakecallButton);
        Button whereAmIButton = findViewById(R.id.whereamiButton);
        Button recordAudioButton = findViewById(R.id.addContactButton);
        Button panicButton = findViewById(R.id.panicButton); // Assuming this is your SOS button

        recordAudioButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordAudioActivity.class);
            startActivity(intent);
        });

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
            intent.putExtra("CALLER_NAME", "Ezhar Sir");
            startActivity(intent);
        });

        whereAmIButton.setOnClickListener(v -> checkLocationPermissions());

        // SOS button click listener
        panicButton.setOnClickListener(v -> checkSmsPermissions());
    }

    private void checkSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            checkLocationPermissions();
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation(); // Call to get the last known location if permission is already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions(); // Check location permissions if SMS permission is granted
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(); // Call to get the last known location if permission is granted
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                sendSMS(location.getLatitude(), location.getLongitude());
            } else {
                requestCurrentLocation(); // Request current location if last location is null
            }
        });
    }

    private void requestCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    sendSMS(location.getLatitude(), location.getLongitude());
                    fusedLocationClient.removeLocationUpdates(this); // Stop updates once we have the location
                } else {
                    Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        }, null);
    }

    private void sendSMS(double latitude, double longitude) {
        String message = "I need help! My location: https://maps.google.com/?q=" + latitude + "," + longitude;
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(PHONE_NUMBER, null, message, null, null);
            Toast.makeText(this, "SOS message sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed, please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openMaps(double latitude, double longitude) {
        String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Current+Location)";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // This ensures it opens in Google Maps
        startActivity(intent);
    }
}
