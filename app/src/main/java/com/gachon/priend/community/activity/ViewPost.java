package com.gachon.priend.community.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.gachon.priend.R;

public class ViewPost extends AppCompatActivity {
    TextView textView_title;
    TextView textView_content;
    TextView textView_time;

    String title;
    String content;
    String time;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        textView_title = findViewById(R.id.activity_view_textView_title);
        textView_content = findViewById(R.id.activity_view_textView_content);
        textView_time = findViewById(R.id.activity_view_textView_time);

        Intent intent = getIntent();
//        인텐트 정보를 받음.
        title = intent.getExtras().getString("title");
        content = intent.getExtras().getString("content");
        time = intent.getExtras().getString("time");
        position = intent.getExtras().getInt("position");

        textView_title.setText(title);
        textView_content.setText(content);
        String txt_time = getString(R.string.post_last_edit_time);
        textView_time.setText(txt_time + " " + time);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case(MotionEvent.ACTION_DOWN):
                Intent intent = new Intent(this, EditPost.class);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("time", time);
                intent.putExtra("position", position);
                startActivity(intent);
                finish();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewPost.this, BulletinList.class);
        startActivity(intent);
        finish();
    }
}

