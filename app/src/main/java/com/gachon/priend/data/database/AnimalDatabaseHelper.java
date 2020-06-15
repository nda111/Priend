package com.gachon.priend.data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gachon.priend.data.Sex;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;

import java.util.Map;
import java.util.TreeMap;

public final class AnimalDatabaseHelper extends SQLiteOpenHelper implements ISQLiteClassBridge<Animal, Long> {

    public static final String TABLE_NAME_ANIMAL = "animal";
    public static final String COL_NAME_ID = "id";
    public static final String COL_NAME_SPECIES = "species";
    public static final String COL_NAME_NAME = "name";
    public static final String COL_NAME_BIRTH = "birth";
    public static final String COL_NAME_SEX = "sex";

    public static final String TABLE_NAME_WEIGHTS = "weights";
    public static final String COL_NAME_DATE = "date";
    public static final String COL_NAME_WEIGHT = "weight";

    public AnimalDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME_ANIMAL + " (" +
                COL_NAME_ID + " INTEGER PRIMARY KEY," +
                COL_NAME_SPECIES + " INTEGER NOT NULL," +
                COL_NAME_NAME + " TEXT NOT NULL," +
                COL_NAME_BIRTH + " INTEGER NOT NULL," +
                COL_NAME_SEX + " INTEGER NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_NAME_WEIGHTS + " (" +
                COL_NAME_ID + "INTEGER, " +
                COL_NAME_DATE + "INTEGER, " +
                COL_NAME_WEIGHT + "REAL, " +
                "PRIMARY KEY (" + COL_NAME_ID + ", " + COL_NAME_WEIGHT + "), " +
                "FOREIGN KEY (" + COL_NAME_ID + ")  REFERENCES " + TABLE_NAME_ANIMAL + "(" + COL_NAME_ID + ")" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ANIMAL + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WEIGHTS + ";");

        this.onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        super.onOpen(db);

        clear();
    }

    @Override
    public Animal readOrNull(@NonNull Long identifier) {

        final SQLiteDatabase db = getReadableDatabase();

        final Cursor animalCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_ANIMAL + " WHERE " + COL_NAME_ID + "=?;", new String[]{identifier.toString()});
        final Cursor weightCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_WEIGHTS + " WHERE " + COL_NAME_ID + "=?;", new String[]{identifier.toString()});

        if (animalCursor.moveToNext()) {

            long species = animalCursor.getLong(1);
            String name = animalCursor.getString(2);
            long birth = animalCursor.getLong(3);
            short sex = animalCursor.getShort(4);
            TreeMap<Date, Double> weights = new TreeMap<Date, Double>();

            while (animalCursor.moveToNext()) {

                long when = weightCursor.getLong(1);
                double weight = weightCursor.getDouble(2);

                weights.put(new Date(when), weight);
            }

            animalCursor.close();
            weightCursor.close();

            return new Animal(identifier, species, new Date(birth), name, Sex.fromShort(sex), weights);
        } else {
            animalCursor.close();
            weightCursor.close();

            return null;
        }
    }

    /**
     * Get array of instances that corresponds the identifier on each indices
     *
     * @param identifiers An array of identifiers
     * @return An array of found instances. Element of each array could be {Animal} instance or {null}.
     */
    public Animal[] readOrNull(@NonNull long[] identifiers) {

        Animal[] result = new Animal[identifiers.length];

        for (int i = 0; i < result.length; i++) {

            result[i] = readOrNull(identifiers[i]);
        }

        return result;
    }

    @Override
    public boolean tryWrite(@NonNull Animal animal) {

        final SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL(
                    "INSERT INTO " + TABLE_NAME_ANIMAL + " VALUES(" +
                            animal.getId() + ", " +
                            animal.getSpecies() + ", " +
                            '\'' + animal.getName() + '\'' +
                            animal.getBirthday().toMillis() + ", " +
                            animal.getSex().toShort() +
                            ");");
        } catch (SQLiteConstraintException e) {

            return false;
        }

        db.execSQL("DELETE FROM " + TABLE_NAME_WEIGHTS + " WHERE " + COL_NAME_ID + "=" + animal.getId() + ";");
        for (Map.Entry<Date, Double> entry : animal.getWeights().entrySet()) {

            long when = entry.getKey().toMillis();
            double weight = entry.getValue();

            try {
                db.execSQL("INSERT INTO " + TABLE_NAME_WEIGHTS + " VALUES(" + animal.getId() + ", " + when + ", " + weight + ");");
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void delete(@NonNull Long identifier) {

        final SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME_WEIGHTS + " WHERE " + COL_NAME_ID + "=" + identifier.toString() + ";");
        db.execSQL("DELETE FROM " + TABLE_NAME_ANIMAL + " WHERE " + COL_NAME_ID + "=" + identifier.toString() + ";");
    }

    @Override
    public void clear() {

        final SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME_ANIMAL + ";");
        db.execSQL("DELETE FROM " + TABLE_NAME_WEIGHTS + ";");
    }
}
