package com.gachon.priend.calendar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.google.android.material.textview.MaterialTextView;

import java.util.LinkedList;

public class CalendarSelectionActivity extends AppCompatActivity {

    public static final String BUNDLE_KEY_MEMO = "memo";

    private MemoListView memoListView = null;
    private CalendarView calendarView = null;
    private MaterialTextView yearTextView = null;
    private MaterialTextView dateTextView = null;

    private Animal animal = null;

    private boolean isCalendarVisible = true;

    private void updateMemoList(Date date) {
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

                                Toast.makeText(CalendarSelectionActivity.this, message, Toast.LENGTH_LONG).show();
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
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.calendar_selection_calendar_expand);

        isCalendarVisible = true;

        calendarView.setVisibility(View.VISIBLE);
        calendarView.startAnimation(animation);
    }

    private void collapseCalendar() {
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.calendar_selection_calendar_collapse);

        isCalendarVisible = false;

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                calendarView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        calendarView.setVisibility(View.VISIBLE);
        calendarView.startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_selection);

        /*
         * Get Extras from Intent
         */
        // todo: update intent key for animal instance
        animal = getIntent().getParcelableExtra("animal");

        /*
         * Initialize actionbar
         */
        View customActionBar = LayoutInflater.from(this).inflate(R.layout.actionbar_calendar_selection, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(customActionBar);

        /*
         * Initialize actionbar GUI components
         */
        calendarView = findViewById(R.id.calendar_calendar_view_date);

        // button_back
        customActionBar.findViewById(R.id.calendar_selection_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // text_view_name
        ((MaterialTextView) customActionBar.findViewById(R.id.calendar_selection_text_view_name)).setText(animal.getName());

        // button_add
        (customActionBar.findViewById(R.id.calendar_selection_button_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarSelectionActivity.this, RecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_KEY_MEMO, null);
                // todo: update intent key for animal instance
                bundle.putParcelable("animal", animal);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        // button_chart
        (customActionBar.findViewById(R.id.calendar_selection_button_chart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarSelectionActivity.this, RecordActivity.class);
                Bundle bundle = new Bundle();
                // todo: update intent key for animal instance
                bundle.putParcelable("animal", animal);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        // calendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                final Date date = new Date(year, month - 1, dayOfMonth);
                updateMemoList(date);
            }
        });
        calendarView.setDate(Date.getNow().toMillis());

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
                Intent intent = new Intent(CalendarSelectionActivity.this, RecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_KEY_MEMO, memo);
                // todo: update intent key for animal instance
                bundle.putParcelable("animal", animal);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
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
}