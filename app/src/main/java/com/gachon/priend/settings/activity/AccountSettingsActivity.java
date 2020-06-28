package com.gachon.priend.settings.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gachon.priend.R;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.membership.AutoLoginManager;
import com.gachon.priend.membership.NowAccountManager;
import com.gachon.priend.membership.activity.LoginEntryActivity;
import com.gachon.priend.settings.OnDeleteClickListener;
import com.gachon.priend.settings.dialog.DeleteAccountDialog;

public class AccountSettingsActivity extends AppCompatActivity {

    private EditText nameEditText;
    private ImageButton editNameImageButton;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        /*
         * Get my account instance
         */
        this.account = NowAccountManager.getAccountOrNull(this);

        /*
         * ActionBar
         */
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        /*
         * Initialize Components
         */
        final TextView emailTextView = findViewById(R.id.account_settings_text_view_email);
        nameEditText = findViewById(R.id.account_settings_text_view_edit_name);
        editNameImageButton = findViewById(R.id.account_settings_image_view_edit_btn);

        findViewById(R.id.account_settings_image_view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.account_settings_Linear_Layout_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NowAccountManager.clear(AccountSettingsActivity.this);
                AutoLoginManager.setValue(AccountSettingsActivity.this, false, null, null);

                final Intent intent = new Intent(AccountSettingsActivity.this, LoginEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.account_settings_Linear_Layout_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: reset password
            }
        });

        findViewById(R.id.account_settings_Linear_Layout_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteAccountDialog(AccountSettingsActivity.this, new OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick() {
                        // todo: delete account

                        // do below if success
                        NowAccountManager.clear(AccountSettingsActivity.this);
                        AutoLoginManager.setValue(AccountSettingsActivity.this, false, null, null);

                        final Intent intent = new Intent(AccountSettingsActivity.this, LoginEntryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
            }
        });

        emailTextView.setText(this.account.getEmail());
        nameEditText.setText(this.account.getName());
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // todo: request change name


                    editNameImageButton.setEnabled(true);
                    nameEditText.setEnabled(false);
                }

                return false;
            }
        });

        editNameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameImageButton.setEnabled(false);
                nameEditText.setEnabled(true);
            }
        });
    }
}