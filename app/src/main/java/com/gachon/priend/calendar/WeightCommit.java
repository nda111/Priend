package com.gachon.priend.calendar;

import androidx.annotation.NonNull;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Animal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class that represents a commit of weight changes
 *
 * @author 유근혁
 * @since June 2nd 2020
 */
public final class WeightCommit implements IJsonConvertible {

    /**
     * A class that represents change of weight
     *
     * @author 유근혁
     * @since June 2nd 2020
     */
    private static final class Change implements IJsonConvertible {

        private static final String JSON_KEY_TYPE = "type";
        private static final String JSON_KEY_DATE = "date";
        private static final String JSON_KEY_WEIGHT = "weight";

        private static final String JSON_VALUE_UPSERT = "ups";
        private static final String JSON_VALUE_DELETE = "del";

        /**
         * A enumeration that represents the type of change
         *
         * @author 유근혁
         * @since June 2nd 2020
         */
        public enum Type {

            INSERT, UPDATE, DELETE
        }

        /**
         * The type of change
         */
        public Type type;
        /**
         * The date when the weight was measured
         */
        public Date date;
        /**
         * the weight before change
         */
        public Double prevWeight;
        /**
         * The weight after change
         */
        public Double weight;

        /**
         * Create an object with null values
         */
        public Change() {

            type = null;
            date = null;
            prevWeight = null;
            weight = null;
        }

        /**
         * Create an instance with type and date
         *
         * @param type The type of this change
         * @param date The date when the weight was measured
         */
        public Change(@NonNull Type type, @NonNull Date date) {

            this.type = type;
            this.date = date;
            this.prevWeight = null;
            this.weight = null;
        }

        @Override
        public JSONObject toJson() {

            JSONObject json = new JSONObject();

            if (date == null) {
                return null;
            }

            try {

                switch (type) {

                    case INSERT:
                    case UPDATE:

                        if (weight == null) {
                            return null;
                        }

                        json.put(JSON_KEY_TYPE, JSON_VALUE_UPSERT);
                        json.put(JSON_KEY_DATE, date.toMillis());
                        json.put(JSON_KEY_WEIGHT, weight);
                        break;

                    case DELETE:
                        json.put(JSON_KEY_TYPE, JSON_VALUE_DELETE);
                        json.put(JSON_KEY_DATE, date.toMillis());
                        break;

                    default:
                        return null;
                }

                return json;
            } catch (JSONException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        public boolean readJson(JSONObject json) {

            try {

                String typeString = json.getString(JSON_KEY_TYPE);
                long dateInMillis = json.getLong(JSON_KEY_DATE);

                if (typeString.equals(JSON_VALUE_UPSERT)) {

                    this.date = new Date(dateInMillis);
                    this.type = Type.INSERT;
                    this.weight = json.getDouble(JSON_KEY_WEIGHT);
                } else if (typeString.equals(JSON_VALUE_DELETE)) {

                    this.date = new Date(dateInMillis);
                    this.type = Type.DELETE;
                } else {

                    return false;
                }

                return true;
            } catch (JSONException e) {

                e.printStackTrace();

                return false;
            }
        }
    }

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_CHANGES = "changes";

    private long animalId;
    private TreeMap<Date, Double> original;
    private TreeMap<Date, Double> applied;
    private HashMap<Date, Change> changes;

    /**
     * Initialize animal id and weight map with Animal instance
     *
     * @param data The tree data of weight
     * @param animalId The specifier of an animal
     */
    public WeightCommit(@NonNull TreeMap<Date, Double> data, long animalId) {

        this.animalId = animalId;
        this.original = data;

        this.changes = new HashMap<Date, Change>();
        applied = new TreeMap<Date, Double>();
        for (Map.Entry<Date, Double> entry : original.entrySet()) {
            applied.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets the changes associated with the date
     *
     * @return HashMap object of changes associated with date
     */
    public HashMap<Date, Change> getChanges() {
        return changes;
    }

    /**
     * Insert or update weight with date wnd weight value
     *
     * @param date The date when the weight was measured
     * @param weight The weight value of the day
     */
    public void insertOrUpdate(@NonNull Date date, double weight) {

        if (changes.containsKey(date)) {

            Change change = changes.get(date);
            switch (change.type) {

                case INSERT:
                    change.weight = weight;
                    break;

                case UPDATE:
                    if (change.weight != weight) {
                        if (change.prevWeight == weight) {
                            changes.remove(date);
                        } else {
                            change.weight = weight;
                        }
                    }
                    break;

                case DELETE:
                    if (change.prevWeight == weight) {
                        changes.remove(date);
                    } else {
                        change.type = Change.Type.UPDATE;
                        change.weight = weight;
                    }
                    break;
            }
        } else {

            Double weightOrNull = original.get(date);

            Change change;
            if (weightOrNull == null) {
                change = new Change(Change.Type.INSERT, date);
            } else {
                change = new Change(Change.Type.UPDATE, date);
                change.prevWeight = weightOrNull;

            }
            change.weight = weight;

            changes.put(date, change);
        }

        applied.put(date, weight);
    }

    /**
     * Delete the weight with date
     *
     * @param date The date when the weight was measured
     * @return True if successfully deleted, False if there is no weight that associated with the date
     */
    public boolean delete(@NonNull Date date) {

        if (changes.containsKey(date)) {

            Change change = changes.get(date);

            switch (change.type) {

                case INSERT:
                    changes.remove(date);
                    break;

                case UPDATE:
                    change.type = Change.Type.DELETE;
                    change.weight = null;
                    break;

                case DELETE:
                    return false;
            }
        } else {

            if (original.containsKey(date)) {
                Change change = new Change(Change.Type.DELETE, date);
                change.prevWeight = original.get(date);

                changes.put(date, change);
            } else {
                return false;
            }
        }

        applied.remove(date);
        return true;
    }

    /**
     * Gets weights before change with corresponding dates
     *
     * @return The map instance with weight before changed
     */
    public TreeMap<Date, Double> getOriginal() {
        return original;
    }

    /**
     * Gets weights after change with corresponding dates
     *
     * @return The map instance with weight after changed
     */
    public TreeMap<Date, Double> getApplied() {
        return applied;
    }

    @Override
    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            JSONArray changeArray = new JSONArray();
            for (Change change : changes.values()) {
                changeArray.put(change.toJson());
            }

            json.put(JSON_KEY_ID, animalId);
            json.put(JSON_KEY_CHANGES, changeArray);

            return json;
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    @Deprecated
    @Override
    public boolean readJson(JSONObject json) {
        return false;
    }
}
