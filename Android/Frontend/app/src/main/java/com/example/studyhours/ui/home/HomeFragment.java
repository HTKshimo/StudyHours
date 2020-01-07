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

import com.example.studyhours.Admin;
import com.example.studyhours.R;
import com.example.studyhours.StudySession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Chronometer chronometer;
    private ToggleButton recordButton;
    private boolean inherited = false;
    private boolean WasRunning = false;
    private long stopOffset = 0L;
    private long checkIn;
    private StudySession studySession;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save myVar's value in saveInstanceState bundle
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("clock", chronometer.getBase());
        savedInstanceState.putBoolean("running", recordButton.isChecked());
        savedInstanceState.putLong("offset", stopOffset);
        savedInstanceState.putLong("checkIn", checkIn);
        System.out.println("OFFSET SAVED: " + stopOffset);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            inherited = true;
            WasRunning = savedInstanceState.getBoolean("running");
            stopOffset = savedInstanceState.getLong("offset");
            checkIn = savedInstanceState.getLong("checkIn");
            if(WasRunning){
                chronometer.setBase(savedInstanceState.getLong("clock"));
                studySession = new StudySession();
                studySession.setIn(checkIn);
            }else{
                chronometer.setBase(SystemClock.elapsedRealtime() - stopOffset);
                System.out.println("CLOCK SET: "+(SystemClock.elapsedRealtime() - stopOffset));
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
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
                        studySession = new StudySession();
                        checkIn = studySession.getIn();
                    }
                    chronometer.start();
                } else {
                    // The toggle is disabled
                    chronometer.stop();
                    stopOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

                    studySession.setOut();
                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("hours").child(mFirebaseUser.getUid());
                    usersRef.child("test").setValue(studySession);
                    usersRef = usersRef.child("total");
//                    usersRef.setValue(usersRef.get);

                    //TODO: check if duration >= 30mins

                    //TODO: popup ask if would like to submit
                }
                inherited = false;
            }
        });
        return root;
    }
}