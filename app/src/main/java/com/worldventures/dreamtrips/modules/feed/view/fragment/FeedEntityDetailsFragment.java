package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedTabletViewManager;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;

@Layout(R.layout.fragment_feed_entity_details)
public class FeedEntityDetailsFragment extends BaseFragmentWithArgs<FeedEntityDetailsPresenter, FeedEntityDetailsBundle> implements FeedEntityDetailsPresenter.View {

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;
    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;
    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;

    FeedItemHeaderHelper feedItemHeaderHelper = new FeedItemHeaderHelper();
    FeedTabletViewManager feedTabletViewManager;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ButterKnife.inject(feedItemHeaderHelper, rootView);
        feedTabletViewManager = new FeedTabletViewManager(rootView);

    }

    @Override
    public void setHeader(FeedItem feedItem) {
        feedItemHeaderHelper.set(feedItem, getContext(), getPresenter().getAccount().getId(), true);
        feedItemHeaderHelper.setOnEditClickListener(v -> eventBus.post(new FeedEntityEditClickEvent(feedItem, v)));
    }

    @Override
    public void setContent(FeedItem feedItem) {
        fragmentCompass.setContainerId(R.id.entity_content_container);
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        Pair<Route, Parcelable> routeParcelablePair = fragmentFactory.create(feedItem);
        NavigationBuilder.create().with(fragmentCompass).data(routeParcelablePair.second).move(routeParcelablePair.first);
    }

    @Override
    public void setSocial(FeedItem feedItem) {
        actionView.setState(feedItem, isForeignItem(feedItem));
        feedActionHandler.init(actionView, createActionPanelNavigationWrapper());
        feedItemHeaderHelper.set(feedItem, getContext(), getPresenter().getAccount().getId(), true);
        feedItemHeaderHelper.setOnEditClickListener(v -> eventBus.post(new FeedEntityEditClickEvent(feedItem, v)));
        User user = feedItem.getItem().getUser();
        feedTabletViewManager.setUser(user, false);
        feedTabletViewManager.setOnUserClick(() -> NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId())));
    }

    @Override
    public void updateContent(FeedItem feedItem) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.entity_content_container);
        if (fragment == null) setContent(feedItem);

        fragmentCompass.setContainerId(R.id.entity_coments_container);
        NavigationBuilder.create().with(fragmentCompass).data(new CommentsBundle(feedItem.getItem())).attach(Route.COMMENTS);

    }

    @Override
    protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedEntityDetailsPresenter(getArgs());
    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                activityRouter, fragmentCompass, this
        );
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getUser() == null
                || getPresenter().getAccount().getId() == feedItem.getItem().getUser().getId();
    }
}
