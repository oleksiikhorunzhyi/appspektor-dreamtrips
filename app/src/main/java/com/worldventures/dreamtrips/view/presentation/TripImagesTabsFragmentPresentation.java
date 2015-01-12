package com.worldventures.dreamtrips.view.presentation;

import android.net.Uri;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.io.File;

@PresentationModel
public class TripImagesTabsFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;
    private final View view;
    ImagePickCallback selectImageCallback = new ImagePickCallback() {
        @Override
        public void onResult(ChosenImage image, String error) {
            if (error != null) {
                view.informUser(error);
            } else {
                activityRouter.openCreatePhoto(Uri.fromFile(new File(image.getFilePathOriginal())));
            }
        }
    };
    ImagePickCallback fbCallback = new ImagePickCallback() {
        @Override
        public void onResult(ChosenImage image, String error) {
            if (error != null) {
                view.informUser(error);
            } else {
                activityRouter.openCreatePhoto(Uri.parse(image.getFilePathOriginal()));
            }
        }
    };

    public TripImagesTabsFragmentPresentation(View view, Injector injector) {
        super(view, injector);
        this.view = view;
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

    public static interface View extends IInformView {
    }
}
