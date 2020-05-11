package com.gachon.priend.membership.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.gachon.priend.R;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.interaction.WebSocketRequest;
import com.gachon.priend.membership.request.EvaluationRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * An activity that evaluates email, goes to {LoginPasswordActivity} or {RegisterActivity}
 *
 * @author 유근혁
 * @since 7th May 2020
 */
public class LoginEntryActivity extends AppCompatActivity {

    /**
     * A compiled email-matching regular expression pattern
     */
    private static final Pattern CompiledEmailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    /**
     * A string constant represents {email} intent extra value
     */
    public static final String EXTRA_KEY_EMAIL = "login_entry_email";

    private TextInputLayout emailTextInputLayout = null;
    private TextInputEditText emailEditText = null;
    private MaterialButton nextButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_entry);

        /*
         * Initialize GUI Components
         */
        emailTextInputLayout = findViewById(R.id.login_entry_text_input_layout_email);
        emailEditText = findViewById(R.id.login_entry_edit_text_email);
        nextButton = findViewById(R.id.login_entry_button_next);

        // nextButton
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable email = emailEditText.getText();

                if (email.length() == 0) { // no input
                    showError(emailTextInputLayout, R.string.login_entry_message_require_email);
                } else if (CompiledEmailPattern.matcher(email).matches()) { // if is email format
                    emailTextInputLayout.setHelperText(null);

                    new EvaluationRequest(email.toString()).request(new RequestBase.ResponseListener<EvaluationRequest.EResponse>() {
                        @Override
                        public void onResponse(final EvaluationRequest.EResponse response, final Object[] args) {

                            switch (response) {
                                case VERIFIED:
                                    gotoActivity(LoginPasswordActivity.class, email.toString());
                                    break;

                                case UNKNOWN:
                                    gotoActivity(RegisterActivity.class, email.toString());
                                    break;

                                case NOT_VERIFIED:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showError(emailTextInputLayout, R.string.login_entry_message_not_verified);
                                        }
                                    });
                                    break;

                                case SERVER_ERROR:
                                default:
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginEntryActivity.this, R.string.login_entry_message_server_error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                } else { // not an email
                    showError(emailTextInputLayout, R.string.login_entry_message_require_email);
                }
            }
        });
    }

    private void gotoActivity(Class<?> cls, String email) {
        Intent intent = new Intent(LoginEntryActivity.this, cls);
        if (email != null) {
            intent.putExtra(EXTRA_KEY_EMAIL, email);
        }

        startActivity(intent);
    }

    private void showError(TextInputLayout layout, @StringRes int resId) {
        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();

        layout.setHelperText(resources.getString(resId));
        layout.setHelperTextColor(resources.getColorStateList(R.color.red_700, theme));
    }
}
