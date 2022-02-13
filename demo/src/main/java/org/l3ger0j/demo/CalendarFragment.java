package org.l3ger0j.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.calendar , container , false);
        listView = parentView.findViewById(R.id.listView);
        initView();
        return parentView;
    }

    private void initView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                getCalendarData());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((adapterView , view , i , l) ->
                Toast.makeText(getActivity(), "Clicked item!", Toast.LENGTH_LONG).show());
    }

    @NonNull
    private ArrayList<String> getCalendarData(){
        ArrayList<String> calendarList = new ArrayList<>();
        calendarList.add("New Year's Day");
        calendarList.add("St. Valentine's Day");
        calendarList.add("Easter Day");
        calendarList.add("April Fool's Day");
        calendarList.add("Mother's Day");
        calendarList.add("Memorial Day");
        calendarList.add("National Flag Day");
        calendarList.add("Father's Day");
        calendarList.add("Independence Day");
        calendarList.add("Labor Day");
        calendarList.add("Columbus Day");
        calendarList.add("Halloween");
        calendarList.add("All Soul's Day");
        calendarList.add("Veterans Day");
        calendarList.add("Thanksgiving Day");
        calendarList.add("Election Day");
        calendarList.add("Forefather's Day");
        calendarList.add("Christmas Day");
        return calendarList;
    }
}
