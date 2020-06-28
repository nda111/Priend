package com.gachon.priend.faq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.gachon.priend.R;
import com.gachon.priend.home.activity.HomeActivity;

public class FaqActivity extends AppCompatActivity {
    ImageButton faq_button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        Intent intent = getIntent();
        getSupportActionBar().hide();
        faq_button_back = (ImageButton) findViewById(R.id.faq_button_back);

        faq_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaqActivity.this, HomeActivity.class);
                finish();
            }
        });

    }
}