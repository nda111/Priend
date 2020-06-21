package com.gachon.priend.home.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.data.Sex;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.database.SpeciesListRequest;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.home.request.DeleteAnimalRequest;
import com.gachon.priend.home.request.EditAnimalRequest;
import com.gachon.priend.home.request.RegisterAnimalRequest;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Objects;

public class AnimalActivity extends AppCompatActivity {

    private TextInputLayout nameContainer;
    private TextInputEditText nameEditText;
    private TextInputEditText birthdayEditText;
    private TextInputLayout weightContainer;
    private TextInputEditText weightEditText;
    private TextInputLayout passwordContainer;
    private TextInputEditText passwordEditText;
    private MaterialRadioButton sexFemaleRadioButton;
    private MaterialCheckBox neuteredCheckBox;

    private boolean isEditMode;
    private Animal animal = null;
    private int groupId = -1;

    private Calendar selectedDate;
    private long selectedSpeciesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        final Resources resources = getResources();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        final Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.secondaryColor));

        final Intent intent = getIntent();
        if (intent.hasExtra(HomeActivity.INTENT_KEY_ANIMAL_ID)) {
            isEditMode = true;

            final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(this);
            final long id = intent.getLongExtra(HomeActivity.INTENT_KEY_ANIMAL_ID, -1);
            animal = animalDb.readOrNull(id);

            if (animal == null) {
                Toast.makeText(getApplicationContext(), R.string.animal_message_error_animal_not_exists, Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (intent.hasExtra(HomeActivity.INTENT_KEY_GROUP_ID)) {
            isEditMode = false;
            groupId = intent.getIntExtra(HomeActivity.INTENT_KEY_GROUP_ID, -1);
        } else {
            finish();
        }

        /*
         * Initialize GUI Components
         */
        final ImageButton deleteButton = findViewById(R.id.animal_button_delete);
        nameContainer = findViewById(R.id.animal_edit_text_name_container);
        nameEditText = findViewById(R.id.animal_edit_text_name);
        birthdayEditText = findViewById(R.id.animal_edit_text_birthday);
        weightContainer = findViewById(R.id.animal_edit_text_weight_container);
        weightEditText = findViewById(R.id.animal_edit_text_weight);
        passwordContainer = findViewById(R.id.animal_edit_text_password_container);
        passwordEditText = findViewById(R.id.animal_edit_text_password);
        final MaterialRadioButton sexMaleRadioButton = findViewById(R.id.animal_radio_button_sex_male);
        sexFemaleRadioButton = findViewById(R.id.animal_radio_button_sex_female);
        neuteredCheckBox = findViewById(R.id.animal_check_box_neutered);
        final Spinner speciesSpinner = findViewById(R.id.animal_spinner_species);
        final MaterialButton registerButton = findViewById(R.id.animal_button_register);

        if (isEditMode) {
            deleteButton.setVisibility(View.VISIBLE);
            registerButton.setText(resources.getText(R.string.animal_text_save));
        } else {
            weightContainer.setVisibility(View.VISIBLE);
            registerButton.setText(resources.getText(R.string.animal_text_register));
        }

        findViewById(R.id.animal_button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Account account = NowAccountManager.getAccountOrNull(AnimalActivity.this);
                assert account != null;
                new DeleteAnimalRequest(account, animal.getId()).request(new RequestBase.ResponseListener<DeleteAnimalRequest.EResponse>() {
                    @Override
                    public void onResponse(final DeleteAnimalRequest.EResponse response, Object[] args) {

                        switch (response) {

                            case OK:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                                Toast.makeText(getApplicationContext(), R.string.animal_message_deletion_ok, Toast.LENGTH_SHORT).show();
                                break;

                            case ACCOUNT_ERROR:
                                Toast.makeText(getApplicationContext(), R.string.animal_message_error_account, Toast.LENGTH_LONG).show();
                                break;

                            case SERVER_ERROR:
                                Toast.makeText(getApplicationContext(), R.string.animal_message_error_server, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }
        });

        // name
        if (isEditMode) {
            nameEditText.setText(animal.getName());
        }

        // birthday
        selectedDate = Calendar.getInstance();
        if (isEditMode) {
            selectedDate.setTimeInMillis(animal.getBirthday().toMillis());
        }
        setSelectedDate(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));

        birthdayEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AnimalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setSelectedDate(year, month, dayOfMonth);
                    }
                }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // sex
        if (isEditMode) {
            final short sex = animal.getSex().toShort();

            if ((sex & 0b01) == 1) {
                sexMaleRadioButton.setChecked(true);
            } else {
                sexFemaleRadioButton.setChecked(true);
            }

            neuteredCheckBox.setChecked((sex & 0b10) == 0b10);
        }

        // species
        final AnimalDatabaseHelper animalDb = new AnimalDatabaseHelper(this);
        final SpeciesListRequest.Species[] allSpecies = animalDb.getAllSpecies();

        final String[] species = new String[allSpecies.length];
        final Locale locale = resources.getConfiguration().getLocales().get(0);
        if (locale == Locale.KOREA) {
            for (int i = 0; i < species.length; i++) {
                species[i] = allSpecies[i].ko_kr;
            }
        } else { // contains en-US
            for (int i = 0; i < species.length; i++) {
                species[i] = allSpecies[i].en_us;
            }
        }
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, species);
        speciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpeciesId = allSpecies[position].id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        speciesSpinner.setAdapter(spinnerAdapter);
        if (isEditMode) {
            speciesSpinner.setSelection((int) animal.getSpecies());
        } else {
            speciesSpinner.setSelection(0);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean bIntegrity = true;

                final String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
                if (name.length() == 0) {
                    nameContainer.setHelperText(resources.getString(R.string.animal_message_error_enter_name));
                    bIntegrity = false;
                }

                Sex sex;
                if (sexFemaleRadioButton.isChecked()) {
                    if (neuteredCheckBox.isChecked()) {
                        sex = Sex.SPAYED;
                    } else {
                        sex = Sex.FEMALE;
                    }
                } else {
                    if (neuteredCheckBox.isChecked()) {
                        sex = Sex.NEUTERED;
                    } else {
                        sex = Sex.MALE;
                    }
                }

                final String weightString = Objects.requireNonNull(weightEditText.getText()).toString().trim();
                if (!isEditMode && weightString.length() == 0) {
                    weightContainer.setHelperText(resources.getString(R.string.animal_message_error_enter_weight));
                    bIntegrity = false;
                }

                final String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
                if (password.length() == 0) {
                    passwordContainer.setHelperText(resources.getString(R.string.animal_message_error_enter_password));
                    bIntegrity = false;
                }

                if (bIntegrity) {
                    final Account account = NowAccountManager.getAccountOrNull(AnimalActivity.this);
                    assert account != null;

                    if (isEditMode) {
                        final Date date = new Date(selectedDate.getTimeInMillis());
                        new EditAnimalRequest(account, animal.getId(), name, selectedDate.getTimeInMillis(), sex.toShort(), selectedSpeciesId, password).request(
                                new RequestBase.ResponseListener<EditAnimalRequest.EResponse>() {
                                    @Override
                                    public void onResponse(final EditAnimalRequest.EResponse response, Object[] args) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (response) {

                                                    case OK:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_edit_ok, Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        break;

                                                    case UNKNOWN_ANIMAL:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_animal_not_exists, Toast.LENGTH_LONG).show();
                                                        finish();
                                                        break;

                                                    case PASSWORD_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_password_not_matches, Toast.LENGTH_LONG);
                                                        break;

                                                    case ACCOUNT_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_account, Toast.LENGTH_LONG).show();
                                                        break;

                                                    case SERVER_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_server, Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                });

                    } else {
                        new RegisterAnimalRequest(account, groupId, name, selectedDate.getTimeInMillis(), sex.toShort(), selectedSpeciesId, weightString, password).request(
                                new RequestBase.ResponseListener<RegisterAnimalRequest.EResponse>() {
                                    @Override
                                    public void onResponse(final RegisterAnimalRequest.EResponse response, Object[] args) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (response) {

                                                    case OK:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_register_ok, Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        break;

                                                    case UNKNOWN_GROUP:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_group_not_exists, Toast.LENGTH_LONG).show();
                                                        finish();
                                                        break;

                                                    case PASSWORD_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_password_not_matches, Toast.LENGTH_LONG).show();
                                                        break;

                                                    case ACCOUNT_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_account, Toast.LENGTH_LONG).show();
                                                        break;

                                                    case SERVER_ERROR:
                                                        Toast.makeText(getApplicationContext(), R.string.animal_message_error_server, Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                }
            }
        });
    }

    private void setSelectedDate(int year, int month, int dayOfMonth) {
        selectedDate.set(year, month, dayOfMonth);

        final Date date = new Date(selectedDate.getTimeInMillis());
        birthdayEditText.setText(date.toString(getResources().getConfiguration().getLocales().get(0)));
    }
}