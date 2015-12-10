package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class TripImageFullscreenPresenter extends FullScreenPresenter<TripImage, TripImageFullscreenPresenter.View> {

    public TripImageFullscreenPresenter(TripImage photo, TripImagesListFragment.Type tab) {
        super(photo, tab);
    }

    public interface View extends FullScreenPresenter.View {
    }

}
