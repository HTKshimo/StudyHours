package com.example.studyhours.ui.home;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.studyhours.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Chronometer chronometer;
    private ToggleButton recordButton;
    private Boolean inherited = false;
    private Boolean WasRunning = false;
    private Long stopOffset = 0L;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save myVar's value in saveInstanceState bundle
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("clock", chronometer.getBase());
        savedInstanceState.putBoolean("running", recordButton.isChecked());
        savedInstanceState.putLong("offset", stopOffset);
        System.out.println("OFFSET SAVED: " + stopOffset);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            inherited = true;
            WasRunning = savedInstanceState.getBoolean("running");
            stopOffset = savedInstanceState.getLong("offset");
            if(WasRunning){
                chronometer.setBase(savedInstanceState.getLong("clock"));
            }else{
                chronometer.setBase(SystemClock.elapsedRealtime() - savedInstanceState.getLong("offset"));
                System.out.println("CLOCK SET: "+(SystemClock.elapsedRealtime() - savedInstanceState.getLong("offset")));
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_main, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        recordButton = root.findViewById(R.id.recordHoursButton);
        chronometer = root.findViewById(R.id.chronometer);

        recordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if (!inherited || !WasRunning){
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    }
                    chronometer.start();
                } else {
                    // The toggle is disabled
                    chronometer.stop();
                    stopOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    //TODO: popup ask if would like to submit
                }
                inherited = false;
            }
        });
        return root;
    }
}