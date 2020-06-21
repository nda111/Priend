package com.gachon.priend.calendar.request;

import androidx.annotation.NonNull;

import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

/**
 * A class that represents memo deletion request
 *
 * @author 유근혁
 * @since May 23rd 2020
 */
public final class DeleteMemoRequest extends RequestBase<DeleteMemoRequest.EResponse> {

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
    private long memoId;

    public DeleteMemoRequest(@NonNull Account account, long animalId, long memoId) {
        this.account = account;
        this.animalId = animalId;
        this.memoId = memoId;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/calendar/memo/delete";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
        conn.send(animalId);
        conn.send(memoId);
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
