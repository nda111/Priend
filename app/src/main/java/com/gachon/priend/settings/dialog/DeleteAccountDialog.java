package com.gachon.priend.settings.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.gachon.priend.R;
import com.gachon.priend.settings.OnDeleteClickListener;

public final class DeleteAccountDialog {

    private AlertDialog dialog;

    public DeleteAccountDialog(@NonNull Context context, @NonNull final OnDeleteClickListener onDeleteClickListener) {

        this.dialog = new AlertDialog.Builder(context)
                .setView(R.layout.dialog_delete_account)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDeleteClickListener.onDeleteClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public void show() {
        this.dialog.show();
    }
}
