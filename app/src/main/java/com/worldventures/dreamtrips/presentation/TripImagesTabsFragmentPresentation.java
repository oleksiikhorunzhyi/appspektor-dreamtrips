package com.worldventures.dreamtrips.presentation;

import android.net.Uri;

import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.io.File;

@PresentationModel
public class TripImagesTabsFragmentPresentation extends BasePresentation<BasePresentation.View> implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;

    ImagePickCallback selectImageCallback = (image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(Uri.fromFile(new File(image.getFilePathOriginal())));
        }
    };

    ImagePickCallback fbCallback = (image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(Uri.parse(image.getFilePathOriginal()));
        }
    };

    public TripImagesTabsFragmentPresentation(View view) {
        super(view);
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public ImagePickCallback providePhotoChooseCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback provideFbCallback() {
        return fbCallback;
    }
}
