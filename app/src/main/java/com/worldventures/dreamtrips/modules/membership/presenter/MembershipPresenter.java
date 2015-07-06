package com.worldventures.dreamtrips.modules.membership.presenter;

import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

public class MembershipPresenter extends Presenter<MembershipPresenter.View> {

    public boolean showInvite() {
        return !featureManager.available(Feature.REP_TOOLS);
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public void trackState(int position) {
        if (position == 0) {
            TrackingHelper.memberVideos(getAccountUserId());
        } else if (position == 1) {
            TrackingHelper.enroll(getAccountUserId());
        } else if (position == 2) {
            TrackingHelper.inviteShare(getAccountUserId());
        }
    }

    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }
}
