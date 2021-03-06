package com.gachon.priend.membership.activity;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.request.RegisterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.regex.Pattern;

/**
 * An activity that gets register data, passes to {LoginEntryActivity} as result
 *
 * @author 유근혁
 * @since 7th May 2020
 */
public class RegisterActivity extends AppCompatActivity {

    private static final Pattern[] CompiledPasswordPatterns = {
            Pattern.compile("((?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).{8,})"),
            Pattern.compile("(.*[a-z].*)"),         // Must contain lower-case alphabet
            Pattern.compile("(.*[A-Z].*)"),         // Must contain upper-case alphabet
            Pattern.compile("(.*[0-9].*)"),         // Must contain digit
            Pattern.compile("(.*[!@#$%^&*].*)"),    // Must contain one of those
            Pattern.compile("(.{8,})")              // At least 8 characters required
    };

    private ImageButton backButton = null;
    private MaterialTextView emailTextView = null;
    private TextInputLayout passwordTextInputLayout = null;
    private TextInputEditText passwordEditText = null;
    private TextInputLayout confirmPasswordTextInputLayout = null;
    private TextInputEditText confirmPasswordEditText = null;
    private TextInputLayout nameTextInputLayout = null;
    private TextInputEditText nameEditText = null;
    private MaterialButton registerButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Intent intent = getIntent();

        /*
         * Initialize GUI Components
         */
        backButton = findViewById(R.id.register_button_back);
        emailTextView = findViewById(R.id.register_text_view_email);
        passwordTextInputLayout = findViewById(R.id.register_text_input_layout_password);
        passwordEditText = findViewById(R.id.register_edit_text_password);
        confirmPasswordTextInputLayout = findViewById(R.id.register_text_input_layout_confirm_password);
        confirmPasswordEditText = findViewById(R.id.register_edit_text_confirm_password);
        nameTextInputLayout = findViewById(R.id.register_text_input_layout_name);
        nameEditText = findViewById(R.id.register_edit_text_name);
        registerButton = findViewById(R.id.register_button_register);

        // button_back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // emailTextView
        emailTextView.setText(intent.getStringExtra(LoginEntryActivity.EXTRA_KEY_EMAIL));

        // registerButton
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable password = passwordEditText.getText();
                final Editable confirmPassword = confirmPasswordEditText.getText();
                final Editable name = nameEditText.getText();

                System.out.println(password.toString());
                System.out.println(confirmPassword.toString());
                System.out.println(name.toString());

                if (password.length() == 0) {
                    showError(passwordTextInputLayout, R.string.register_message_password_required);
                } else if (!CompiledPasswordPatterns[0].matcher(password).matches()) {
                    if (!CompiledPasswordPatterns[1].matcher(password).matches() || !CompiledPasswordPatterns[2].matcher(password).matches()) {
                        showError(passwordTextInputLayout, R.string.register_message_password_alphabet_required);
                    } else if (!CompiledPasswordPatterns[3].matcher(password).matches()) {
                        showError(passwordTextInputLayout, R.string.register_message_password_digit_required);
                    } else if (!CompiledPasswordPatterns[4].matcher(password).matches()) {
                        showError(passwordTextInputLayout, R.string.register_message_password_special_required);
                    } else if (!CompiledPasswordPatterns[5].matcher(password).matches()) {
                        showError(passwordTextInputLayout, R.string.register_message_password_length);
                    }
                } else if (!confirmPassword.toString().equals(password.toString())) {
                    showError(confirmPasswordTextInputLayout, R.string.register_message_password_not_matches);
                } else if (name.length() == 0) {
                    showError(nameTextInputLayout, R.string.register_message_name_required);
                } else {

                    setGuiEnabled(false);

                    new RegisterRequest(emailTextView.getText().toString(), password.toString(), name.toString())
                            .request(new RequestBase.ResponseListener<RegisterRequest.EResponse>() {
                                @Override
                                public void onResponse(final RegisterRequest.EResponse response, final Object[] args) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            switch (response) {
                                                case OK:
                                                    Toast.makeText(getApplicationContext(), R.string.register_message_welcome, Toast.LENGTH_LONG).show();
                                                    finish();
                                                    break;

                                                case SERVER_ERROR:
                                                    Toast.makeText(getApplicationContext(), R.string.register_message_server_error, Toast.LENGTH_LONG).show();
                                                    break;

                                                default:
                                                    Toast.makeText(getApplicationContext(), R.string.register_message_unexpected_error, Toast.LENGTH_LONG).show();
                                                    break;
                                            }

                                            setGuiEnabled(true);
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }

    private void setGuiEnabled(boolean enabled) {
        backButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        passwordEditText.setEnabled(enabled);
        confirmPasswordEditText.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }

    private void showError(TextInputLayout layout, @StringRes int resId) {
        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();

        layout.setHelperText(resources.getString(resId));
        layout.setHelperTextColor(resources.getColorStateList(R.color.red_700, theme));
    }
}
