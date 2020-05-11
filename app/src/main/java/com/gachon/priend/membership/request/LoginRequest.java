package com.gachon.priend.membership.request;

import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class that requests login with email and password
 *
 * @author 유근혁
 * @since 8th May 2020
 */
public final class LoginRequest extends RequestBase<LoginRequest.EResponse> {

    public enum EResponse {
        OK(0), WRONG_PASSWORD(1), UNKNOWN_EMAIL(2), SERVER_ERROR(3);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return OK;
                case 1:
                    return WRONG_PASSWORD;
                case 2:
                    return UNKNOWN_EMAIL;
                case 3:
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

    private String email;
    private String password;

    /**
     * Create an instance with email and password of an account
     * @param email Email of an account
     * @param password Password of an account
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/membership/login";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.send(email);
        conn.send(password);
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        if (paramNumber == 0) {
            super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);

            if (super.response != EResponse.OK)
            {
                conn.close();
            }
        } else {
            try {
                Account account = new Account();
                account.readJson(new JSONObject(message.toString()));

                args = new Object[] { account };
            } catch (JSONException e) {
                e.printStackTrace();
                super.response = EResponse.SERVER_ERROR;
            }

            conn.close();
        }
    }

    @Override
    protected void onClose() {
        /* Empty */
    }
}
