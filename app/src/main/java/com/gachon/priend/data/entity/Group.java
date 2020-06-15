package com.gachon.priend.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.database.GroupDatabaseHelper;
import com.gachon.priend.data.database.SQLiteCompatBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class represents group of managed animal
 *
 * @author 유근혁
 * @since May 4th 2020
 */
public final class Group extends SQLiteCompatBase<Group, Integer, GroupDatabaseHelper> implements IJsonConvertible, Parcelable {

    private static String JSON_KEY_ID = "id";
    private static String JSON_KEY_OWNER = "owner";
    private static String JSON_KEY_NAME = "name";

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    private int id = -1;
    private long ownerId = -1;
    private String name = null;

    private Group(Parcel in) {
        id = in.readInt();
        ownerId = in.readLong();
        name = in.readString();
    }

    public Group(int id, long ownerId, @NonNull String name) {

        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(ownerId);
        dest.writeString(name);
    }

    @Override
    public void copyFrom(@NonNull Group group) {

        this.id = group.id;
        this.ownerId = group.ownerId;
        this.name = group.name;
    }
}
