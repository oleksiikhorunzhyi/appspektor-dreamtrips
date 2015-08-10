package com.worldventures.dreamtrips.modules.tripsimages.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PickImageDelegate {

    private Context context;
    private Fragment fragment;

    int pidTypeShown;
    private PickImageDialog pid;

    private ImagePickCallback makePhotoImageCallback;
    private ImagePickCallback chooseImageCallback;
    private ImagePickCallback fbImageCallback;
    private MultiSelectPickCallback multiSelectPickCallback;

    public PickImageDelegate(Context context, Fragment fragment, int pidTypeShown) {
        this.context = context;
        this.fragment = fragment;
        this.pidTypeShown = pidTypeShown;
    }

    public void actionFacebook() {
        pid = new PickImageDialog(context, fragment);
        pid.setTitle("");
        pid.setCallback(fbImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_FACEBOOK);
        pid.show();
        pidTypeShown = PickImageDialog.REQUEST_FACEBOOK;

    }

    public void actionGallery() {
        pid = new PickImageDialog(context, fragment);
        pid.setTitle("");
        pid.setCallback(chooseImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_PICK_PICTURE);
        pid.show();
        pidTypeShown = PickImageDialog.REQUEST_PICK_PICTURE;
    }

    public void actionCapture() {
        pid = new PickImageDialog(context, fragment);
        pid.setTitle("");
        pid.setCallback(makePhotoImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_CAPTURE_PICTURE);
        pid.show();
        pidTypeShown = PickImageDialog.REQUEST_CAPTURE_PICTURE;
    }


    public void actionMultiSelect() {
        pid = new PickImageDialog(context, fragment);
        pid.setCallback(multiSelectPickCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_MULTI_SELECT);
        pid.show();
        pidTypeShown = PickImageDialog.REQUEST_MULTI_SELECT;
    }

    public void setMakePhotoImageCallback(ImagePickCallback makePhotoImageCallback) {
        this.makePhotoImageCallback = makePhotoImageCallback;
    }

    public void setChooseImageCallback(ImagePickCallback chooseImageCallback) {
        this.chooseImageCallback = chooseImageCallback;
    }

    public void setFbImageCallback(ImagePickCallback fbImageCallback) {
        this.fbImageCallback = fbImageCallback;
    }

    public void setMultiSelectPickCallback(MultiSelectPickCallback multiSelectPickCallback) {
        this.multiSelectPickCallback = multiSelectPickCallback;
    }

    public void handlePickDialogActivityResult(int requestCode, int resultCode, Intent data) {
        if (pidTypeShown != 0) {
            if (pid == null) {
                pid = new PickImageDialog(context, fragment);
                switch (pidTypeShown) {
                    case PickImageDialog.REQUEST_CAPTURE_PICTURE:
                        pid.setCallback(makePhotoImageCallback);
                        break;
                    case PickImageDialog.REQUEST_PICK_PICTURE:
                        pid.setCallback(chooseImageCallback);
                        break;
                    case PickImageDialog.REQUEST_MULTI_SELECT:
                        pid.setCallback(multiSelectPickCallback);
                        break;
                    case PickImageDialog.REQUEST_FACEBOOK:
                        pid.setCallback(fbImageCallback);
                        break;
                }
                pid.setChooserType(pidTypeShown);
            }
            pidTypeShown = 0;
            pid.onActivityResult(requestCode, resultCode, data);
        }
    }

}
