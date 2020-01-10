package com.example.studyhours.ui.sistersHours;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.studyhours.CustomListAdapter;
import com.example.studyhours.R;
import com.example.studyhours.ui.gallery.GalleryViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class SistersHoursFragment extends Fragment {
    private ListView listView;
//    private ArrayList<String> hourArray;
    private ArrayList<String> infoArray;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sisters_hours, container, false);

//        hourArray = new ArrayList<>();
//        String[] tempHourArray = {"sister1","sister2","sister3"};
//        for (String s : tempHourArray){
//            hourArray.add(s);
//        }
        infoArray = new ArrayList<>();
        String[] tempInfoArray = {"hour sum 1","hour sum 2","hour sum 3"};
        for (String s : tempInfoArray){
            infoArray.add(s);
        }

        CustomListAdapter customListAdapter = new CustomListAdapter(getActivity(), infoArray);
        listView = root.findViewById(R.id.simpleListView2);
        listView.setAdapter(customListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return root;
    }
}
