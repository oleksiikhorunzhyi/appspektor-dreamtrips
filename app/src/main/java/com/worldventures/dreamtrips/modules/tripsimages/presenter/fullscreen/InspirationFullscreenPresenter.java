package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class InspirationFullscreenPresenter extends FullScreenPresenter<Inspiration, InspirationFullscreenPresenter.View> {

    public InspirationFullscreenPresenter(Inspiration photo, Type tab) {
        super(photo, tab);
    }


    public interface View extends FullScreenPresenter.View {
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackingHelper.insprDetails(getAccountUserId(), photo.getFSId());
    }
}
