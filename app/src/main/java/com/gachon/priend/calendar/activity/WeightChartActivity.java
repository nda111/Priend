package com.gachon.priend.calendar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.calendar.delegate.OnDeleteWeightListener;
import com.gachon.priend.calendar.request.CommitWeightRequest;
import com.gachon.priend.calendar.view.SimpleChartView;
import com.gachon.priend.calendar.view.WeightListView;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class WeightChartActivity extends AppCompatActivity {

    private Animal animal = null;
    private Date selectedDate = null;

    private SimpleChartView weightChartView;
    private TextInputEditText dateEditText;
    private TextInputEditText weightEditText;
    private MaterialButton pushButton;
    private WeightListView weightListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Actionbar
         */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_weight_chart, null);
        actionBarView.findViewById(R.id.weight_chart_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        actionBarView.findViewById(R.id.weight_chart_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CommitWeightRequest(WeightChartActivity.this, weightListView.getCommit()).request(new RequestBase.ResponseListener<CommitWeightRequest.EResponse>() {
                    @Override
                    public void onResponse(final CommitWeightRequest.EResponse response, final Object[] args) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                switch (response) {

                                    case OK:
                                        final int successCount = (int)args[0];
                                        Toast.makeText(
                                                WeightChartActivity.this,
                                                successCount + " " + getResources().getString(R.string.weight_chart_message_n_changed),
                                                Toast.LENGTH_LONG).show();

                                        weightListView.setInitialData(animal.getWeights(), animal.getId());
                                        weightChartView.setValues(animal.getWeights());
                                        break;

                                    case SERVER_ERROR:
                                        Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_error_server, Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        });
                    }
                });
            }
        });

        final Bundle bundle = getIntent().getExtras();

        // todo: update intent key for animal instance
        this.animal = bundle.getParcelable("animal");

        /*
         * initialize GUI Components
         */
        weightChartView = findViewById(R.id.weight_chart_simple_chart_view_weights);
        dateEditText = findViewById(R.id.weight_chart_edit_text_date);
        weightEditText = findViewById(R.id.weight_chart_edit_text_weight);
        pushButton = findViewById(R.id.weight_chart_button_push);
        weightListView = findViewById(R.id.weight_chart_weight_list_view_weights);

        // weightChartView
        weightChartView.setValues(this.animal.getWeights());
        weightChartView.setGraphColor(getResources().getColor(R.color.primaryColor, getTheme()));

        // dateEditText
        setSelectedDate(Date.getNow());
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(WeightChartActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        setSelectedDate(new Date(year, month, dayOfMonth));
                    }
                }, selectedDate.getYear(), selectedDate.getMonth() - 1, selectedDate.getDay());
                dialog.show();
            }
        });

        // weightEditText
        weightEditText.setText("2");

        // pushButton
        pushButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final double weight = Double.parseDouble(weightEditText.getText().toString());

                weightListView.insertOrUpdateWeight(selectedDate, weight);
                weightChartView.insertOrUpdateValue(selectedDate, weight);
            }
        });

        // weightListView
        weightListView.setInitialData(animal.getWeights(), animal.getId());
        weightListView.setOnDeleteWeightListener(new OnDeleteWeightListener() {
            @Override
            public void onDelete(@NonNull Date date) {

                weightListView.deleteWeight(selectedDate);
                weightChartView.removeValue(date);
            }
        });

        // weightChartView
        weightChartView.setValues(this.animal.getWeights());
    }

    @Override
    public void onBackPressed() {

        new CommitWeightRequest(this, this.weightListView.getCommit()).request(new RequestBase.ResponseListener<CommitWeightRequest.EResponse>() {
            @Override
            public void onResponse(final CommitWeightRequest.EResponse response, final Object[] args) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        switch (response) {

                            case OK:
                                final int successCount = (int)args[0];
                                Toast.makeText(
                                        WeightChartActivity.this,
                                        successCount + " " + getResources().getString(R.string.weight_chart_message_n_changed),
                                        Toast.LENGTH_LONG).show();

                                WeightChartActivity.super.onBackPressed();
                                break;

                            case SERVER_ERROR:
                                Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_error_server, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private void setSelectedDate(@NonNull final Date date) {

        final Locale here = getResources().getConfiguration().getLocales().get(0);

        this.selectedDate = date;
        this.dateEditText.setText(this.selectedDate.toString(here));
    }
}
