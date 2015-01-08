package com.worldventures.dreamtrips.view.presentation;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.view.activity.Injector;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

@PresentationModel
public class TripImagesTabsFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;
    private final View view;
    private PickImageDialog.Callback selectImageCallback = new PickImageDialog.Callback() {
        @Override
        public void onResult(ChosenImage image, String error) {
            if (error != null) {
                view.informUser(error);
            } else {

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

    public void onActionFacebookClick() {

    }

    public void onActionGalleryClick() {

    }

    public void onActionPhotoClick() {
        activityRouter.openCreatePhoto();
    }

    public PickImageDialog.Callback providePhotoChooseCallback() {
        return selectImageCallback;
    }

    public static interface View extends IInformView {
    }
}
