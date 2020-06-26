package com.gachon.priend.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.gachon.priend.R;
import com.gachon.priend.calendar.delegate.GalleryOrCameraListener;

/**
 * A dialog that represents choice between gallery and camera
 *
 * @author 유근혁
 * @since May 22nd 2020
 */
public class GalleryOrCameraDialog {

    private Context context;

    /**
     * Create an instance with its {Context}
     * @param context The context of dialog
     */
    public GalleryOrCameraDialog(@NonNull Context context) {
        this.context = context;
    }

    /**
     * Show the dialog
     *
     * @param listener The result listener
     */
    public void show(final GalleryOrCameraListener listener) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_gallery_or_camera);

        dialog.findViewById(R.id.gallery_or_camera_button_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGallery();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.gallery_or_camera_button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCamera();
                dialog.dismiss();
            }
        });
    }
}
