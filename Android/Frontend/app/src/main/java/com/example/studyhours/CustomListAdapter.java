package com.example.studyhours;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;

    //to store the list of details
    private final ArrayList<String> infoArray;

    public CustomListAdapter(Activity context, ArrayList<String> infoArrayParam){

        super(context,R.layout.listview_row , infoArrayParam);
        this.context=context;
        this.infoArray = infoArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView infoTextField = rowView.findViewById(R.id.detail_date_time);

        //this code sets the values of the objects to values from the arrays
        infoTextField.setText(infoArray.get(position));

        return rowView;

    };
}
