package com.worldventures.dreamtrips.modules.membership.presenter;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.membership.event.SearchFocusChangedEvent;

import java.util.ArrayList;
import java.util.List;

public class MembershipPresenter extends Presenter<MembershipPresenter.View> {

    private List<FragmentItem> items;

    @Override
    public void onInjected() {
        super.onInjected();
        items = provideScreens();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setScreens(items);
    }

    @NonNull
    private List<FragmentItem> provideScreens() {
        List<FragmentItem> screens = new ArrayList<>();
        screens.add(new FragmentItem(Route.PRESENTATION_VIDEOS, context.getString(R.string.presentations)));
        screens.add(new FragmentItem(Route.ENROLL_MEMBER, context.getString(R.string.enroll_member)));
        if (showSuggestRestaurant()) {
            screens.add(new FragmentItem(Route.ENROLL_RESTAURANT, context.getString(R.string.suggest_restaurant)));
        }
        if (showInvite()) {
            screens.add(new FragmentItem(Route.INVITE, context.getString(R.string.invite_and_share)));
        }
        return screens;
    }

    private boolean showSuggestRestaurant() {
        return featureManager.available(Feature.REP_SUGGEST_MERCHANT);
    }

    private boolean showInvite() {
        return !featureManager.available(Feature.REP_TOOLS);
    }

    public void trackState(int position) {
        FragmentItem item = items.get(position);
        switch (item.route) {
            case PRESENTATION_VIDEOS:
                TrackingHelper.memberVideos(getAccountUserId());
                break;
            case ENROLL_MEMBER:
                TrackingHelper.enrollMember(getAccountUserId());
                break;
            case ENROLL_RESTAURANT:
                TrackingHelper.enrollRestaurant(getAccountUserId());
                break;
            case INVITE:
                TrackingHelper.inviteShare(getAccountUserId());
                break;
        }
    }

    public void onEvent(SearchFocusChangedEvent event) {
        view.toggleTabStripVisibility(!event.hasFocus());
    }

    public interface View extends Presenter.View {
        void toggleTabStripVisibility(boolean isVisible);

        void setScreens(List<FragmentItem> items);
    }
}
