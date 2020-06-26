package com.gachon.priend.calendar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.calendar.WeightAdapter;
import com.gachon.priend.calendar.WeightCommit;
import com.gachon.priend.calendar.delegate.OnDeleteWeightListener;
import com.gachon.priend.calendar.request.CommitWeightRequest;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Map;

public class WeightChartActivity extends AppCompatActivity {

    private Animal animal = null;
    private WeightCommit commit = null;
    private Date selectedDate = null;

    private LineChart weightChartView;
    private TextInputEditText dateEditText;
    private TextInputLayout weightContainer;
    private TextInputEditText weightEditText;
    private MaterialButton pushButton;
    private RecyclerView weightListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

        /*
         * Actionbar
         */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Bundle bundle = getIntent().getExtras();
        final long animalId = bundle.getLong(CalendarSelectionActivity.BUNDLE_KEY_ANIMAL_ID);

        final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(this);
        this.animal = animalDb.readOrNull(animalId);
        if (this.animal == null) {
            finish();
        }
        commit = new WeightCommit(this.animal.getWeights(), this.animal.getId());

        /*
         * initialize GUI Components
         */
        weightChartView = findViewById(R.id.weight_chart_simple_chart_view_weights);
        dateEditText = findViewById(R.id.weight_chart_edit_text_date);
        weightContainer = findViewById(R.id.weight_chart_container_weight);
        weightEditText = findViewById(R.id.weight_chart_edit_text_weight);
        pushButton = findViewById(R.id.weight_chart_button_push);
        weightListView = findViewById(R.id.weight_chart_weight_list_view_weights);

        // button_back
        findViewById(R.id.weight_chart_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // text_view_name
        ((MaterialTextView)findViewById(R.id.weight_chart_text_view_name)).setText(animal.getName());

        // button_save
        findViewById(R.id.weight_chart_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commit.getChanges().size() > 0) {

                    new CommitWeightRequest(WeightChartActivity.this, commit).request(new RequestBase.ResponseListener<CommitWeightRequest.EResponse>() {
                        @Override
                        public void onResponse(final CommitWeightRequest.EResponse response, Object[] args) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (response) {

                                        case OK:
                                            animal.setWeights(commit.getApplied());
                                            final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(WeightChartActivity.this);
                                            animalDb.delete(animal.getId());
                                            animalDb.tryWrite(animal);

                                            commit = new WeightCommit(animal.getWeights(), animal.getId());

                                            Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_n_changed, Toast.LENGTH_SHORT).show();
                                            break;

                                        case ACCOUNT_ERROR:
                                            Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_error_account, Toast.LENGTH_LONG).show();
                                            break;

                                        case SERVER_ERROR:
                                            Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_error_server, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(WeightChartActivity.this, R.string.weight_chart_message_error_no_change, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // dateEditText
        selectedDate = Date.getNow();
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(WeightChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = new Date(year, month + 1, dayOfMonth);

                        dateEditText.setText(selectedDate.toString(getResources().getConfiguration().getLocales().get(0)));
                    }
                }, selectedDate.getYear(), selectedDate.getMonth() - 1, selectedDate.getDay()).show();
            }
        });
        dateEditText.setText(selectedDate.toString(getResources().getConfiguration().getLocales().get(0)));

        // weightEditText
        weightEditText.setText("2");

        // pushButton
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightContainer.setHelperText("");

                final String weightString = weightEditText.getText().toString();
                if (weightString.length() <= 0) {
                    weightContainer.setHelperText(getResources().getString(R.string.weight_chart_message_error_required));
                } else {
                    final double weight = (int)(Double.valueOf(weightString) * 100) / 100.0;
                    commit.insertOrUpdate(selectedDate, weight);

                    initializeSeriesViews();
                }
            }
        });

        // weightListView
        weightListView.setLayoutManager(new LinearLayoutManager((this)));

        // weightChartView
        XAxis xAxis = weightChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long longDate = Float.valueOf(value).longValue();
                Date date = new Date(longDate);

                return date.toString(getResources().getConfiguration().getLocales().get(0));
            }
        });
        xAxis.setLabelCount(3, true);

        initializeSeriesViews();
    }

    private void initializeSeriesViews() {
        final WeightAdapter adapter = new WeightAdapter(commit.getApplied(), new OnDeleteWeightListener() {
            @Override
            public void onDelete(@NonNull Date date) {
                commit.delete(date);
                initializeSeriesViews();
            }
        });
        weightListView.setAdapter(adapter);

        ArrayList<Entry> entries = new ArrayList<>();
        for (Map.Entry<Date, Double> entry : commit.getApplied().entrySet()) {
            entries.add(new Entry(entry.getKey().toMillis(), entry.getValue().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, null);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        int secondaryColor = getResources().getColor(R.color.secondaryColor, getTheme());
        dataSet.setColor(secondaryColor);
        dataSet.setCircleColor(secondaryColor);

        LineData data = new LineData(dataSets);
        weightChartView.setData(data);
        weightChartView.invalidate();
    }
}
