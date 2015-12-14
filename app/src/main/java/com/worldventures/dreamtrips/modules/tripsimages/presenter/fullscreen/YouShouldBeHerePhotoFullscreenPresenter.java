package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

public class YouShouldBeHerePhotoFullscreenPresenter extends FullScreenPresenter<YSBHPhoto, YouShouldBeHerePhotoFullscreenPresenter.View> {

    public YouShouldBeHerePhotoFullscreenPresenter(YSBHPhoto photo, TripImagesType tab) {
        super(photo, tab);
    }

    public interface View extends FullScreenPresenter.View {
    }

}
