package com.gachon.priend.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.data.entity.Group;

public class GroupDatabaseHelper extends SQLiteOpenHelper implements ISQLiteClassBridge<Group, Integer> {

    public static final String TABLE_NAME_GROUP = "animal_group";
    public static final String COL_NAME_ID = "id";
    public static final String COL_NAME_OWNER_ID = "species";
    public static final String COL_NAME_NAME = "name";

    public static final String TABLE_NAME_MANAGED = "managed";
    public static final String COL_NAME_GROUP_ID = "group_id";
    public static final String COL_NAME_ANIMAL_ID = "animal_id";

    public GroupDatabaseHelper(@Nullable Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME_GROUP + " (" +
                COL_NAME_ID + " INTEGER PRIMARY KEY, " +
                COL_NAME_OWNER_ID + " INTEGER, " +
                COL_NAME_NAME + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_NAME_MANAGED + " (" +
                COL_NAME_GROUP_ID + " INTEGER NOT NULL," +
                COL_NAME_ANIMAL_ID + " INTEGER NOT NULL," +
                "PRIMARY KEY (" + COL_NAME_GROUP_ID + ", " + COL_NAME_ANIMAL_ID + ")," +
                "FOREIGN KEY (" + COL_NAME_GROUP_ID + ") REFERENCES " + TABLE_NAME_GROUP + "(" + COL_NAME_ID + "), " +
                "FOREIGN KEY (" + COL_NAME_ANIMAL_ID + ") REFERENCES " + AnimalDatabaseHelper.TABLE_NAME_ANIMAL + "(" + AnimalDatabaseHelper.COL_NAME_ID + ")" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GROUP + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MANAGED + ";");

        this.onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        super.onOpen(db);

        clear();
    }

    @Override
    public Group readOrNull(@NonNull Integer identifier) {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_GROUP + " WHERE " + COL_NAME_ID + "=?;", new String[]{identifier.toString()});

        if (cursor.moveToNext()) {

            long ownerId = cursor.getLong(1);
            String name = cursor.getString(2);

            return new Group(identifier, ownerId, name);
        } else {

            return null;
        }
    }

    @Override
    public boolean tryWrite(@NonNull Group group) {

        final SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("INSERT INTO " + TABLE_NAME_GROUP + " VALUES(" + group.getId() + ", " + group.getOwnerId() + ", '" + group.getName() + "');");
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(@NonNull Integer identifier) {

        final SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME_MANAGED + " WHERE " + COL_NAME_GROUP_ID + "=" + identifier.toString() + ";");
        db.execSQL("DELETE FROM " + TABLE_NAME_GROUP + " WHERE " + COL_NAME_ID + "=" + identifier.toString() + ";");
    }

    @Override
    public void clear() {

        final SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME_GROUP + ";");
        db.execSQL("DELETE FROM " + TABLE_NAME_MANAGED + ";");
    }

    public long[] getAllAnimalsFrom(@NonNull Group group) {

        return getAllAnimalsFrom(group.getId());
    }

    public long[] getAllAnimalsFrom(int groupId) {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_MANAGED + " WHERE " + COL_NAME_GROUP_ID + "=?;", new String[]{Integer.toString(groupId)});
        final int rowCount = cursor.getCount();

        if (rowCount == 0) {
            return new long[0];
        } else {

            long[] result = new long[rowCount];

            for (int i = 0; cursor.moveToNext(); i++) {

                result[i] = cursor.getLong(1);
            }

            return result;
        }
    }

    public boolean bindAnimal(@NonNull Animal animal, @NonNull Group group, boolean addGroupIfNotExists) {

        final SQLiteDatabase db = getWritableDatabase();

        boolean bGroupExists;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_GROUP  + " WHERE " + COL_NAME_ID + "=?;", new String[] { Integer.toString(group.getId()) });

        bGroupExists = cursor.getCount() != 0;
        cursor.close();

        if (!bGroupExists) {
            if (addGroupIfNotExists) {
                tryWrite(group);
            } else {
                return false;
            }
        }

        try {
            db.execSQL("INSERT INTO " + TABLE_NAME_MANAGED + " VALUES(" + group.getId() + ", " + animal.getId() + ");");
            return true;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
    }
}
