package com.example.studyhours.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.studyhours.CustomListAdapter;
import com.example.studyhours.R;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView listView;
    private TextView totalHoursText;
    private ArrayList<String> infoArray;
    private float totalHours;
    private HashMap<Integer, String> map;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final View root = inflater.inflate(R.layout.activity_hours_history, container, false);
        totalHoursText = root.findViewById(R.id.total_hours);
        infoArray = new ArrayList<>();
        map = new HashMap<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String UID = getArguments().getString("UID");
        final DatabaseReference usersRef = database.getReference("hours/" + UID);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long in, out;
                for(DataSnapshot child : dataSnapshot.getChildren()){

                    if (child.getKey().equals("totalHours")){
                        System.out.println("child.getKey().equals(\"totalHours\")");
                        totalHours = child.getValue(Float.class);
                        totalHoursText.setText(getString(R.string.total_hours, totalHours));
                    }else if((!child.getKey().equals("numSessions")) && (!child.getKey().equals("sisterName"))){
                        System.out.println("!child.getKey().equals(\"numSessions\")");
                        in = child.child("in").getValue(Long.class);
                        out = child.child("out").getValue(Long.class);
                        String inS = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Timestamp(in));
                        String outS = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Timestamp(out));
                        String temp = inS + " - " + outS;
                        //order the arraylist in chronological order
                        map.put(Integer.valueOf(child.getKey()), temp);
                    }
                }
                SortedSet<Integer> keys = new TreeSet<>(map.keySet());
                for (int key : keys) {
                    infoArray.add(map.get(key));
//                    System.out.println("key: "+key);
                }
                CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(), Arrays.copyOf(infoArray.toArray(), infoArray.toArray().length, String[].class));
                listView = root.findViewById(R.id.simpleListView);
                listView.setAdapter(customListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }
}