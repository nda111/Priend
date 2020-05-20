package com.gachon.priend.membership.request;

import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

/**
 * A class that requests register information of an account with email address
 *
 * @author 유근혁
 * @since 8th May 2020
 */
public final class EvaluationRequest extends RequestBase<EvaluationRequest.EResponse> {

    public enum EResponse {
        VERIFIED(0), NOT_VERIFIED(1), UNKNOWN(2), SERVER_ERROR(3);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return VERIFIED;
                case 1:
                    return NOT_VERIFIED;
                case 2:
                    return UNKNOWN;
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

    /**
     * Create an instance with email of an account
     * @param email Email of an account
     */
    public EvaluationRequest(String email) {
        this.email = email;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/membership/evaluation";
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