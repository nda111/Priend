package com.gachon.priend.calendar.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.icu.text.AlphabeticIndex;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gachon.priend.R;
import com.gachon.priend.calendar.Memo;
import com.gachon.priend.calendar.delegate.GalleryOrCameraListener;
import com.gachon.priend.calendar.dialog.GalleryOrCameraDialog;
import com.gachon.priend.calendar.request.DeleteMemoRequest;
import com.gachon.priend.calendar.request.InsertMemoRequest;
import com.gachon.priend.calendar.request.UpdateMemoRequest;
import com.gachon.priend.data.database.AnimalDatabaseHelper;
import com.gachon.priend.data.datetime.Date;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.entity.Animal;
import com.gachon.priend.interaction.RequestBase;
import com.gachon.priend.membership.NowAccountManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Objects;

/**
 * An activity that create new memo or edit memo which exists
 *
 * @author 유근혁
 * @since May 22th 2020
 */
public class RecordActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 0x01;
    private static final int CAMERA_REQUEST_CODE = 0x02;

    private TextInputEditText titleEditText = null;
    private EditText contentEditText = null;
    private ImageButton photoImageButton = null;

    private long animalId = -1;
    private Memo memo = null;
    private Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /*
         * Get Extras from Bundle
         */
        Bundle bundle = getIntent().getExtras();
        animalId = bundle.getLong(CalendarSelectionActivity.BUNDLE_KEY_ANIMAL_ID, -1);
        if (animalId == -1) {
            finish();
        }
        memo = bundle.getParcelable(CalendarSelectionActivity.BUNDLE_KEY_MEMO);

        /*
         * Action bar GUI Components
         */
        ImageButton backButton = findViewById(R.id.record_button_back);
        ImageButton saveButton = findViewById(R.id.record_button_save);
        ImageButton deleteButton = findViewById(R.id.record_button_delete);

        // backButton
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSaveMemo();
            }
        });

        // deleteButton
        if (memo != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDeleteMemo();
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        /*
         * Initialize GUI Components
         */
        titleEditText = findViewById(R.id.record_edit_text_title);
        contentEditText = findViewById(R.id.record_edit_text_content);
        photoImageButton = findViewById(R.id.record_image_view_photo);

        if (memo != null) {
            // titleEditText
            titleEditText.setText(memo.getTitle());

            // contentEditText
            contentEditText.setText(memo.getText());
        }

        // photoImageButton
        if (memo != null) {
            setPhoto(memo.getPhoto());
        }
        photoImageButton.setOnClickListener(new View.OnClickListener() {

            private final String[] ImageExtensions = {"image/jpeg", "image/png"};

            @Override
            public void onClick(View v) {
                new GalleryOrCameraDialog(RecordActivity.this).show(new GalleryOrCameraListener() {
                    @Override
                    public void onGallery() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, ImageExtensions);

                        startActivityForResult(intent, GALLERY_REQUEST_CODE);
                    }

                    @Override
                    public void onCamera() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    }
                });
            }
        });
        photoImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
                builder.setTitle(R.string.record_text_delete_photo);
                builder.setPositiveButton(R.string.record_text_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto(null);
                    }
                });
                builder.setNegativeButton(R.string.record_text_cancel, null);
                builder.show();
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);

                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        setPhoto(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");

                    setPhoto(bitmap);
                    break;

                default:
                    break;
            }
        }
    }

    private void setPhoto(Bitmap bitmap) {
        photoImageButton.setImageBitmap(bitmap);

        if (bitmap == null) {
            photoImageButton.setBackgroundResource(R.drawable.ic_add_photo_alternate_black_48dp);
            photoImageButton.setImageBitmap(null);

            this.photo = null;
        } else {
            photoImageButton.setBackground(null);
            photoImageButton.setImageBitmap(bitmap);

            this.photo = bitmap;
        }
    }

    private void requestSaveMemo() {
        final Memo newMemo = new Memo(
                this.memo == null
                        ? new Date(getIntent().getExtras().getLong(CalendarSelectionActivity.BUNDLE_KEY_WHEN))
                        : memo.getWhen(),
                titleEditText.getText().toString(),
                contentEditText.getText().toString(),
                photo);
        final Account nowAccount = NowAccountManager.getAccountOrNull(this);

        if (nowAccount != null) {
            if (memo == null) { // request new memo

                new InsertMemoRequest(nowAccount, animalId, newMemo).request(new RequestBase.ResponseListener<InsertMemoRequest.EResponse>() {
                    @Override
                    public void onResponse(final InsertMemoRequest.EResponse response, Object[] args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (response) {

                                    case OK:
                                        Toast.makeText(getApplicationContext(), R.string.record_message_memo_saved, Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        break;

                                    case ACCOUNT_ERROR:
                                        Toast.makeText(getApplicationContext(), R.string.record_message_error_account, Toast.LENGTH_SHORT).show();
                                        break;

                                    case SERVER_ERROR:
                                        Toast.makeText(getApplicationContext(), R.string.record_message_error_server, Toast.LENGTH_LONG).show();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                    }
                });
            } else { // request memo update
                Memo.Commit commit = new Memo.Commit(memo, newMemo);

                if (commit.haveChanges()) {
                    new UpdateMemoRequest(nowAccount, animalId, commit).request(new RequestBase.ResponseListener<UpdateMemoRequest.EResponse>() {
                        @Override
                        public void onResponse(final UpdateMemoRequest.EResponse response, Object[] args) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (response) {

                                        case OK:
                                            Toast.makeText(getApplicationContext(), R.string.record_message_memo_saved, Toast.LENGTH_SHORT).show();
                                            break;

                                        case ACCOUNT_ERROR:
                                            Toast.makeText(getApplicationContext(), R.string.record_message_error_account, Toast.LENGTH_SHORT).show();
                                            break;

                                        case SERVER_ERROR:
                                            Toast.makeText(getApplicationContext(), R.string.record_message_error_server, Toast.LENGTH_LONG).show();
                                            break;

                                        default:
                                            break;
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void requestDeleteMemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.record_text_delete_memo)
                .setPositiveButton(R.string.record_text_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final Account nowAccount = NowAccountManager.getAccountOrNull(RecordActivity.this);

                        if (nowAccount != null) {
                            new DeleteMemoRequest(nowAccount, animalId, memo.getId()).request(new RequestBase.ResponseListener<DeleteMemoRequest.EResponse>() {
                                @Override
                                public void onResponse(final DeleteMemoRequest.EResponse response, Object[] args) {

                                    Log.d("RecordActivity", "Memo id: " + memo.getId());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (response) {

                                                case OK:
                                                    Toast.makeText(getApplicationContext(), R.string.record_message_memo_deleted, Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                    break;

                                                case ACCOUNT_ERROR:
                                                    Toast.makeText(getApplicationContext(), R.string.record_message_error_account, Toast.LENGTH_SHORT).show();
                                                    break;

                                                case SERVER_ERROR:
                                                    Toast.makeText(getApplicationContext(), R.string.record_message_error_server, Toast.LENGTH_LONG).show();
                                                    break;

                                                default:
                                                    break;
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(R.string.record_text_cancel, null);

        builder.show();
    }
}
