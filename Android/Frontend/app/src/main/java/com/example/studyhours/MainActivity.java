package com.example.studyhours;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton recordButton = findViewById(R.id.recordHoursButton);
        final Chronometer chronometer = findViewById(R.id.chronometer);

        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                } else {
                    // The toggle is disabled
                    chronometer.stop();
                    //TODO: popup ask if would like to submit
                }
            }
        });
    }
}
