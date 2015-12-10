package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class YouShouldBeHerePhotoFullscreenPresenter extends FullScreenPresenter<YSBHPhoto, YouShouldBeHerePhotoFullscreenPresenter.View> {

    public YouShouldBeHerePhotoFullscreenPresenter(YSBHPhoto photo, TripImagesListFragment.Type tab) {
        super(photo, tab);
    }

    public interface View extends FullScreenPresenter.View {
    }

}
