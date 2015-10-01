package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
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
public class FeedEntityDetailsFragment extends BaseFragmentWithArgs<FeedEntityDetailsPresenter, CommentsBundle> implements FeedEntityDetailsPresenter.View {

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;

    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;

    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;

    FeedItemHeaderHelper feedItemHeaderHelper = new FeedItemHeaderHelper();

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ButterKnife.inject(feedItemHeaderHelper, rootView);
    }

    @Override
    public void setHeader(FeedItem feedItem) {
        fragmentCompass.setContainerId(R.id.entity_content_container);
        fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
        Pair<Route, Parcelable> routeParcelablePair = fragmentFactory.create(feedItem);
        NavigationBuilder.create().with(fragmentCompass).data(routeParcelablePair.second).move(routeParcelablePair.first);
        actionView.setState(feedItem);
        feedActionHandler.init(actionView);
        feedItemHeaderHelper.set(feedItem, getContext());

    }


    @Override
    public void updateHeader(FeedItem feedItem) {
        actionView.setState(feedItem);
        feedActionHandler.init(actionView);
        feedItemHeaderHelper.set(feedItem, getContext());
    }


    @Override
    protected FeedEntityDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedEntityDetailsPresenter(getArgs());
    }
}
