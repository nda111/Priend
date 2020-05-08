package com.gachon.priend.membership;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A static class that manages auto login preferences
 *
 * @author 유근혁
 * @since May 5th 2020
 */
public final class AutoLoginManager {

    private static final String PREFERENCES_NAME = "AUTO_LOGIN";

    private static final String KEY_DO_AUTO_LOGIN = "do_auto_login";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static SharedPreferences preferences = null;

    private static void loadPreferencesIfNull(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Get login parameters
     *
     * @param context The context it runs
     * @return True, if do auto login, False, otherwise
     */
    public static String[] getValueOrNull(Context context) {
        loadPreferencesIfNull(context);

        if (preferences.getBoolean(KEY_DO_AUTO_LOGIN, false)) {
            return new String[]{
                    preferences.getString(KEY_EMAIL, null),
                    preferences.getString(KEY_PASSWORD, null)
            };
        } else {
            return null;
        }
    }

    /**
     * Set auto login value and parameters
     *
     * @param context The context it runs
     * @param doAutoLogin Weather do auto login or not
     * @param email The email of an account
     * @param password The password of an account corresponds to its email
     */
    public static void setValue(Context context, boolean doAutoLogin, String email, String password) {
        loadPreferencesIfNull(context);

        if (doAutoLogin) {
            preferences.edit()
                    .putBoolean(KEY_DO_AUTO_LOGIN, true)
                    .putString(KEY_EMAIL, email)
                    .putString(KEY_PASSWORD, password)
                    .apply();
        } else {
            preferences.edit()
                    .putBoolean(KEY_DO_AUTO_LOGIN, false)
                    .remove(KEY_EMAIL)
                    .remove(KEY_PASSWORD)
                    .apply();
        }
    }
}
