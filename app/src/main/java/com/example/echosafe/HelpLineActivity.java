package com.example.echosafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpLineActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_line);
        CardView emergencyCall = findViewById(R.id.button_emergency_call);
        emergencyCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                callEmergencyNumber();
            }
        });
        CardView lawButton = findViewById(R.id.button_law);
        lawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // URL for the laws page
                String url = "http://bdlaws.minlaw.gov.bd/act-11/chapter-details-5.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        CardView policeButton = findViewById(R.id.button_police);
        policeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPolice();
            }
        });
    }
    private void callEmergencyNumber(){
        String emergencyNumber = "01798377320";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            startActivity(callIntent);
        }
    }
    private void callPolice() {
        String phoneNumber = "999";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            startActivity(callIntent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPolice();
            }
        }
    }


}