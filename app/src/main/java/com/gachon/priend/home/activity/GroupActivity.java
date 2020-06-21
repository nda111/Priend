package com.gachon.priend.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.home.request.CreateGroupRequest;
import com.gachon.priend.home.request.JoinGroupRequest;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class GroupActivity extends AppCompatActivity {

    private MaterialRadioButton joinRadioButton;
    private MaterialRadioButton createRadioButton;
    private TextInputLayout idContainer;
    private TextInputEditText idEditText;
    private TextInputLayout joinPasswordContainer;
    private TextInputEditText joinPasswordEditText;
    private TextInputLayout nameContainer;
    private TextInputEditText nameEditText;
    private TextInputLayout createPasswordContainer;
    private TextInputEditText createPasswordEditText;

    private View[] joinPanel;
    private View[] createPanel;
    private View[] radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.secondaryColor));

        /*
         * Initialize GUI Components
         */
        joinRadioButton = findViewById(R.id.group_radio_button_join);
        createRadioButton = findViewById(R.id.group_radio_button_create);
        idContainer = findViewById(R.id.group_edit_text_id_container);
        idEditText = findViewById(R.id.group_edit_text_id);
        joinPasswordContainer = findViewById(R.id.group_edit_text_password_join_container);
        joinPasswordEditText = findViewById(R.id.group_edit_text_password_join);
        nameContainer = findViewById(R.id.group_edit_text_name_container);
        nameEditText = findViewById(R.id.group_edit_text_name);
        createPasswordContainer = findViewById(R.id.group_edit_text_password_create_container);
        createPasswordEditText = findViewById(R.id.group_edit_text_password_create);

        joinPanel = new View[]{idContainer, idEditText, joinPasswordContainer, joinPasswordEditText};
        createPanel = new View[]{nameContainer, nameEditText, createPasswordContainer, createPasswordEditText};
        radioButtons = new View[]{joinRadioButton, createRadioButton};

        // button_back
        findViewById(R.id.group_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // radio buttons
        joinRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    createRadioButton.setChecked(false);
                }
                setViewsEnabled(joinPanel, isChecked);
            }
        });
        createRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    joinRadioButton.setChecked(false);
                }
                setViewsEnabled(createPanel, isChecked);
            }
        });

        // button_add
        ((MaterialButton)findViewById(R.id.group_button_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (joinRadioButton.isChecked()) {

                    final String idString = idEditText.getText().toString().trim();
                    final String passwordString = joinPasswordEditText.getText().toString().trim();

                    idContainer.setHelperText("");
                    joinPasswordContainer.setHelperText("");

                    if (idString.length() == 0) {
                        idContainer.setHelperText(getResources().getText(R.string.group_message_error_enter_id));
                    } else if (passwordString.length() == 0) {
                        joinPasswordContainer.setHelperText(getResources().getText(R.string.group_message_error_enter_password));
                    } else {
                        final Account account = NowAccountManager.getAccountOrNull(GroupActivity.this);
                        final int id = Integer.parseInt(idString);
                        final int password = Integer.parseInt(passwordString);

                        setViewsEnabled(joinPanel, false);
                        setViewsEnabled(radioButtons, false);

                        new JoinGroupRequest(account, id, password).request(new RequestBase.ResponseListener<JoinGroupRequest.EResponse>() {
                            @Override
                            public void onResponse(final JoinGroupRequest.EResponse response, Object[] args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (response) {

                                            case OK:
                                                Toast.makeText(getApplicationContext(), R.string.group_message_ok, Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                                break;

                                            case UNKNOWN_GROUP:
                                                idContainer.setHelperText(getResources().getText(R.string.group_message_error_unknown_id));
                                                break;

                                            case PASSWORD_ERROR:
                                                joinPasswordContainer.setHelperText(getResources().getText(R.string.group_message_error_password_not_matches));
                                                break;

                                            case ACCOUNT_ERROR:
                                                Toast.makeText(GroupActivity.this, R.string.group_message_error_account, Toast.LENGTH_LONG).show();
                                                break;

                                            case SERVER_ERROR:
                                                Toast.makeText(GroupActivity.this, R.string.group_message_error_server, Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        setViewsEnabled(joinPanel, true);
                                        setViewsEnabled(radioButtons, true);
                                    }
                                });
                            }
                        });
                    }
                } else {

                    final String name = nameEditText.getText().toString().trim();
                    final String passwordString = createPasswordEditText.getText().toString().trim();

                    nameContainer.setHelperText("");
                    createPasswordContainer.setHelperText("");

                    if (name.length() == 0) {
                        nameContainer.setHelperText(getResources().getText(R.string.group_message_error_enter_name));
                    } else if (passwordString.length() == 0) {
                        createPasswordContainer.setHelperText(getResources().getText(R.string.group_message_error_enter_password));
                    } else {
                        final Account account = NowAccountManager.getAccountOrNull(GroupActivity.this);
                        final int password = Integer.parseInt(passwordString);

                        setViewsEnabled(createPanel, false);
                        setViewsEnabled(radioButtons, false);

                        new CreateGroupRequest(account, name, password).request(new RequestBase.ResponseListener<CreateGroupRequest.EResponse>() {
                            @Override
                            public void onResponse(final CreateGroupRequest.EResponse response, Object[] args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (response) {

                                            case OK:
                                                Toast.makeText(getApplicationContext(), R.string.group_message_ok, Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                                break;

                                            case ACCOUNT_ERROR:
                                                Toast.makeText(GroupActivity.this, R.string.group_message_error_account, Toast.LENGTH_LONG).show();
                                                break;

                                            case SERVER_ERROR:
                                                Toast.makeText(GroupActivity.this, R.string.group_message_error_server, Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        setViewsEnabled(createPanel, true);
                                        setViewsEnabled(radioButtons, true);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    private void setViewsEnabled(@NonNull View[] views, boolean enabled) {

        for (View view : views) {
            view.setEnabled(enabled);
        }
    }
}