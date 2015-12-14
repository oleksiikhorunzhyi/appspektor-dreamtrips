package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public class TripImageFullscreenPresenter extends FullScreenPresenter<TripImage, TripImageFullscreenPresenter.View> {

    public TripImageFullscreenPresenter(TripImage photo, TripImagesType tab) {
        super(photo, tab);
    }

    public interface View extends FullScreenPresenter.View {
    }

}
