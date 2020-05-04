package com.gachon.priend.data.entity;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.Sex;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.datetime.TimeSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * A class represents managed animal
 */
public final class Animal implements IJsonConvertible {

    private static String JSON_KEY_ID = "id";
    private static String JSON_KEY_SPECIES = "species";
    private static String JSON_KEY_BIRTHDAY = "birthday";
    private static String JSON_KEY_NAME = "name";
    private static String JSON_KEY_SEX = "sex";
    private static String JSON_KEY_WEIGHTS = "weights";
    private static String JSON_KEY_WEIGHTS_DATE = "date";
    private static String JSON_KEY_WEIGHTS_VALUE = "value";

    private int id = -1;
    private int species = -1;
    private Date birthday = null;
    private String name = null;
    private Sex sex = null;
    private TreeMap<Date, Double> weights = null;

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
    public int getId() {
        return id;
    }

    /**
     * Get species ID
     *
     * @return The species ID
     */
    public int getSpecies() {
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
     * Set or update weight at a specific day
     *
     * @param date The date measurement
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
            int id = json.getInt(JSON_KEY_ID);
            int species = json.getInt(JSON_KEY_SPECIES);
            long birthday = json.getLong(JSON_KEY_BIRTHDAY);
            String name = json.getString(JSON_KEY_NAME);
            short sex = (short)json.getInt(JSON_KEY_SEX);
            JSONArray weightArray = json.getJSONArray(JSON_KEY_WEIGHTS);
            TreeMap<Date, Double> weights = new TreeMap<>();

            for (int i = 0; i < weightArray.length(); i++) {
                JSONObject pair = weightArray.getJSONObject(i);

                long date = pair.getLong(JSON_KEY_WEIGHTS_DATE);
                double value = pair.getDouble(JSON_KEY_WEIGHTS_VALUE);

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
}
