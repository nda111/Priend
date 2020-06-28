package com.gachon.priend.faq;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gachon.priend.R;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        getSupportActionBar().hide();
    }
}