package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;

import javax.inject.Inject;

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
    @InjectView(R.id.feedDetailsRootView)
    ViewGroup feedDetailsRootView;

    FeedItemHeaderHelper feedItemHeaderHelper = new FeedItemHeaderHelper();

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ButterKnife.inject(feedItemHeaderHelper, rootView);
        if (getArgs().isSlave()) {
            int space = getResources().getDimensionPixelSize(R.dimen.tablet_details_spacing);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) feedDetailsRootView.getLayoutParams();
            lp.rightMargin = space;
            lp.leftMargin = space;
            feedDetailsRootView.setLayoutParams(lp);
        }

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
        feedActionHandler.init(actionView);
    }

    @Override
    public void updateContent(FeedItem feedItem) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.entity_content_container);
        if (fragment == null) setContent(feedItem);
    }

    @Override
    protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedEntityDetailsPresenter(getArgs());
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getUser() == null
                || getPresenter().getAccount().getId() == feedItem.getItem().getUser().getId();
    }
}
