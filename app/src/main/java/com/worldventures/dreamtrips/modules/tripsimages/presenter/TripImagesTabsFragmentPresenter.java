package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;

public class TripImagesTabsFragmentPresenter extends Presenter<TripImagesTabsFragmentPresenter.View> {

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null || image == null || image.getFileThumbnail() == null) {
            view.informUser(error);
        } else {
            imageSelected(fragment, Uri.fromFile(new File(image.getFileThumbnail())));
        }
    };

    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null || image == null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            imageSelected(fragment, uri);
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

    @Override
    public void dropView() {
        eventBus.removeAllStickyEvents();
        super.dropView();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setFabVisibility(true);
    }

    public void imageSelected(Fragment fragment, Uri uri) {
        if (activityRouter != null) {
            activityRouter.openCreatePhoto(fragment, uri);
        }
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
