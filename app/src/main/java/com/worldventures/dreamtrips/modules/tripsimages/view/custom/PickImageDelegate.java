package com.worldventures.dreamtrips.modules.tripsimages.view.custom;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;

import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.IntentBuilder;
import timber.log.Timber;

public class PickImageDelegate implements ImageChooserListener {

    public static final int REQUEST_MULTI_SELECT = 345;
    public static final int REQUEST_FACEBOOK = 346;
    public static final int REQUEST_CAPTURE_PICTURE = ChooserType.REQUEST_CAPTURE_PICTURE;
    public static final int REQUEST_PICK_PICTURE = ChooserType.REQUEST_PICK_PICTURE;

    public static final String FOLDERNAME = "dreamtrip_folder_temp_sd";

    private Fragment fragment;

    private int requestType;

    private ImageChooserManager imageChooserManager;
    private String filePath;

    private ImagePickCallback imageCallback;
    private ImagePickErrorCallback errorCallback;

    public PickImageDelegate(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void show() {
        switch (requestType) {
            case REQUEST_CAPTURE_PICTURE:
                takePicture();
                break;
            case REQUEST_PICK_PICTURE:
                chooseImage();
                break;
            case REQUEST_MULTI_SELECT:
                chooseMultiSelect();
                break;
            case REQUEST_FACEBOOK:
                chooseFacebook();
                break;
        }
    }

    private void chooseFacebook() {
        Intent intent = new Intent(fragment.getActivity(), FacebookPickPhotoActivity.class);
        fragment.startActivityForResult(intent, REQUEST_FACEBOOK);
    }

    private void chooseMultiSelect() {
        Intent intent = new IntentBuilder()
                .setSelectionLimit(5)
                .setOptions(IntentBuilder.Option.GALLERY)
                .createIntent(fragment.getActivity());
        fragment.startActivityForResult(intent, REQUEST_MULTI_SELECT);
    }

    private void chooseImage() {
        imageChooserManager = new ImageChooserManager(fragment, requestType, FOLDERNAME, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Timber.e(e, "Problem on image choosing");
        }
    }

    private void takePicture() {
        imageChooserManager = new ImageChooserManager(fragment, requestType, FOLDERNAME, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Timber.e(e, "Problem on image choosing");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ChooserType.REQUEST_PICK_PICTURE
                    || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
                if (imageChooserManager == null) {
                    imageChooserManager = new ImageChooserManager(fragment.getActivity(), requestType, FOLDERNAME, true);
                    imageChooserManager.setImageChooserListener(this);
                }

                imageChooserManager.reinitialize(filePath);
                imageChooserManager.submit(requestCode, data);
            } else if (requestCode == REQUEST_MULTI_SELECT) {
                handleMultiSelectActivityResult(data);
            } else if (requestCode == REQUEST_FACEBOOK) {
                handleFacebookActivityResult(data);
            }
        }
    }

    private void handleMultiSelectActivityResult(Intent data) {
        Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

        if (parcelableUris != null) {
            Uri[] uris = new Uri[parcelableUris.length];
            System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

            if (imageCallback != null)
                imageCallback.onImagePicked(Queryable.from(uris).map(element -> {
                    ChosenImage chosenImage = new ChosenImage();
                    chosenImage.setFilePathOriginal("file://" + element.toString());
                    return chosenImage;
                }).toArray());
        }

    }

    private void handleFacebookActivityResult(Intent data) {
        ChosenImage image = new Gson().fromJson(data.getStringExtra(FacebookPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
        if (imageCallback != null) imageCallback.onImagePicked(image);
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        chosenImage.setFilePathOriginal("file://" + chosenImage.getFilePathOriginal());
        if (imageCallback != null) imageCallback.onImagePicked(chosenImage);
    }

    @Override
    public void onError(String reason) {
        if (errorCallback != null) errorCallback.onError(reason);
    }

    public void setImageCallback(ImagePickCallback imageCallback) {
        this.imageCallback = imageCallback;
    }

    public void setErrorCallback(ImagePickErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    public interface ImagePickCallback {
        void onImagePicked(ChosenImage... chosenImage);
    }

    public interface ImagePickErrorCallback {
        void onError(String reason);
    }
}