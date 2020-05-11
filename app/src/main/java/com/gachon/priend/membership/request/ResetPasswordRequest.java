package com.gachon.priend.membership.request;

import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

/**
 * A class that requests reset password of an account
 *
 * @author 유근혁
 * @since 8th May 2020
 */
public final class ResetPasswordRequest extends RequestBase<ResetPasswordRequest.EResponse> {

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

    /**
     * Create an instance with email of an account to reset
     * @param email Email of an account
     */
    public ResetPasswordRequest(String email) {
        this.email = email;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/membership/reset";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.send(email);
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
        conn.close();
    }

    @Override
    protected void onClose() {
        /* Empty */
    }
}
