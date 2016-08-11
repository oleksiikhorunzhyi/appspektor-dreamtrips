package com.worldventures.dreamtrips.modules.membership.presenter;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipPresenter extends Presenter<MembershipPresenter.View> {

    private List<FragmentItem> items;
    @Inject
    SearchFocusChangedDelegate searchFocusChangedDelegate;

    @Override
    public void onInjected() {
        super.onInjected();
        items = provideScreens();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setScreens(items);
        searchFocusChangedDelegate.getObservable()
                .compose(bindViewToMainComposer())
                .subscribe(hasFocus -> view.toggleTabStripVisibility(!hasFocus));
    }

    @NonNull
    private List<FragmentItem> provideScreens() {
        List<FragmentItem> screens = new ArrayList<>();
        screens.add(new FragmentItem(Route.PRESENTATION_VIDEOS, context.getString(R.string.presentations)));
        screens.add(new FragmentItem(Route.ENROLL_MEMBER, context.getString(R.string.enroll_member)));
        if (showEnrollMerchant()) {
            screens.add(new FragmentItem(Route.ENROLL_MERCHANT, context.getString(R.string.suggest_merchant_title)));
        }
        if (showInvite()) {
            screens.add(new FragmentItem(Route.INVITE, context.getString(R.string.invite_and_share)));
        }
        if (showPodcasts()) {
            screens.add(new FragmentItem(Route.PODCASTS, context.getString(R.string.podcasts)));
        }
        return screens;
    }

    private boolean showEnrollMerchant() {
        return featureManager.available(Feature.REP_SUGGEST_MERCHANT);
    }

    private boolean showInvite() {
        return !featureManager.available(Feature.REP_TOOLS);
    }

    private boolean showPodcasts() {
        return featureManager.available(Feature.MEMBERSHIP);
    }

    public interface View extends Presenter.View {

        void toggleTabStripVisibility(boolean isVisible);

        void setScreens(List<FragmentItem> items);
    }
}
