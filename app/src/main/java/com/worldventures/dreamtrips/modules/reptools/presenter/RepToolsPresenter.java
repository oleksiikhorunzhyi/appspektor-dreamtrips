package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {

    public boolean showInvite() {
        return getUser().isRep();
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public void trackState(int position) {
        if (position == 0) {
            TrackingHelper.successStories(getUserId());
        } else if (position == 1) {
            TrackingHelper.trainingVideos(getUserId());
        } else if (position == 2) {
            TrackingHelper.repEnroll(getUserId());
        } else if (position == 3) {
            TrackingHelper.inviteShare(getUserId());
        }
    }


    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }

}
