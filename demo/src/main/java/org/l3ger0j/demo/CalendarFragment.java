package org.l3ger0j.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.calendar , container , false);
        calendarView = parentView.findViewById(R.id.calendarView);
        initView();
        return parentView;
    }

    private void initView(){
        calendarView.setOnDateChangeListener((view , year , month , dayOfMonth) ->
                Toast.makeText(getActivity(), "Clicked item!", Toast.LENGTH_LONG).show());
    }
}