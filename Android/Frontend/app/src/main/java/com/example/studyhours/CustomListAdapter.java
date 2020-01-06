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

    //to store the list of hours
    private final ArrayList<String> hourArray;

    //to store the list of details
    private final ArrayList<String> infoArray;

    public CustomListAdapter(Activity context, ArrayList<String> hourArrayParam, ArrayList<String> infoArrayParam){

        super(context,R.layout.listview_row , hourArrayParam);
        this.context=context;
        this.hourArray = hourArrayParam;
        this.infoArray = infoArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView hourTextField = (TextView) rowView.findViewById(R.id.title_num_of_hours);
        TextView infoTextField = (TextView) rowView.findViewById(R.id.detail_date_time);

        //this code sets the values of the objects to values from the arrays
        hourTextField.setText(hourArray.get(position));
        infoTextField.setText(infoArray.get(position));

        return rowView;

    };
}
