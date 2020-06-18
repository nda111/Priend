package com.gachon.priend.data.database;

import androidx.annotation.NonNull;

import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class SpeciesListRequest extends RequestBase<SpeciesListRequest.EResponse> {

    public static class Species {

        public long id;
        public String en_us;
        public String ko_kr;

        public Species(long id, @NonNull String en_us, @NonNull String ko_kr) {
            this.id = id;
            this.en_us = en_us;
            this.ko_kr = ko_kr;
        }
    }

    public enum EResponse {
        OK(0), SERVER_ERROR(1);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return SERVER_ERROR;
                default:
                    return null;
            }
        }

        private byte id = -1;

        EResponse(int id) {
            this.id = (byte) id;
        }
    }

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_EN_US = "en_us";
    private static final String JSON_KEY_KO_KR = "ko_kr";

    private LinkedList<Species> species;

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/data/species";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        /* EMPTY */
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        switch (paramNumber) {

            case 0:
                super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
                if (super.response != EResponse.OK) {
                    conn.close();
                } else {
                    species = new LinkedList<Species>();
                }
                break;

            case 1:
                final String jsonString = message.getTextMessageOrNull();
                try {
                    final JSONArray array = new JSONArray(jsonString);
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject set = array.getJSONObject(i);

                        final long id = set.getLong(JSON_KEY_ID);
                        final String en_us = set.getString(JSON_KEY_EN_US);
                        final String ko_kr = set.getString(JSON_KEY_KO_KR);

                        species.addLast(new Species(id, en_us, ko_kr));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                args = new Object[]{species};
                conn.close();
                break;
        }
    }

    @Override
    protected void onClose() {
        /* EMPTY */
    }
}
