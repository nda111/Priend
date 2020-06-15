package com.gachon.priend.calendar.request;

import androidx.annotation.NonNull;

import com.gachon.priend.calendar.Memo;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * A class that represents request that downloads list of memo of an animal
 *
 * @author 유근혁
 * @since May 21st 2020
 */
public class MemoListRequest extends RequestBase<MemoListRequest.EResponse> {

    public enum EResponse {
        SERVER_ERROR(0), ACCOUNT_ERROR(1), BEGIN_LIST(2), OK(3);

        public static EResponse fromId(int id) {
            switch (id) {
                case 0:
                    return SERVER_ERROR;
                case 1:
                    return ACCOUNT_ERROR;
                case 2:
                    return BEGIN_LIST;
                case 3:
                    return OK;
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
    private Animal animal;
    private Date when;
    private LinkedList<Memo> memoList = null;

    public MemoListRequest(@NonNull Account account, @NonNull Animal animal, @NonNull Date when) {
        this.account = account;
        this.animal = animal;
        this.when = when;
    }

    @Override
    protected String getUri() {
        return SERVER_ADDRESS + "/ws/calendar/memos";
    }

    @Override
    protected void onRequest(WebSocketRequest conn) {
        conn.sendAuthentication(account);
        conn.send(animal.getId());
        conn.send(when.toMillis());
    }

    @Override
    protected void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
        if (paramNumber == 0) {
            super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);

            switch (super.response) {

                case SERVER_ERROR:
                case ACCOUNT_ERROR:
                    conn.close();

                case BEGIN_LIST:
                    memoList = new LinkedList<Memo>();
                    break;

                default:
                    break;
            }
        } else if (message.getTextMessageOrNull() != null) {

            String jsonString = message.getTextMessageOrNull();
            try {

                JSONObject json = new JSONObject(jsonString);

                Memo memo = new Memo();
                if (memo.readJson(json)) {
                    memoList.addLast(memo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            super.response = EResponse.fromId(message.getBinaryMessageOrNull()[0]);
            super.args = new Object[]{ memoList };
            conn.close();
        }
    }

    @Override
    protected void onClose() {
        /* Empty */
    }
}
