package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class TripImagesTabsPresenter extends Presenter<TripImagesTabsPresenter.View> {

    public static final String SELECTION_EXTRA = "selection_extra";

    protected PickImageDelegate.ImagePickCallback captureImageCallback = chosenImage -> {
        if (chosenImage != null) {
            imageSelected(Uri.parse(chosenImage[0].getFilePathOriginal()), "camera");
        }
    };
    protected PickImageDelegate.ImagePickCallback chooseImageCallback = chosenImage -> {
        if (chosenImage != null) {
            imageSelected(Uri.parse(chosenImage[0].getFilePathOriginal()), "album");
        }
    };
    protected PickImageDelegate.ImagePickCallback fbCallback = chosenImage -> {
        if (chosenImage != null) {
            imageSelected(Uri.parse(chosenImage[0].getFilePathOriginal()), "facebook");
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

    public void imageSelected(Uri uri, String type) {
        if (activityRouter != null) {
            activityRouter.openCreatePhoto((Fragment) view, uri, type);
        }
    }

    public PickImageDelegate.ImagePickCallback provideCallback(int pidType) {
        switch (pidType) {
            case PickImageDelegate.REQUEST_FACEBOOK:
                return fbCallback;
            case PickImageDelegate.REQUEST_CAPTURE_PICTURE:
                return captureImageCallback;
            case PickImageDelegate.REQUEST_PICK_PICTURE:
                return chooseImageCallback;
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
