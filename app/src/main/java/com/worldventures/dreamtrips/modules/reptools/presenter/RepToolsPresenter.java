package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {

    public boolean showInvite() {
        return getAccount().isRep();
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public void trackState(int position) {
        if (position == 0) {
            TrackingHelper.successStories(getAccountUserId());
        } else if (position == 1) {
            TrackingHelper.trainingVideos(getAccountUserId());
        } else if (position == 2) {
            TrackingHelper.repEnroll(getAccountUserId());
        } else if (position == 3) {
            TrackingHelper.inviteShare(getAccountUserId());
        }
    }


    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }

}
