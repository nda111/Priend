package com.gachon.priend.settings.request;

import androidx.annotation.NonNull;

import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;
import com.gachon.priend.membership.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public final class ChangeNameRequest extends RequestBase<ChangeNameRequest.EResponse> {


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
    private String name;

    public ChangeNameRequest(@NonNull Account account, String name) {
        this.account = account;
        this.name = name;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/settings/account/change";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
        conn.send(name);
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = ChangeNameRequest.EResponse.fromId(message.getBinaryMessageOrNull()[0]);
        conn.close();
    }

    @Override
    protected void onClose() {
        /* EMPTY */
    }
}
