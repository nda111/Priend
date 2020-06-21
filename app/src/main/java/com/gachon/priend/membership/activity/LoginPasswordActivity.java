package com.gachon.priend.membership.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gachon.priend.home.activity.HomeActivity;
import com.gachon.priend.R;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.AutoLoginManager;
import com.gachon.priend.membership.NowAccountManager;
import com.gachon.priend.membership.request.LoginRequest;
import com.gachon.priend.membership.request.ResetPasswordRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

/**
 * An activity that actually send login request to a server
 *
 * @author 유근혁
 * @since 8th May 2020
 */
public class LoginPasswordActivity extends AppCompatActivity {

    private ImageButton backButton = null;
    private MaterialTextView emailTextView = null;
    private TextInputLayout passwordTextInputLayout = null;
    private TextInputEditText passwordEditText = null;
    private MaterialCheckBox rememberMeCheckBox = null;
    private MaterialButton loginButton = null;
    private MaterialButton forgotPasswordButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Intent intent = getIntent();

        /*
         * Initialize GUI Components
         */
        backButton = findViewById(R.id.login_password_button_back);
        emailTextView = findViewById(R.id.login_password_text_view_email);
        passwordTextInputLayout = findViewById(R.id.login_password_text_input_layout_password);
        passwordEditText = findViewById(R.id.login_password_edit_text_password);
        rememberMeCheckBox = findViewById(R.id.login_password_check_box_remember_me);
        loginButton = findViewById(R.id.login_password_button_login);
        forgotPasswordButton = findViewById(R.id.login_password_button_forgot_password);

        // backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // emailTextView
        emailTextView.setText(intent.getStringExtra(LoginEntryActivity.EXTRA_KEY_EMAIL));

        // loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String password = passwordEditText.getText().toString();

                if (password.length() == 0) {
                    passwordTextInputLayout.setHelperText(getResources().getString(R.string.login_password_password_required));
                } else {
                    passwordTextInputLayout.setHelperText(null);

                    final String email = emailTextView.getText().toString();

                    setGuiEnabled(false);

                    new LoginRequest(email, password).request(new RequestBase.ResponseListener<LoginRequest.EResponse>() {
                        @Override
                        public void onResponse(final LoginRequest.EResponse response, final Object[] args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    switch (response) {

                                        case OK:
                                            Account account = (Account) args[0];
                                            NowAccountManager.setAccount(LoginPasswordActivity.this, account);

                                            if (rememberMeCheckBox.isChecked()) {
                                                AutoLoginManager.setValue(getApplicationContext(), true, email, password);
                                            } else {
                                                AutoLoginManager.setValue(getApplicationContext(), false, null, null);
                                            }

                                            Intent intent = new Intent(LoginPasswordActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            startActivity(intent);
                                            break;

                                        case WRONG_PASSWORD:
                                            Toast.makeText(getApplicationContext(), R.string.login_password_password_wrong, Toast.LENGTH_LONG).show();
                                            forgotPasswordButton.setVisibility(View.VISIBLE);
                                            break;

                                        case UNKNOWN_EMAIL:
                                            Toast.makeText(getApplicationContext(), R.string.login_password_unknown_email, Toast.LENGTH_LONG).show();
                                            finish();
                                            break;

                                        case SERVER_ERROR:
                                        default:
                                            Toast.makeText(getApplicationContext(), R.string.login_password_server_error, Toast.LENGTH_LONG).show();
                                            break;
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setGuiEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        // forgotPasswordButton
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailTextView.getText().toString();

                setGuiEnabled(false);
                new ResetPasswordRequest(email).request(new RequestBase.ResponseListener<ResetPasswordRequest.EResponse>() {
                    @Override
                    public void onResponse(ResetPasswordRequest.EResponse response, Object[] args) {

                        switch (response) {

                            case OK:
                                Toast.makeText(getApplicationContext(), R.string.login_password_reset_ok, Toast.LENGTH_LONG).show();
                                break;

                            case SERVER_ERROR:
                                Toast.makeText(getApplicationContext(), R.string.login_password_reset_server_error, Toast.LENGTH_LONG).show();
                            default:

                                break;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setGuiEnabled(true);
                            }
                        });
                    }
                });
            }
        });
    }

    private void setGuiEnabled(boolean enabled) {
        backButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        passwordEditText.setEnabled(enabled);
        rememberMeCheckBox.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        forgotPasswordButton.setEnabled(enabled);
    }
}