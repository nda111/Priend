package com.gachon.priend.community.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.community.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPost extends AppCompatActivity {
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    String tableName;
    String dbName;

    EditText editText_title;
    EditText editText_content;

    TextView textView_time;

    String title;
    String content;
    String time;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        dbName = "notepad.db";
        tableName = "noteData";

        editText_title = findViewById(R.id.activity_edit_editText_title);
        editText_content = findViewById(R.id.activity_edit_editText_content);
        textView_time = findViewById(R.id.activity_edit_textview_time);

        Intent intent = getIntent();
//        인텐트 정보를 받음.
        title = intent.getExtras().getString("title");
        content = intent.getExtras().getString("content");
        time = intent.getExtras().getString("time");
        position = intent.getExtras().getInt("position");
//        키 값

        editText_title.setText(title);
        editText_content.setText(content);
        String txt_time = getString(R.string.post_last_edit_time);
        textView_time.setText(txt_time+ " " + time);

        Button button_save = findViewById(R.id.activity_edit_button_save_edit);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit_title = editText_title.getText().toString();
                String edit_content = editText_content.getText().toString();
                ContentValues contentValues = new ContentValues();

                if(TextUtils.isEmpty(edit_title) && TextUtils.isEmpty(edit_content)){
//                    제목과 내용이 모두 비어있으면 삭제함.
                    Toast.makeText(EditPost.this, R.string.Toast_not_save, Toast.LENGTH_LONG).show();
                    database.delete("noteData", "_id=?", new String[] {String.valueOf(position)});
//                    데이터베이스에서 삭제.
                    finish();
//                    액티비티 닫기
                    Intent intent1 = new Intent(EditPost.this, BulletinList.class);
                    startActivity(intent1);
//                    메인 액티비티 호출
                    return;
                }

                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String formatDate = sdfNow.format(mDate);
//                현재 시간을 담기위해 호출

                contentValues.put("title", edit_title);
                contentValues.put("content", edit_content);
                contentValues.put("time", formatDate);
                Toast.makeText(EditPost.this, R.string.Toast_edit, Toast.LENGTH_SHORT).show();
                database.update("noteData", contentValues, "_id=?", new String[] {String.valueOf(position)});
//                업데이트문 (수정)
                finish();
                Intent intent1 = new Intent(EditPost.this, BulletinList.class);
                startActivity(intent1);
            }
        });


    }

}