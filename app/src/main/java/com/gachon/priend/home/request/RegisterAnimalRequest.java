package com.gachon.priend.home.request;

import androidx.annotation.NonNull;

import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

public final class RegisterAnimalRequest extends RequestBase<RegisterAnimalRequest.EResponse> {

    public enum EResponse {
        OK(0), UNKNOWN_GROUP(1), PASSWORD_ERROR(2),  ACCOUNT_ERROR(3), SERVER_ERROR(4);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return UNKNOWN_GROUP;
                case 2:
                    return PASSWORD_ERROR;
                case 3:
                    return ACCOUNT_ERROR;
                case 4:
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
    private int groupId;
    private String name;
    private long birthday;
    private short sex;
    private long species;
    private String weightString;
    private String password;

    public RegisterAnimalRequest(@NonNull Account account, int groupId, @NonNull String name, long birthday, short sex, long species, @NonNull String weightString, @NonNull String password) {
        this.account = account;
        this.groupId = groupId;
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.species = species;
        this.weightString = weightString;
        this.password = password;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/home/entity/animal/register";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
        conn.send(groupId);
        conn.send(password);
        conn.send(name);
        conn.send(birthday);
        conn.send(sex);
        conn.send(species);
        conn.send(weightString);
        conn.send(Date.getNow().toMillis());
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
        conn.close();
    }

    @Override
    protected void onClose() {
        /*EMPTY*/
    }
}
