package com.example.echosafeguiversion10;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelfDefenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_defense);

        Button downloadPdfButton = findViewById(R.id.downloadPdfButton);
        Button playVideoButton = findViewById(R.id.playVideoButton);

        // PDF download functionality (opens in browser)
        downloadPdfButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/self_defense.pdf"));
            startActivity(browserIntent);
        });

        // Play video functionality (using an online link)
        playVideoButton.setOnClickListener(v -> {
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/self_defense_video.mp4"));
            startActivity(videoIntent);
        });
    }
}
