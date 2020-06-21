package com.gachon.priend.home.request;

import androidx.annotation.NonNull;

import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

public final class EditAnimalRequest extends RequestBase<EditAnimalRequest.EResponse> {

    public enum EResponse {
        OK(0), UNKNOWN_ANIMAL(1), PASSWORD_ERROR(2), ACCOUNT_ERROR(3), SERVER_ERROR(4);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return UNKNOWN_ANIMAL;
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
    private long animalId;
    private String name;
    private long birthday;
    private short sex;
    private long species;
    private String password;

    public EditAnimalRequest(@NonNull Account account, long animalId, @NonNull String name, long birthday, short sex, long species, @NonNull String password) {
        this.account = account;
        this.animalId = animalId;
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.species = species;
        this.password = password;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/home/entity/animal/edit";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
        conn.send(animalId);
        conn.send(password);
        conn.send(name);
        conn.send(birthday);
        conn.send(sex);
        conn.send(species);
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
