package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.File;

public class TripImagesTabsFragmentPresenter extends BasePresenter<TripImagesTabsFragmentPresenter.View> {

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

    public TripImagesTabsFragmentPresenter(View view) {
        super(view);
    }

    public void trackState(int position) {
        if (position == TripImagesListFragment.Type.MY_IMAGES.ordinal()) {
            AdobeTrackingHelper.mine(getUserId());
        } else if (position == TripImagesListFragment.Type.YOU_SHOULD_BE_HERE.ordinal()) {
            AdobeTrackingHelper.ysbh(getUserId());
        } else if (position == TripImagesListFragment.Type.MEMBER_IMAGES.ordinal()) {
            AdobeTrackingHelper.all(getUserId());
        }
    }

    public void destroy() {
         eventBus.removeAllStickyEvents();
    }

    @Override
    public void destroyView() {
        super.destroyView();
    }

    public void onCreate() {
        boolean facebookAvailable = appSessionHolder.get().get().getGlobalConfig().isFacebook_gallery_enabled();
        view.setFabVisibility(facebookAvailable);
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

    public interface View extends BasePresenter.View {
        void setFabVisibility(boolean visibility);


    }
}
