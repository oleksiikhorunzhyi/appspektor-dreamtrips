package com.worldventures.dreamtrips.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

public class PickImageDialog implements ImageChooserListener {


    public static final String FOLDERNAME = "dreamtrip_folder";
    private Callback callback;
    private MaterialDialog.Builder builder;
    private Context context;
    private String title;
    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String filePath;
    private Fragment fragment;

    public PickImageDialog(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.builder = new MaterialDialog.Builder(context);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void show() {
        builder.title(title != null ? title : "")
                .items(new String[]{"Take picture", "Choose image"})
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        takePicture();
                    } else {
                        chooseImage();
                    }
                }).show();
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(fragment, ChooserType.REQUEST_PICK_PICTURE, FOLDERNAME, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(fragment, ChooserType.REQUEST_CAPTURE_PICTURE, FOLDERNAME, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        if (callback != null) {
            callback.onResult(chosenImage, null);
        }
    }

    @Override
    public void onError(String s) {
        if (callback != null) {
            callback.onResult(null, s);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean pictureRequestCode = requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE;
        if (resultCode == Activity.RESULT_OK && pictureRequestCode) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(fragment, chooserType, FOLDERNAME, true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback {
        void onResult(ChosenImage image, String error);
    }

}
