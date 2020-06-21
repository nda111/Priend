package com.gachon.priend.calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.datetime.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;

/**
 * A class that represents memo for animal
 *
 * @author 유근혁
 * @since May 19th 2020
 */
public final class Memo implements IJsonConvertible, Parcelable {

    /**
     * A class that represents commitment for change of a memo
     *
     * @author 유근혁
     * @since May 22nd 2020
     */
    public static class Commit implements IJsonConvertible {

        private Memo memo;

        private long id;
        private String title = null;
        private String content = null;
        private Bitmap photo = null;

        /**
         * Create an instance with memos of before change and after change
         * @param origin Memo before change
         * @param changed Memo after change
         */
        public Commit(Memo origin, Memo changed) {

            this.memo = changed;
            this.id = origin.id;

            if (!origin.title.equals(changed.title)) {
                this.title = changed.title;
            }

            if (!origin.text.equals(changed.text)) {
                this.content = changed.text;
            }

            if (origin.getPhoto() != null) {
                if (!origin.getPhoto().sameAs(changed.photo)) {
                    this.photo = changed.photo;
                }
            }
        }

        /**
         * Get a memo after change
         *
         * @return Memo after change
         */
        public Memo toMemo() {
            return memo;
        }

        /**
         * Return weather this commit has changes\
         *
         * @return True, if have changes, false, otherwise
         */
        public boolean haveChanges() {
            return title != null || content != null || photo != null;
        }

        @Override
        public JSONObject toJson() {

            JSONObject json = new JSONObject();

            try {
                json.put(JSON_KEY_ID, id);

                if (title != null) {
                    json.put(JSON_KEY_TITLE, title);
                }

                if (content != null) {
                    json.put(JSON_KEY_CONTENT, content);
                }

                if (photo != null) {
                    json.put(JSON_KEY_PHOTO, bitmapToBinaryString(photo));
                }

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

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_WHEN = "when";
    private static final String JSON_KEY_TITLE = "title";
    private static final String JSON_KEY_CONTENT = "content";
    private static final String JSON_KEY_PHOTO = "photo";

    private static final String HexDigits = "0123456789ABCDEF";

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel source) {
            return new Memo(source);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    private static String bitmapToBinaryString(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);

        StringBuilder resultBuilder = new StringBuilder();

        for (byte b : buffer.array()) {
            resultBuilder.append(HexDigits.charAt(b >> 4));
            resultBuilder.append(HexDigits.charAt(b & 0xF));
        }

        return resultBuilder.toString();
    }

    private static Bitmap binaryStringToBitmap(String binaryString) {
        if (binaryString == null) {
            return null;
        } else {
            int length = binaryString.length();
            byte[] data = new byte[length / 2];

            for (int i = 0; i < length / 2; i++) {
                data[i] = (byte) ((Character.digit(binaryString.charAt(i), 16) << 4) + Character.digit(binaryString.charAt(i + 1), 16));
            }

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    private long id = -1;
    private Date when = null;
    private String title = null;
    private String text = null;
    private Bitmap photo = null;

    private Memo(Parcel in) {
        this.id = in.readLong();
        this.when = new Date(in.readLong());
        this.title = in.readString();
        this.text = in.readString();

        //Log.d("CalendarSelectionActivity", "Remaining: " + in.dataAvail());
        //if (in.dataAvail() > 0) {
        //    this.photo = in.readTypedObject(Bitmap.CREATOR);
        //} else {
        //    this.photo = null;
        //}
    }

    /**
     * Create an memo instance with default values
     */
    public Memo() {
        /* Empty */
    }

    /**
     * Create an memo instance with id, when, text and photo
     *
     * @param when  The date when the memo targets
     * @param text  The text content
     * @param photo The photo content which could be {null}
     */
    public Memo(@NonNull Date when, @NonNull String title, @NonNull String text, Bitmap photo) {
        this.id = -1;
        this.when = when;
        this.title = title;
        this.text = text;
        this.photo = photo;
    }

    /**
     * Get ID of memo
     *
     * @return The ID of the memo
     */
    public long getId() {
        return id;
    }

    /**
     * Get the date about memo
     *
     * @return The date about memo
     */
    public Date getWhen() {
        return when;
    }

    /**
     * Get the title of memo
     *
     * @return The title of memo
     */
    public String getTitle() {
        return title == null ? "" : title;
    }

    /**
     * Set the title of memo
     *
     * @param title The title of memo
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the text content
     *
     * @return The text content
     */
    public String getText() {
        return text == null ? "" : text;
    }

    /**
     * Set the text content
     *
     * @param text The text content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get the photo content
     *
     * @return The photo content
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Set the photo content
     *
     * @param photo The photo content
     */
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(JSON_KEY_ID, id);
            json.put(JSON_KEY_WHEN, when.toMillis());
            json.put(JSON_KEY_TITLE, title);
            json.put(JSON_KEY_CONTENT, text);

            if (photo != null) {
                json.put(JSON_KEY_PHOTO, bitmapToBinaryString(photo));
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

        return json;
    }

    @Override
    public boolean readJson(JSONObject json) {
        try {
            long id = json.getLong(JSON_KEY_ID);
            long when = json.getLong(JSON_KEY_WHEN);
            String title = json.getString(JSON_KEY_TITLE);
            String text = json.getString(JSON_KEY_CONTENT);
            Bitmap photo = null;

            if (json.has(JSON_KEY_PHOTO)) {
                String photoString = json.getString(JSON_KEY_PHOTO);
                photo = binaryStringToBitmap(photoString);
            }

            this.id = id;
            this.when = new Date(when);
            this.title = title;
            this.text = text;
            this.photo = photo;

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
        dest.writeLong(when.toMillis());
        dest.writeString(title);
        dest.writeString(text);
        if (photo != null) {
            dest.writeTypedObject(photo, flags);
        }
    }
}
