package com.worldventures.dreamtrips.modules.reptools.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

import java.util.ArrayList;
import java.util.List;

public class RepToolsPresenter extends Presenter<RepToolsPresenter.View> {

    private List<FragmentItem> screens;

    @Override
    public void onInjected() {
        super.onInjected();
        screens = provideScreens();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setScreens(screens);
    }

    private List<FragmentItem> provideScreens() {
        List<FragmentItem> items = new ArrayList<>();
        items.add(new FragmentItem(Route.TRAINING_VIDEOS, context.getString(R.string.training_videos)));
        items.add(new FragmentItem(Route.ENROLL_REP, context.getString(R.string.rep_enrollment)));
        items.add(new FragmentItem(Route.SUCCESS_STORY_LIST, context.getString(R.string.success_stories)));
        if (showInvite()) {
            items.add(new FragmentItem(Route.INVITE, context.getString(R.string.invite_and_share)));
        }
        return items;
    }

    public boolean showInvite() {
        return featureManager.available(Feature.REP_TOOLS);
    }

    public void trackState(int position) {
        FragmentItem item = screens.get(position);
        switch (item.route) {
            case TRAINING_VIDEOS:
                TrackingHelper.trainingVideos(getAccountUserId());
                break;
            case ENROLL_REP:
                TrackingHelper.enrollRep(getAccountUserId());
                break;
            case SUCCESS_STORY_LIST:
                TrackingHelper.successStories(getAccountUserId());
                break;
            case INVITE:
                TrackingHelper.actionRepToolsInviteShare(TrackingHelper.ATTRIBUTE_VIEW);
                break;
        }
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public interface View extends Presenter.View {
        void setScreens(List<FragmentItem> items);

        void toggleTabStripVisibility(boolean isVisible);
    }
}
