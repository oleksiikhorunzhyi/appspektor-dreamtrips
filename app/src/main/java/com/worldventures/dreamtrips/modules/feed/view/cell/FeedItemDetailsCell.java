package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import javax.inject.Inject;

import butterknife.InjectView;


@Layout(R.layout.adapter_item_feed_details_wrapper)
public class FeedItemDetailsCell extends AbstractCell<FeedItem> {

    @Inject
    Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    FragmentCompass fragmentCompass;
    @Inject
    ActivityRouter activityRouter;
    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;
    @Inject
    FragmentManager fragmentManager;
    @Inject
    SessionHolder<UserSession> sessionHolder;
    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;
    //
    FeedItemCommonDataHelper feedItemCommonDataHelper;
    LikersPanelHelper likersPanelHelper;

    @InjectView(R.id.fragment_container)
    ViewGroup viewGroup;
    @InjectView(R.id.likers_panel)
    TextView likersPanel;
    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;

    public FeedItemDetailsCell(View view) {
        super(view);
        feedItemCommonDataHelper = new FeedItemCommonDataHelper(view.getContext());
        feedItemCommonDataHelper.attachView(view);
        likersPanelHelper = new LikersPanelHelper();
    }

    @Override
    public void afterInject() {
        fragmentCompass.setContainerId(R.id.fragment_container);
        fragmentCompass.setSupportFragmentManager(fragmentManager);
        fragmentCompass.disableBackStack();
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    @Override
    protected void syncUIStateWithModel() {
        Pair<Route, Parcelable> routeParcelablePair = fragmentFactory.create(getModelObject());
        NavigationBuilder.create().with(fragmentCompass).data(routeParcelablePair.second).move(routeParcelablePair.first);
        //
        feedItemCommonDataHelper.set(getModelObject(), sessionHolder.get().get().getUser().getId(), true);
        feedItemCommonDataHelper.setOnEditClickListener(v -> getEventBus().post(new FeedEntityEditClickEvent(getModelObject(), v)));
        //
        likersPanelHelper.setup(likersPanel, getModelObject().getItem());
        likersPanel.setOnClickListener(v -> {
            createActionPanelNavigationWrapper().navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(getModelObject().getItem().getUid()));
        });
        //
        actionView.setState(getModelObject(), isForeignItem(getModelObject()));
        feedActionHandler.init(actionView, createActionPanelNavigationWrapper());
    }

    @Override
    public void prepareForReuse() {

    }


    public void onEventMainThread(FeedEntityCommentedEvent event) {
        if (event.getFeedEntity().equals(getModelObject().getItem())) {
            actionView.setState(getModelObject(), isForeignItem(getModelObject()));
        }
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getOwner() == null
                || sessionHolder.get().get().getUser().getId() == feedItem.getItem().getOwner().getId();
    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                activityRouter, fragmentCompass, tabletAnalytic
        );
    }

}
