package com.worldventures.dreamtrips.modules.tripsimages.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;

import java.util.Arrays;

import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.IntentBuilder;
import timber.log.Timber;

public class PickImageDialog implements ImageChooserListener {

    public static final int REQUEST_MULTI_SELECT = 345;
    public static final int REQUEST_FACEBOOK = 346;
    public static final int REQUEST_CAPTURE_PICTURE = ChooserType.REQUEST_CAPTURE_PICTURE;
    public static final int REQUEST_PICK_PICTURE = ChooserType.REQUEST_PICK_PICTURE;
    public static final String FOLDERNAME = "dreamtrip_folder";
    private ImagePickCallback callback;
    private MaterialDialog.Builder builder;
    private Context context;
    private String title = "";
    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String filePath;
    private Fragment fragment;
    private int[] requestTypes;
    private MultiSelectPickCallback multiSelectPickCallback;
    private WeakHandler handler = new WeakHandler(Looper.getMainLooper());


    public PickImageDialog(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.builder = new MaterialDialog.Builder(context);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void show() {
        if (requestTypes != null && requestTypes.length == 1) {
            switch (requestTypes[0]) {
                case REQUEST_CAPTURE_PICTURE:
                    takePicture();
                    return;
                case REQUEST_PICK_PICTURE:
                    chooseImage();
                    return;
                case REQUEST_MULTI_SELECT:
                    chooseMultiSelect();
                    return;
                case REQUEST_FACEBOOK:
                    chooseFacebook();
                    return;
            }
        }
        builder.title(title != null ? title : "")
                .items(R.array.photo_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        takePicture();
                    } else {
                        chooseImage();
                    }
                }).show();
    }

    private void chooseFacebook() {
        Intent intent = new Intent(context, FacebookPickPhotoActivity.class);
        fragment.startActivityForResult(intent, FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

    private void chooseMultiSelect() {
        Intent intent = new IntentBuilder()
                .setSelectionLimit(5)
                .setOptions(IntentBuilder.Option.GALLERY)
                .createIntent(context);
        fragment.startActivityForResult(intent, REQUEST_MULTI_SELECT);
    }


    public void setRequestTypes(int... requestTypes) {
        this.requestTypes = requestTypes;
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(fragment, ChooserType.REQUEST_PICK_PICTURE, FOLDERNAME, false);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Timber.e(e, "Problem on image picking");
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(fragment, ChooserType.REQUEST_CAPTURE_PICTURE, FOLDERNAME, false);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Log.e(PickImageDialog.class.getSimpleName(), "", e);
        }
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        if (callback != null) {
            handler.post(() -> callback.onResult(fragment, chosenImage, null));
        }
    }

    @Override
    public void onError(String s) {
        if (callback != null) {
            callback.onResult(fragment, null, s);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean pictureRequestCode = requestCode == ChooserType.REQUEST_PICK_PICTURE
                || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE;
        if (resultCode == Activity.RESULT_OK && pictureRequestCode) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
        handleMultiSelectActivityResult(requestCode, resultCode, data);
        handleFacebookActivityResult(requestCode, resultCode, data);
    }


    private void handleMultiSelectActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PickImageDialog.REQUEST_MULTI_SELECT) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                if (multiSelectPickCallback != null) {
                    multiSelectPickCallback.onResult(fragment, Arrays.asList(uris), null);
                }
            }
        }
    }

    private void handleFacebookActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO
                    && callback != null) {
                ChosenImage image = new Gson().fromJson(data.getStringExtra(FacebookPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
                callback.onResult(fragment, image, null);
            }
        }
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(fragment, chooserType, FOLDERNAME, false);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    public void setChooserType(int chooserType) {
        this.chooserType = chooserType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCallback(ImagePickCallback callback) {
        this.callback = callback;
    }

    public void setCallback(MultiSelectPickCallback callback) {
        this.multiSelectPickCallback = callback;
    }


}
