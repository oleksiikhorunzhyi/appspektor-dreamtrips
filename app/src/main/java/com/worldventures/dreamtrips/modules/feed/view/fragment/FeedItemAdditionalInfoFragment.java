package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.IFeedTabletViewDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

@Layout(R.layout.fragment_feed_item_additional_info)
public class FeedItemAdditionalInfoFragment<P extends FeedItemAdditionalInfoPresenter> extends BaseFragmentWithArgs<P, FeedAdditionalInfoBundle> implements FeedItemAdditionalInfoPresenter.View {

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    @Inject
    IFeedTabletViewDelegate feedTabletViewManager;


    @Override
    protected P createPresenter(Bundle savedInstanceState) {
        return (P) new FeedItemAdditionalInfoPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        feedTabletViewManager.setRootView(rootView);
        User user = getArgs().getUser();
        feedTabletViewManager.setUser(user, false);
        feedTabletViewManager.setOnUserClick(() -> NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId())));
    }


    @Override
    public void setupAccount(User user) {
        feedTabletViewManager.setUser(user, isFullInfoShown());
    }

    protected boolean isFullInfoShown() {
        return false;
    }

}
