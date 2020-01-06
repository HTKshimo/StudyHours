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

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ListView listView;
    private ArrayList<String> hourArray;
    private ArrayList<String> infoArray;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.activity_hours_history, container, false);

        hourArray = new ArrayList<>();
        String[] tempHourArray = {"test1","test2","test3"};
        for (String s : tempHourArray){
            hourArray.add(s);
        }
        infoArray = new ArrayList<>();
        String[] tempInfoArray = {"MM/DD/YY HH:MM - HH:MM","MM/DD/YY HH:MM - DD HH:MM","MM/DD/YY HH:MM - DD HH:MM"};
        for (String s : tempInfoArray){
            infoArray.add(s);
        }

        CustomListAdapter customListAdapter = new CustomListAdapter(this.getActivity(), hourArray, infoArray);
        listView = root.findViewById(R.id.simpleListView);
        listView.setAdapter(customListAdapter);

        return root;
    }
}