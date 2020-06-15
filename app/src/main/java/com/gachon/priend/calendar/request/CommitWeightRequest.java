package com.gachon.priend.calendar.request;

import android.content.Context;

import androidx.annotation.NonNull;

import com.gachon.priend.calendar.WeightCommit;
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

    private Context mContext;
    private WeightCommit commit;

    public CommitWeightRequest(@NonNull final Context context, @NonNull final WeightCommit commit) {

        this.commit = commit;
    }

    @Override
    protected String getUri() {

        return SERVER_ADDRESS + "/ws/calendar/weight/commit";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {

        conn.sendAuthentication(NowAccountManager.getAccountOrNull(mContext));
        try {
            conn.send(commit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {

        switch (paramNumber) {
            case 0:

                super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);

                if (super.response == EResponse.SERVER_ERROR) {
                    conn.close();
                }
                break;

            case 1:

                super.args = new Object[] { message.getAsInt32MessageOrNull() };
                conn.close();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onClose() {

    }
}
