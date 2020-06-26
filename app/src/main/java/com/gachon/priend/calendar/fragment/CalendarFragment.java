package com.gachon.priend.calendar.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.gachon.priend.R;
import com.gachon.priend.data.datetime.Date;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;

/**
 * A fragment of calendar
 *
 * @author 유근혁
 * @since May 19th 2020
 */
public class CalendarFragment extends Fragment {

    private Date date = null;

    private MaterialTextView yearTextView = null;
    private MaterialTextView dateTextView = null;
    private CalendarView calendarView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.date = Date.getNow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        yearTextView = view.findViewById(R.id.calendar_text_view_year);
        dateTextView = view.findViewById(R.id.calendar_text_view_date);
        calendarView = view.findViewById(R.id.calendar_calendar_view_date);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                setDate(new Date(year, month + 1, dayOfMonth));
            }
        });
        setDate(date);

        return view;
    }

    /**
     * Get the date selected
     * @return The date selected
     */
    @NonNull
    public Date getDate() {
        return date;
    }

    /**
     * Select a date
     * @param date The date to select
     */
    public void setDate(@NonNull Date date) {
        this.date = date;

        calendarView.setDate(date.toMillis());

        yearTextView.setText(Integer.toString(date.getYear()));
        dateTextView.setText(this.date.toString(getResources().getConfiguration().getLocales().get(0)));

        Log.d("CalendarSelectionActivity", date.toString(getResources().getConfiguration().getLocales().get(0)));
    }
}
