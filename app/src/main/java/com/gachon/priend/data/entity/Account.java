package com.gachon.priend.data.entity;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.Settings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class represents account
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public final class Account implements IJsonConvertible {

    private static String JSON_KEY_ID = "id";
    private static String JSON_KEY_NAME = "name";
    private static String JSON_KEY_EMAIL = "email";
    private static String JSON_KEY_AUTH_TOKEN = "authToken";
    private static String JSON_KEY_SETTINGS = "settings";

    private long id = -1;
    private String name = null;
    private String email = null;
    private String authenticationToken = null;
    private Settings settings = null;

    /**
     * Create an empty instance with default values
     */
    public Account() {
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
     * Get name
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name if not null and not empty
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
     * Get email
     *
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the authentication token
     *
     * @return The authentication token
     */
    public String getAuthenticationToken() {
        return authenticationToken;
    }

    /**
     * Get settings
     *
     * @return Settings information
     */
    public Settings getSettings() {
        return settings;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(JSON_KEY_ID, id);
            json.put(JSON_KEY_NAME, name);
            json.put(JSON_KEY_EMAIL, email);
            json.put(JSON_KEY_AUTH_TOKEN, authenticationToken);
            json.put(JSON_KEY_SETTINGS, settings.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean readJson(JSONObject json) {
        try {
            long id = json.getLong(JSON_KEY_ID);
            String name = json.getString(JSON_KEY_NAME);
            String email = json.getString(JSON_KEY_EMAIL);
            String authenticationToken = json.getString(JSON_KEY_AUTH_TOKEN);
            JSONObject settingsJson = json.getJSONObject(JSON_KEY_SETTINGS);
            Settings settings = new Settings();
            settings.readJson(settingsJson);

            this.id = id;
            this.name = name;
            this.email = email;
            this.authenticationToken = authenticationToken;
            this.settings = settings;

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }
}
