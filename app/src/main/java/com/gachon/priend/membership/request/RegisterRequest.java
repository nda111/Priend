package com.gachon.priend.membership.request;

import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

/**
 * A class that requests account register
 *
 * @author 유근혁
 * @since 8th May 2020
 */
public final class RegisterRequest extends RequestBase<RegisterRequest.EResponse> {

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

    private String email = null;
    private String password = null;
    private String name = null;

    /**
     * Create an instance with email, password of an account and your name
     * @param email Email of an account
     * @param password Password of an account
     * @param name Your name
     */
    public RegisterRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/membership/register";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.send(email);
        conn.send(password);
        conn.send(name);
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
    }

    @Override
    protected void onClose() {
        /* Empty */
    }
}
