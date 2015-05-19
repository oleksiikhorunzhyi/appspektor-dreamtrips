package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;

public class TripImagesTabsFragmentPresenter extends Presenter<TripImagesTabsFragmentPresenter.View> {

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null || image.getFileThumbnail() == null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(fragment, Uri.fromFile(new File(image.getFileThumbnail())));
        }
    };

    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            activityRouter.openCreatePhoto(fragment, Uri.parse(image.getFilePathOriginal()));
        }
    };

    public void trackState(int position) {
        if (position == TripImagesListFragment.Type.MY_IMAGES.ordinal()) {
            TrackingHelper.mine(getUserId());
        } else if (position == TripImagesListFragment.Type.YOU_SHOULD_BE_HERE.ordinal()) {
            TrackingHelper.ysbh(getUserId());
        } else if (position == TripImagesListFragment.Type.MEMBER_IMAGES.ordinal()) {
            TrackingHelper.all(getUserId());
        }
    }

    public void destroy() {
        eventBus.removeAllStickyEvents();
    }

    @Override
    public void dropView() {
        super.dropView();
    }

    public void onCreate() {
        view.setFabVisibility(true);
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

    public interface View extends Presenter.View {
        void setFabVisibility(boolean visibility);
    }
}
