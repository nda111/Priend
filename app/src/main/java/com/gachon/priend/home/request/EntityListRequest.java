package com.gachon.priend.home.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.data.entity.Group;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class EntityListRequest extends RequestBase<EntityListRequest.EResponse> {

    public enum EResponse {
        OK(0), BEGIN_GROUP(1), END_OF_GROUP(2), END_OF_LIST(3), ACCOUNT_ERROR(4), SERVER_ERROR(5);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return BEGIN_GROUP;
                case 2:
                    return END_OF_GROUP;
                case 3:
                    return END_OF_LIST;
                case 4:
                    return ACCOUNT_ERROR;
                case 5:
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

    private Account account;

    private LinkedList<Group> groups = null;
    private HashMap<Group, ArrayList<Animal>> entries = null;

    private Group nowGroup = null;
    private ArrayList<Animal> nowAnimals = null;

    private boolean waitGroup = false;

    public EntityListRequest(@NonNull Account account) {
        this.account = account;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/home/entity/list";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        if (paramNumber == 0) {

            super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
            if (super.response != EResponse.OK) {
                conn.close();
            } else {
                groups = new LinkedList<>();
                entries = new HashMap<>();
            }
        } else if (message.getBinaryMessageOrNull() != null) {

            final EResponse resp = EResponse.fromId(message.getBinaryMessageOrNull()[0]);

            switch (resp) {

                case BEGIN_GROUP:
                    waitGroup = true;
                    nowGroup = null;
                    nowAnimals = null;
                    break;

                case END_OF_GROUP:
                    groups.addLast(nowGroup);
                    entries.put(nowGroup, nowAnimals);
                    break;

                case END_OF_LIST:
                    super.args = new Object[]{groups, entries};
                    conn.close();
                    break;
            }
        } else {

            final String jsonString = message.getTextMessageOrNull();

            if (waitGroup) {
                try {
                    final Group group = new Group();
                    group.readJson(new JSONObject(jsonString));

                    nowGroup = group;
                    nowAnimals = new ArrayList<Animal>();

                    waitGroup = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    final Animal animal = new Animal();
                    animal.readJson(new JSONObject(jsonString));

                    nowAnimals.add(animal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onClose() {
        /* EMPTY */
    }
}
