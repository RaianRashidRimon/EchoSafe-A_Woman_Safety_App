package com.example.echosafe;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FakeCallActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        String callerName = getIntent().getStringExtra("CALLER_NAME");

        TextView callerNameTextView = findViewById(R.id.callerNameTextView);
        callerNameTextView.setText(callerName);

        ImageView receiveImageView = findViewById(R.id.imageViewReceive);
        ImageView rejectImageView = findViewById(R.id.imageViewReject);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(),
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startRingingAndVibrating();

        receiveImageView.setOnClickListener(v -> {
            stopRingingAndVibrating();
            finish(); // Close the fake call activity
        });

        rejectImageView.setOnClickListener(v -> {
            stopRingingAndVibrating();
            finish(); // Close the fake call activity
        });
    }

    private void startRingingAndVibrating() {
        if (ringtone != null) {
            ringtone.play();
        }

        if (vibrator != null) {
            long[] pattern = {0, 500, 500};
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopRingingAndVibrating() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingingAndVibrating();
    }
}
