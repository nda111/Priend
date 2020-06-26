package com.gachon.priend.calendar.request;

import androidx.annotation.NonNull;

import com.gachon.priend.calendar.Memo;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class that represents memo insertion request
 *
 * @author 유근혁
 * @since May 23rd 2020
 */
public final class InsertMemoRequest extends RequestBase<InsertMemoRequest.EResponse> {

    public enum EResponse {
        OK(0), ACCOUNT_ERROR(1), SERVER_ERROR(2);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return ACCOUNT_ERROR;
                case 2:
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
    private Memo memo;

    public InsertMemoRequest(@NonNull Account account, long animalId, @NonNull Memo memo) {
        this.account = account;
        this.animalId = animalId;
        this.memo = memo;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/calendar/memo/insert";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        JSONObject json = memo.toJson();

        try {
            conn.sendAuthentication(account);
            conn.send(animalId);
            conn.send(json.toString(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = InsertMemoRequest.EResponse.fromId(message.getBinaryMessageOrNull()[0]);
        conn.close();
    }

    @Override
    protected void onClose() {
        /* Empty */
    }
}
