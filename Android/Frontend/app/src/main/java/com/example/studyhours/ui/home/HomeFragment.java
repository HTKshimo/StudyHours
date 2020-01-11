package com.example.studyhours.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.studyhours.R;
import com.example.studyhours.StudySession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import android.view.ViewGroup.LayoutParams;


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
        final View root = inflater.inflate(R.layout.activity_main, container, false);
        //TODO: disable all menuitems when start button is clicked
//        final View nav = inflater.inflate(R.layout.activity_navigation, container, false);
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
//                    nav.findViewById(R.id.nav_sisters_hours).setEnabled(false);
//                    nav.findViewById(R.id.nav_record).setEnabled(false);
//                    nav.findViewById(R.id.nav_history).setEnabled(false);
//                    nav.findViewById(R.id.nav_logout).setEnabled(false);
                    // The toggle is enabled
                    if (!inherited || !WasRunning){
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        studySession = new StudySession();
                        checkIn = studySession.getIn();
                    }
                    chronometer.start();
                } else {
                    // The toggle is disabled
//                    getActivity().findViewById(R.id.nav_sisters_hours).setEnabled(false);
//                    getActivity().findViewById(R.id.nav_record).setEnabled(false);
//                    getActivity().findViewById(R.id.nav_history).setEnabled(false);
//                    getActivity().findViewById(R.id.nav_logout).setEnabled(false);

                    chronometer.stop();
                    stopOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

                    studySession.setOut();
                    final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference usersRef = database.getReference("hours/" + mFirebaseUser.getUid());

                    final long diff = studySession.getOut() - studySession.getIn();
                    if (diff >= 1800000) {
                        //popup ask if would like to submit, if yes then, else do nothing
                        final View submitPopupView = LayoutInflater.from(getActivity()).inflate(R.layout.submit_hour_popup_layout, null);

                        final PopupWindow popupWindow = new PopupWindow(
                                submitPopupView,
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT
                        );

                        // Set an elevation value for popup window
                        // Call requires API level 21
                        if (Build.VERSION.SDK_INT >= 21) {
                            popupWindow.setElevation(5.0f);
                        }

                        // Get a reference for the custom view close button
                        Button doNotSubmitButton = submitPopupView.findViewById(R.id.do_not_submit_hour);
                        Button submitButton = submitPopupView.findViewById(R.id.submit_hour);
                        // Set a click listener for the popup window close button
                        submitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(), "Hours submitted",
                                        Toast.LENGTH_SHORT).show();
                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                                            //update totalHours in db
                                            if (child.getKey().equals("totalHours")) {
                                                usersRef.child(child.getKey()).runTransaction(new Transaction.Handler() {
                                                    @Override
                                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                                        Double totalHours = mutableData.getValue(Double.class);
                                                        if (totalHours == null) {
                                                            return Transaction.success(mutableData);
                                                        }

                                                        double duration = ((double) diff) / (1000 * 60 * 60);
                                                        DecimalFormat df = new DecimalFormat("#.#");
                                                        df.setRoundingMode(RoundingMode.HALF_UP);
                                                        totalHours += Double.valueOf(df.format(duration));
                                                        mutableData.setValue(totalHours);
                                                        return Transaction.success(mutableData);
                                                    }

                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                                    }
                                                });

                                            }
                                            //increment numSessions by 1, add new session to db
                                            else if (child.getKey().equals("numSessions")) {
                                                usersRef.child(child.getKey()).runTransaction(new Transaction.Handler() {
                                                    @Override
                                                    public Transaction.Result doTransaction(MutableData mutableData) {

                                                        Integer numSessions = mutableData.getValue(Integer.class);
                                                        if (numSessions == null) {
                                                            return Transaction.success(mutableData);
                                                        }
                                                        numSessions += 1;
                                                        mutableData.setValue(numSessions);
                                                        usersRef.child("" + numSessions).setValue(studySession);
                                                        return Transaction.success(mutableData);
                                                    }

                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                                );
                                popupWindow.dismiss();
                            }
                        });

                        doNotSubmitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                popupWindow.dismiss();
                            }
                        });
                        popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);

                    } else {

                        //popup "Sessions duration is less than 30 mins. Only sessions longer than 30 mins will be recorded."
                        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.hour_short_popup_layout, null);

                        final PopupWindow mPopupWindow = new PopupWindow(
                                popupView,
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT
                        );

                        // Set an elevation value for popup window
                        // Call requires API level 21
                        if (Build.VERSION.SDK_INT >= 21) {
                            mPopupWindow.setElevation(5.0f);
                        }

                        // Get a reference for the custom view close button
                        Button okButton = popupView.findViewById(R.id.closePopupBtn);

                        // Set a click listener for the popup window close button
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                mPopupWindow.dismiss();
                            }
                        });
                        mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
                    }
                }
                inherited = false;
            }
        });
        return root;
    }
}