package com.example.echosafe;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.telephony.SmsManager;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;




public class HomeScreenActivity extends AppCompatActivity {

    // Request code for location permissions
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private static final int REQUEST_AUDIO_PERMISSION = 101;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private DatabaseReference contactsRef;

    // FusedLocationProviderClient to access location services
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        contactsRef = FirebaseDatabase.getInstance().getReference("Contacts");

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize buttons
        ImageView logoutButton = findViewById(R.id.logout_icon);
        CardView whereAmIButton = findViewById(R.id.button_where_am_i);
        CardView viewContactsButton = findViewById(R.id.button_view_contacts);
        CardView recordAudioButton = findViewById(R.id.button_record_audio);
        CardView selfDefenseButton = findViewById(R.id.button_self_defence); // Added the self defense button
        CardView fakeCallButton = findViewById(R.id.button_fake_call);
        CardView sirenButton = findViewById(R.id.button_siren);
        CardView helplineButton = findViewById(R.id.button_help_lines);
        CardView sosButton = findViewById(R.id.button_sos);
        mediaPlayer = MediaPlayer.create(this, R.raw.siren_sound);
        helplineButton.setOnClickListener(v -> helpline());
        sirenButton.setOnClickListener(v -> toggleSiren());
        whereAmIButton.setOnClickListener(v -> handleWhereAmI());
        sosButton.setOnClickListener(v -> handleSOS());
        viewContactsButton.setOnClickListener(v -> openViewContactsActivity());

        recordAudioButton.setOnClickListener(v -> handleRecordAudio());
        logoutButton.setOnClickListener(v -> logout());
        selfDefenseButton.setOnClickListener(v -> handleSelfDefense());
        fakeCallButton.setOnClickListener(v -> handleFakeCall());
    }
    private void logout() {
        Log.d("LogoutDebug", "Logout button clicked.");

        // Perform Firebase logout if applicable
        FirebaseAuth.getInstance().signOut();

        // Debug log to ensure redirection logic is invoked
        Log.d("LogoutDebug", "Logging out and redirecting to LoginActivity...");

        Intent intent = new Intent(HomeScreenActivity.this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void helpline(){
        Intent intent = new Intent(HomeScreenActivity.this, HelpLineActivity.class);
        startActivity(intent);
    }



    private void toggleSiren() {
        if (isPlaying) {
            stopSiren();
        } else {
            startSiren();
        }
    }

    private void startSiren() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            Toast.makeText(this, "Siren Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSiren() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync(); // Prepares MediaPlayer for reuse
            isPlaying = false;
            Toast.makeText(this, "Siren Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void handleWhereAmI() {
        Toast.makeText(this, "Fetching Location...", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            openGoogleMaps(location);
                        } else {
                            Toast.makeText(this, "Location data not available. Try again later.", Toast.LENGTH_SHORT).show();
                            Log.e("Location Debug", "Location is null");
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Toast.makeText(this, "Failed to fetch location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Location Debug", "Location fetch failed", e);
                    });
        } catch (SecurityException e) {
            Toast.makeText(this, "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Location Debug", "Permission issue or other exception", e);
        }
    }

    private void requestLocationUpdate() {
        // Request a fresh location update
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Set interval to 1 second to get a quick update

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        fusedLocationClient.removeLocationUpdates(this);
                        openGoogleMaps(locationResult.getLastLocation());
                    } else {
                        Toast.makeText(HomeScreenActivity.this, "Still unable to fetch location.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);
        } catch (SecurityException e) {
            Log.e("Location Debug", "Error requesting location updates", e);
        }
    }


    private void openGoogleMaps(Location location) {
        if (location != null) {
            Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() +
                    "?q=" + location.getLatitude() + "," + location.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

            try {
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Google Maps is not installed or unable to launch.", Toast.LENGTH_LONG).show();
                Log.e("Location Debug", "ActivityNotFoundException while starting Maps Intent.", e);
            }
        } else {
            Toast.makeText(this, "No valid location data to display.", Toast.LENGTH_SHORT).show();
        }
    }



    private void openViewContactsActivity() {
        Intent intent = new Intent(this, ViewContactsActivity.class);
        startActivity(intent);
    }

    private void handleRecordAudio() {
        // Check for audio recording permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
        } else {
            // If permission granted, navigate to RecordAudioActivity
            Intent intent = new Intent(this, RecordAudioActivity.class);
            startActivity(intent);
        }
    }

    private void handleSelfDefense() {
        // Navigate to SelfDefenseActivity when self defense button is clicked
        Intent intent = new Intent(this, SelfDefenceActivity.class);
        startActivity(intent);
    }

    private void handleFakeCall(){
        Intent intent = new Intent(this, FakeCallActivity.class);
        intent.putExtra("CALLER_NAME", "Baba");
        startActivity(intent);
    }

    private void handleSOS() {
        // Check if permissions for GPS and SMS are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission is denied", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 100);
            return;
        }

        // Fetch location data
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String locationLink = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        sendSMSToAllContacts(locationLink);
                    } else {
                        Toast.makeText(this, "Could not fetch GPS location.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Failed to fetch GPS data.", Toast.LENGTH_SHORT).show();
                    Log.e("GPS Error", "Could not fetch GPS data", e);
                });
    }

    private void sendSMSToAllContacts(String gpsLink) {
        // Fetch contact data from Firebase and send SMS
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = contactSnapshot.child("number").getValue(String.class);
                    if (phoneNumber != null) {
                        sendSMS(phoneNumber, "I NEED HELP!! My location: " + gpsLink);
                    }
                }
                Toast.makeText(getApplicationContext(), "SOS Sent to Contacts", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to fetch contacts", Toast.LENGTH_SHORT).show();
                Log.e("Database Error", "Error fetching contacts", databaseError.toException());
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent to: " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("SMS Debug", "Error sending SMS", e);
        }
    }


}
