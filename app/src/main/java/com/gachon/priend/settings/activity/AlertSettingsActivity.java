package com.gachon.priend.settings.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.gachon.priend.R;

import java.util.HashMap;

public class AlertSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_settings);

        getSupportActionBar().hide();

        findViewById(R.id.alert_settings_image_view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Switch weightSwitch = findViewById(R.id.alert_settings_switch_button_weight);
        final Switch commentSwitch = findViewById(R.id.alert_settings_switch_button_comment);
        final Switch birthdaySwitch = findViewById(R.id.alert_settings_switch_button_birthday);
        final Switch eventSwitch = findViewById(R.id.alert_settings_switch_button_event);

        final HashMap<Switch, String> messages = new HashMap<Switch, String>();
        messages.put(weightSwitch, "Weight alert has turned ");
        messages.put(commentSwitch, "Comment alert has turned ");
        messages.put(birthdaySwitch, "Birthday alert has turned ");
        messages.put(eventSwitch, "Event alert has turned ");

        for (final Switch view : messages.keySet()) {
            view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String message = messages.get(view) + (isChecked ? "on." : "off.");
                    Toast.makeText(AlertSettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}