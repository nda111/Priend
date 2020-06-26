package com.gachon.priend.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.database.SQLiteCompatBase;
import com.gachon.priend.data.Sex;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.datetime.TimeSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * A class represents managed animal
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public final class Animal extends SQLiteCompatBase<Animal, Long, AnimalDatabaseHelper> implements IJsonConvertible, Parcelable {

    private static String JSON_KEY_ID = "id";
    private static String JSON_KEY_SPECIES = "species";
    private static String JSON_KEY_BIRTHDAY = "birthday";
    private static String JSON_KEY_NAME = "name";
    private static String JSON_KEY_SEX = "sex";
    private static String JSON_KEY_WEIGHTS = "weights";
    private static String JSON_KEY_WEIGHTS_DATE = "date";
    private static String JSON_KEY_WEIGHTS_VALUE = "value";

    public final static Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel source) {
            return new Animal(source);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    private long id = -1;
    private long species = -1;
    private Date birthday = null;
    private String name = null;
    private Sex sex = null;
    private TreeMap<Date, Double> weights = null;

    private Animal(Parcel in) {
        id = in.readLong();
        species = in.readLong();
        birthday = new Date(in.readLong());
        name = in.readString();
        sex = Sex.fromShort((short) in.readInt());

        int weightCount = in.readInt();
        long[] dateArray = new long[weightCount];
        double[] weightArray = new double[weightCount];
        in.readLongArray(dateArray);
        in.readDoubleArray(weightArray);

        weights = new TreeMap<Date, Double>();
        for (int i = 0; i < weightCount; i++) {
            weights.put(new Date(dateArray[i]), weightArray[i]);
        }
    }

    /**
     * Create an instance with its specifications
     *
     * @param id The ID of the animal
     * @param species The species ID of the animal
     * @param birthday The birthday of the animal
     * @param name the name of the animal
     * @param sex The sex of the animal
     * @param weights The weights of the animal in {TreeMap} type
     */
    public Animal(long id, long species, @NonNull Date birthday, @NonNull String name, @NonNull Sex sex, @NonNull TreeMap<Date, Double> weights) {

        this.id = id;
        this.species = species;
        this.birthday = birthday;
        this.name = name;
        this.sex = sex;
        this.weights = weights;
    }

    /**
     * Create an empty instance with default values
     */
    public Animal() {
        /* Empty */
    }

    /**
     * Get ID
     *
     * @return The ID
     */
    public long getId() {
        return id;
    }

    /**
     * Get species ID
     *
     * @return The species ID
     */
    public long getSpecies() {
        return species;
    }

    /**
     * Set species ID
     *
     * @param species The species ID
     */
    public void setSpecies(int species) {
        if (species < 0) {
            species = -1;
        } else {
            this.species = species;
        }
    }

    /**
     * Get the birthday
     *
     * @return The birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * Set the birthday
     *
     * @param birthday The birthday
     */
    public void setBirthday(Date birthday) {
        if (birthday != null) {
            this.birthday = birthday;
        }
    }

    /**
     * Get the name
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     *
     * @param name The name
     */
    public void setName(String name) {
        if (name != null) {
            name = name.trim();

            if (name.length() > 0) {
                this.name = name;
            }
        }
    }

    /**
     * Get sex
     *
     * @return Sex of animal containing if neutered or not
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Set sex value if not null
     *
     * @param sex Sex value
     */
    public void setSex(Sex sex) {
        if (sex != null) {
            this.sex = sex;
        }
    }

    /**
     * Get weight value at a specific date
     *
     * @param date A specific date to get a weight
     * @return Weight of the day or null
     */
    public Double getWeightAtOrNull(Date date) {
        if (weights.containsKey(date)) {
            return weights.get(date);
        } else {
            return null;
        }
    }

    /**
     * Get the last weight
     *
     * @return The last weight or null
     */
    public Double getLastWeightOrNull() {
        if (weights.size() != 0) {
            return weights.lastEntry().getValue();
        } else {
            return null;
        }
    }

    /**
     * Get all weight records in TreeMap<Date, Double> type.
     *
     * @return The map of weight associated with date
     */
    public TreeMap<Date, Double> getWeights() {
        return weights;
    }

    /**
     * Set every weights for animal with specified tree map
     *
     * @param weights The weight map
     */
    public void setWeights(@NonNull TreeMap<Date, Double> weights) {
        this.weights = weights;
    }

    /**
     * Set or update weight at a specific day
     *
     * @param date   The date measurement
     * @param weight Weight value at the day
     */
    public void setWeight(Date date, double weight) {
        if (date != null) {
            if (weights.containsKey(date)) {
                weights.replace(date, weight);
            } else {
                weights.put(date, weight);
            }
        }
    }

    /**
     * Get the age in TimeSpan
     *
     * @return The age
     */
    public TimeSpan getAge() {
        Date today = Date.getNow();
        return new TimeSpan(birthday, today);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray weightArray = new JSONArray();

        try {
            for (Map.Entry<Date, Double> entry : weights.entrySet()) {
                JSONObject dateWeightPair = new JSONObject();

                dateWeightPair.put(JSON_KEY_WEIGHTS_DATE, entry.getKey().toMillis());
                dateWeightPair.put(JSON_KEY_WEIGHTS_VALUE, entry.getValue());

                weightArray.put(dateWeightPair);
            }

            json.put(JSON_KEY_ID, id);
            json.put(JSON_KEY_SPECIES, species);
            json.put(JSON_KEY_BIRTHDAY, birthday.toMillis());
            json.put(JSON_KEY_NAME, name);
            json.put(JSON_KEY_SEX, sex.toShort());
            json.put(JSON_KEY_WEIGHTS, weightArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean readJson(JSONObject json) {
        try {
            long id = json.getLong(JSON_KEY_ID);
            long species = json.getLong(JSON_KEY_SPECIES);
            long birthday = json.getLong(JSON_KEY_BIRTHDAY);
            String name = json.getString(JSON_KEY_NAME);
            short sex = (short) json.getInt(JSON_KEY_SEX);
            JSONArray weightArray = json.getJSONArray(JSON_KEY_WEIGHTS);
            TreeMap<Date, Double> weights = new TreeMap<>();

            for (int i = 0; i < weightArray.length(); i++) {
                JSONObject pair = weightArray.getJSONObject(i);

                long date = pair.getLong(JSON_KEY_WEIGHTS_DATE);
                double value = (int)(pair.getDouble(JSON_KEY_WEIGHTS_VALUE) * 100) / 100.0;

                weights.put(new Date(date), value);
            }

            this.id = id;
            this.species = species;
            this.birthday = new Date(birthday);
            this.name = name;
            this.sex = Sex.fromShort(sex);
            this.weights = weights;

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(species);
        dest.writeLong(birthday.toMillis());
        dest.writeString(name);
        dest.writeInt(sex.toShort());

        long[] dateArray = new long[weights.size()];
        double[] weightArray = new double[weights.size()];
        int i = 0;
        for (Map.Entry<Date, Double> entry : weights.entrySet()) {
            dateArray[i++] = entry.getKey().toMillis();
            weightArray[i++] = entry.getValue();
        }

        dest.writeInt(dateArray.length);
        dest.writeLongArray(dateArray);
        dest.writeDoubleArray(weightArray);
    }

    @Override
    public void copyFrom(@NonNull Animal animal) {

        this.id = animal.id;
        this.species = animal.species;
        this.birthday = animal.birthday;
        this.name = animal.name;
        this.sex = animal.sex;
        this.weights = animal.weights;
    }
}
