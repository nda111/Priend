package com.gachon.priend;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.AutoLoginManager;
import com.gachon.priend.membership.NowAccountManager;
import com.gachon.priend.membership.activity.LoginEntryActivity;
import com.gachon.priend.membership.request.LoginRequest;

/**
 * An activity that tries auto-login or goes to {LoginEntryActivity}
 *
 * @author 유근혁
 * @since 7th May 2020
 */
public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        String[] parameters = AutoLoginManager.getValueOrNull(this);

        if (parameters != null && parameters[0] != null && parameters[1] != null) {
            new LoginRequest(parameters[0], parameters[1]).request(new RequestBase.ResponseListener<LoginRequest.EResponse>() {
                @Override
                public void onResponse(LoginRequest.EResponse response, Object[] args) {
                    switch (response) {
                        case OK:
                            Account account = (Account) args[0];
                            NowAccountManager.setAccount(EntryActivity.this, account);

                            gotoActivity(HomeActivity.class);
                            break;

                        case WRONG_PASSWORD:
                        case UNKNOWN_EMAIL:
                        case SERVER_ERROR:
                        default:
                            gotoActivity(LoginEntryActivity.class);
                            break;
                    }
                }
            });
        } else {
            gotoActivity(LoginEntryActivity.class);
        }
    }

    /**
     * Go to specified new activity, clearing tasks
     *
     * @param cls The type of the specified activity
     */
    private void gotoActivity(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}
