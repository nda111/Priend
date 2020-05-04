package com.gachon.priend.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class represents settings
 */
public class Settings implements IJsonConvertible {

    private static String JSON_KEY_WEIGHT_ALERT = "weight";
    private static String JSON_KEY_BIRTHDAY_ALERT = "birthday";
    private static String JSON_KEY_EVENT_ALERT = "event";
    private static String JSON_KEY_COMMENT_ALERT = "comment";

    /**
     * Weather make weight alert or not
     */
    public boolean doWeightAlert = false;

    /**
     * Weather make birthday alert or not
     */
    public boolean doBirthdayAlert = false;

    /**
     * Weather make event alert or not
     */
    public boolean doEventAlert = false;

    /**
     * Weather make comment alert or not
     */
    public boolean doCommentAlert = false;

    /**
     * Create an empty instance with default values
     */
    public Settings() {
        /* EMPTy */
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(JSON_KEY_WEIGHT_ALERT, doWeightAlert);
            json.put(JSON_KEY_BIRTHDAY_ALERT, doBirthdayAlert);
            json.put(JSON_KEY_EVENT_ALERT, doEventAlert);
            json.put(JSON_KEY_COMMENT_ALERT, doCommentAlert);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean readJson(JSONObject json) {
        try {
            boolean weight = json.getBoolean(JSON_KEY_WEIGHT_ALERT);
            boolean birthday = json.getBoolean(JSON_KEY_BIRTHDAY_ALERT);
            boolean event = json.getBoolean(JSON_KEY_EVENT_ALERT);
            boolean comment = json.getBoolean(JSON_KEY_COMMENT_ALERT);

            doWeightAlert = weight;
            doBirthdayAlert = birthday;
            doEventAlert = event;
            doCommentAlert = comment;

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }
}
