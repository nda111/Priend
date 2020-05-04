package com.gachon.priend.data.entity;

import com.gachon.priend.data.IJsonConvertible;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class represents group of managed animal
 */
public final class Group implements IJsonConvertible {

    private static String JSON_KEY_ID = "id";
    private static String JSON_KEY_OWNER = "owner";
    private static String JSON_KEY_NAME = "name";

    private int id = -1;
    private long ownerId = -1;
    private String name = null;

    /**
     * Create an empty instance with default values
     */
    public Group() {
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
     * Get the ID of the owner
     *
     * @return The ID of the owner
     */
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * Get name
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(JSON_KEY_ID, id);
            json.put(JSON_KEY_OWNER, ownerId);
            json.put(JSON_KEY_NAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean readJson(JSONObject json) {
        try {
            int id = json.getInt(JSON_KEY_ID);
            long ownerId = json.getLong(JSON_KEY_OWNER);
            String name = json.getString(JSON_KEY_NAME);

            this.id = id;
            this.ownerId = ownerId;
            this.name = name;

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }
}
