package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;

public class TripImagesTabsPresenter extends Presenter<TripImagesTabsPresenter.View> {

    public static final String SELECTION_EXTRA = "selection_extra";

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null || image == null || image.getFileThumbnail() == null) {
            view.informUser(error);
        } else {
            imageSelected(fragment, Uri.fromFile(new File(image.getFileThumbnail())), "camera");
        }
    };

    protected ImagePickCallback selectImageGalleryCallback = (fragment, image, error) -> {
        if (error != null || image.getFileThumbnail() == null) {
            view.informUser(error);
        } else {
            imageSelected(fragment, Uri.fromFile(new File(image.getFileThumbnail())), "album");
        }
    };

    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null || image == null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            imageSelected(fragment, uri, "facebook");
        }
    };

    private int selection;

    public TripImagesTabsPresenter(Bundle args) {
        if (args != null) {
            selection = args.getInt(SELECTION_EXTRA);
        }
    }

    public void trackState(int position) {
        if (position == TripImagesListFragment.Type.MY_IMAGES.ordinal()) {
            TrackingHelper.mine(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.YOU_SHOULD_BE_HERE.ordinal()) {
            TrackingHelper.ysbh(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.MEMBER_IMAGES.ordinal()) {
            TrackingHelper.all(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.VIDEO_360.ordinal()) {
            TrackingHelper.video360(getAccountUserId());
        } else if (position == TripImagesListFragment.Type.INSPIRE_ME.ordinal()) {
            TrackingHelper.inspr(getAccountUserId());
        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setFabVisibility(true);
        view.setSelection(selection);
    }

    public void imageSelected(Fragment fragment, Uri uri, String type) {
        if (activityRouter != null) {
            activityRouter.openCreatePhoto(fragment, uri, type);
        }
    }

    public ImagePickCallback provideCallback(int pidType) {
        switch (pidType) {
            case PickImageDialog.REQUEST_FACEBOOK:
                return fbCallback;
            case PickImageDialog.REQUEST_CAPTURE_PICTURE:
                return selectImageCallback;
            case PickImageDialog.REQUEST_PICK_PICTURE:
                return selectImageGalleryCallback;
        }
        return null;
    }

    @Override
    public void dropView() {
        eventBus.removeAllStickyEvents();
        super.dropView();
    }

    public interface View extends Presenter.View {
        void setFabVisibility(boolean visibility);

        void setSelection(int selection);
    }
}
