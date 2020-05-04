package com.gachon.priend.data;

import org.json.JSONObject;

/**
 * An interface represents object that is able to converted to JSON object.
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public interface IJsonConvertible {

    /**
     * Converts it into a JSON object.
     *
     * @return Converted JSON object
     */
    JSONObject toJson();

    /**
     * Read date from JSON Object.
     *
     * @param json JSON object to read.
     * @return True if succeed, False otherwise.
     */
    boolean readJson(JSONObject json);
}
