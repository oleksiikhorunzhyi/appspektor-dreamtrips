package com.worldventures.dreamtrips.presentation;

import android.net.Uri;

import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.io.File;

@PresentationModel
public class TripImagesTabsFragmentPresentation extends BasePresentation<TripImagesTabsFragmentPresentation.View> implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;

    ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(fragment, Uri.fromFile(new File(image.getFileThumbnail())));
        }
    };

    ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(fragment, Uri.parse(image.getFilePathOriginal()));
        }
    };

    public TripImagesTabsFragmentPresentation(View view) {
        super(view);
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    public void onCreate() {
        //boolean facebookAvailable = appSessionHolder.get().get().getGlobalConfig().isFacebook_gallery_enabled();
        view.setFabVisibility(true);
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public void onFacebookAction(BaseFragment from) {
        activityRouter.openFacebookPhoto(from);
    }

    public ImagePickCallback providePhotoChooseCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback provideFbCallback() {
        return fbCallback;
    }

    public interface View extends BasePresentation.View {
        void setFabVisibility(boolean visibility);
    }
}
