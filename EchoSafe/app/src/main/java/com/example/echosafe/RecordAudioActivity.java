package com.example.echosafe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.IOException;

public class RecordAudioActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;
    private Button stopButton;
    private TextView timerTextView;
    private MediaRecorder mediaRecorder;
    private long startTime = 0;
    private boolean isRecording = false;
    private Thread timerThread;
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        stopButton = findViewById(R.id.stop_button);
        timerTextView = findViewById(R.id.timer_text_view);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startRecording();
        }

        stopButton.setOnClickListener(v -> stopRecording());
    }

    private void startRecording() {
        try {
            audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "/recorded_audio.3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTimer();
            Toast.makeText(this, "Recording started.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to start recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            isRecording = false;
            stopTimer();
            Toast.makeText(this, "Recording saved to: " + audioFilePath, Toast.LENGTH_LONG).show();
            finish(); // Redirect back to HomeScreenActivity
        }
    }

    private void startTimer() {
        startTime = SystemClock.elapsedRealtime();
        timerThread = new Thread(() -> {
            while (isRecording) {
                runOnUiThread(() -> {
                    long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                    timerTextView.setText(formatTime(elapsedTime));
                });
                SystemClock.sleep(100); // Update every 100 ms
            }
        });
        timerThread.start();
    }

    private void stopTimer() {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }

    private String formatTime(long timeMs) {
        int seconds = (int) (timeMs / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permission denied to record audio.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
