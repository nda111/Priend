package com.gachon.priend.community.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommunityDatabaseHelper extends SQLiteOpenHelper {
    public static String NAME = "post.db";
    public static int VERSION = 1;

    public CommunityDatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists noteData("
                + " _id integer PRIMARY KEY autoincrement, "
                + " title text, "
                + " content text, "
                + " time text)";
        db.execSQL(sql);
    }

    public void onOpen(SQLiteDatabase db) {    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS noteData");
        }
    }
}