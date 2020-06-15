package com.gachon.priend.calendar.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    private static final String[] Days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static final String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private Date date = null;

    private MaterialTextView yearTextView = null;
    private MaterialTextView dateTextView = null;
    private CalendarView calendarView = null;

    private CalendarView.OnDateChangeListener onDateChangeListener = null;

    private String generateDateText() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.toMillis());

        return Days[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                Months[calendar.get(Calendar.MONTH)] + ' ' +
                calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDate(Date.getNow());
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

        yearTextView.setText(date.getYear());
        dateTextView.setText(generateDateText());
    }
}
