package com.gachon.priend.calendar.request;

import android.content.Context;

import androidx.annotation.NonNull;

import com.gachon.priend.calendar.WeightCommit;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;
import com.gachon.priend.membership.NowAccountManager;

import org.json.JSONException;

/**
 * A class that requests to commit weight changes for specified animal
 *
 * @author 유근혁
 * @since 9th Jun 2020
 */
public class CommitWeightRequest extends RequestBase<CommitWeightRequest.EResponse> {

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
    private WeightCommit commit;

    public CommitWeightRequest(@NonNull final Context context, @NonNull final WeightCommit commit) {

        this.account = NowAccountManager.getAccountOrNull(context);
        this.commit = commit;
    }

    @Override
    protected String getUri() {

        return SERVER_ADDRESS + "/ws/calendar/weight/commit";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {

        conn.sendAuthentication(account);
        try {
            conn.send(commit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
        conn.close();
    }

    @Override
    protected void onClose() {
        /* EMPTY */
    }
}
