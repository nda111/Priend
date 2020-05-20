package com.gachon.priend.membership;

import android.content.Context;
import android.content.SharedPreferences;

import com.gachon.priend.data.entity.Account;

import org.json.JSONException;
import org.json.JSONObject;

public final class NowAccountManager {

    private static final String PREFERENCES_NAME = "NOW_ACCOUNT";

    private static final String KEY_ACCOUNT = "account";

    private static SharedPreferences preferences = null;

    private static void loadPreferencesIfNull(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    public static Account getAccountOrNull(Context context) {
        loadPreferencesIfNull(context);

        String jsonString = preferences.getString(KEY_ACCOUNT, null);
        if (jsonString == null) {
            return null;
        }

        Account account = new Account();
        try {
            account.readJson(new JSONObject(jsonString));
            return account;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setAccount(Context context, Account account) {
        loadPreferencesIfNull(context);

        if (account == null) {
            clear(context);
        } else {
            try {
                String json = account.toJson().toString(0);
                preferences.edit().putString(KEY_ACCOUNT, json).apply();
            } catch (JSONException e) {
                e.printStackTrace();
                clear(context);
            }
        }
    }

    public static void clear(Context context) {
        loadPreferencesIfNull(context);

        preferences.edit().clear().apply();
    }
}
