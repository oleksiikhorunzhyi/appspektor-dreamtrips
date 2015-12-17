package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryListFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuggestRestaurantFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {

    public boolean showInvite() {
        return featureManager.available(Feature.REP_TOOLS);
    }

    public boolean showSuggestMerchant() {
        return featureManager.available(Feature.REP_SUGGEST_MERCHANT);
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public void trackState(Class fragmentClazz) {
        if (fragmentClazz.equals(SuccessStoryListFragment.class)) {
            TrackingHelper.successStories(getAccountUserId());
        } else if (fragmentClazz.equals(TrainingVideosFragment.class)) {
            TrackingHelper.trainingVideos(getAccountUserId());
        } else if (fragmentClazz.equals(StaticInfoFragment.EnrollRepFragment.class)) {
            TrackingHelper.repEnroll(getAccountUserId());
        } else if (fragmentClazz.equals(InviteFragment.class)) {
            TrackingHelper.actionRepToolsInviteShare(TrackingHelper.ATTRIBUTE_VIEW);
        } else if (fragmentClazz.equals(SuggestRestaurantFragment.class)) {
            TrackingHelper.dtlSuggestMerchantView();
        }
    }


    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);
    }
}
