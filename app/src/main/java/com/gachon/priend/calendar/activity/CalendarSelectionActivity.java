package com.gachon.priend.calendar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.calendar.Memo;
import com.gachon.priend.calendar.delegate.ShowMemoListener;
import com.gachon.priend.calendar.request.MemoListRequest;
import com.gachon.priend.calendar.view.MemoListView;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.home.activity.HomeActivity;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;

public class CalendarSelectionActivity extends AppCompatActivity {

    public static final String BUNDLE_KEY_MEMO = "memo";
    public static final String BUNDLE_KEY_ANIMAL_ID = "animal_id";
    public static final String BUNDLE_KEY_WHEN = "when";

    private MemoListView memoListView = null;
    private CalendarView calendarView = null;
    private MaterialTextView yearTextView = null;
    private MaterialTextView dateTextView = null;

    private Animal animal = null;

    private boolean isCalendarVisible = true;
    private long backupDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_selection);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /*
         * Get Extras from Intent
         */
        final long animalId = getIntent().getLongExtra(HomeActivity.INTENT_KEY_ANIMAL_ID, -1);
        final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(this);
        animal = animalDb.readOrNull(animalId);

        /*
         * Initialize actionbar GUI components
         */
        calendarView = findViewById(R.id.calendar_calendar_view_date);

        // button_back
        findViewById(R.id.calendar_selection_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // text_view_name
        ((MaterialTextView) findViewById(R.id.calendar_selection_text_view_name)).setText(animal.getName());

        // button_add
        (findViewById(R.id.calendar_selection_button_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_KEY_MEMO, null);
                bundle.putLong(BUNDLE_KEY_WHEN, calendarView.getDate());
                bundle.putLong(BUNDLE_KEY_ANIMAL_ID, animalId);

                Intent intent = new Intent(CalendarSelectionActivity.this, RecordActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // button_chart
        (findViewById(R.id.calendar_selection_button_chart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(BUNDLE_KEY_ANIMAL_ID, animalId);

                Intent intent = new Intent(CalendarSelectionActivity.this, WeightChartActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // calendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                final Date date = new Date(year, month + 1, dayOfMonth);
                backupDate = date.toMillis();
                updateMemoList(date);
            }
        });
        backupDate = calendarView.getDate();

        /*
         * Initialize GUI Components
         */
        memoListView = findViewById(R.id.calendar_selection_memo_list);
        yearTextView = findViewById(R.id.calendar_text_view_year);
        dateTextView = findViewById(R.id.calendar_text_view_date);

        final View.OnClickListener textViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPortrait()) {
                    if (isCalendarVisible) {
                        collapseCalendar();
                    } else {
                        expandCalendar();
                    }
                }
            }
        };

        // yearTextView, dateTextView
        yearTextView.setOnClickListener(textViewOnClickListener);
        dateTextView.setOnClickListener(textViewOnClickListener);

        // memoListView
        memoListView.setOnShowMemoListener(new ShowMemoListener() {
            @Override
            public void showMemo(Memo memo) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_KEY_MEMO, memo);
                bundle.putLong(BUNDLE_KEY_ANIMAL_ID, animalId);

                Intent intent = new Intent(CalendarSelectionActivity.this, RecordActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMemoList(new Date(backupDate));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                collapseCalendar();
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                expandCalendar();
                break;

            default:
                break;
        }
    }

    private void updateMemoList(@NonNull Date date) {

        yearTextView.setText(Integer.toString(date.getYear()));
        dateTextView.setText(date.toString(getResources().getConfiguration().getLocales().get(0)));
        collapseCalendar();

        new MemoListRequest(NowAccountManager.getAccountOrNull(this), animal, date).request(new RequestBase.ResponseListener<MemoListRequest.EResponse>() {
            @Override
            public void onResponse(final MemoListRequest.EResponse response, final Object[] args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (response) {

                            case SERVER_ERROR:
                                Toast.makeText(CalendarSelectionActivity.this, R.string.calendar_message_error_server, Toast.LENGTH_LONG).show();
                                break;

                            case ACCOUNT_ERROR:
                                Toast.makeText(CalendarSelectionActivity.this, R.string.calendar_message_error_account, Toast.LENGTH_LONG).show();
                                break;

                            case OK:
                                final LinkedList<Memo> memoList = (LinkedList<Memo>) args[0];

                                memoListView.clearMemo();

                                for (Memo memo : memoList) {
                                    memoListView.addMemo(memo);
                                }

                                final String message = getResources().getString(
                                        R.string.calendar_message_num_of_memo).replace("{0}",
                                        Integer.toString(memoList.size()));

                                Toast.makeText(CalendarSelectionActivity.this, message, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private void expandCalendar() {
        isCalendarVisible = true;
        calendarView.setVisibility(View.VISIBLE);
    }

    private void collapseCalendar() {
        isCalendarVisible = false;
        calendarView.setVisibility(View.GONE);
    }
}