package com.gachon.priend.settings.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gachon.priend.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        findViewById(R.id.settings_image_view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.settings_container_item_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SettingsActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.settings_container_item_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SettingsActivity.this, AlertSettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}