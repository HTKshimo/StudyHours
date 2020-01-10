package com.example.studyhours.ui.sistersHours;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.studyhours.BaseActivity;
import com.example.studyhours.CustomListAdapter;
import com.example.studyhours.R;
import com.example.studyhours.ui.gallery.GalleryFragment;
import com.example.studyhours.ui.gallery.GalleryViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.ProgressBar;

public class SistersHoursFragment extends Fragment {
    private ListView listView;
//    private ArrayList<String> hourArray;
    private HashMap<String, String> map = new HashMap<>();
    private String[] infoArray;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.sisters_hours, container, false);

        progressBar = root.findViewById(R.id.progressBarSistersHours);
        progressBar.setVisibility(View.VISIBLE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference("hours/");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    double totalHours = child.child("totalHours").getValue(Double.class);
                    System.out.println("child.child(\"totalHours\").getValue(Double.class): " + totalHours);
                    String name = child.child("sisterName").getValue(String.class);
                    map.put(name + ": " + totalHours, child.getKey());
                }
                infoArray = Arrays.copyOf(map.keySet().toArray(), map.values().toArray().length, String[].class);
                CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(), infoArray);
                listView = root.findViewById(R.id.simpleListView2);
                listView.setAdapter(customListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Fragment fragment = new GalleryFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("UID", map.get(infoArray[position]));
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }
}
