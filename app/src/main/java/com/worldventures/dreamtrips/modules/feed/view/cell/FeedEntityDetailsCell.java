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
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemHeaderHelper;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


@Layout(R.layout.adapter_item_feed_details_wrapper)
public class FeedEntityDetailsCell extends AbstractCell<FeedItem> {

    @Inject
    Presenter.TabletAnalytic tabletAnalytic;

    @Inject
    FragmentCompass fragmentCompass;

    @Inject
    ActivityRouter activityRouter;

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;

    @InjectView(R.id.fragment_container)
    ViewGroup viewGroup;

    @Inject
    FragmentManager fragmentManager;

    @InjectView(R.id.likers_panel)
    TextView likersPanel;

    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;
    @Inject
    SessionHolder<UserSession> sessionHolder;


    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;
    FeedItemHeaderHelper feedItemHeaderHelper = new FeedItemHeaderHelper();
    LikersPanelHelper likersPanelHelper = new LikersPanelHelper();

    public FeedEntityDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        fragmentCompass.setContainerId(R.id.fragment_container);
        fragmentCompass.setSupportFragmentManager(fragmentManager);
        fragmentCompass.disableBackStack();
        Pair<Route, Parcelable> routeParcelablePair = fragmentFactory.create(getModelObject());
        NavigationBuilder.create().with(fragmentCompass).data(routeParcelablePair.second).attach(routeParcelablePair.first);

        likersPanelHelper.setup(likersPanel, getModelObject().getItem());
        likersPanel.setOnClickListener(v -> {
            createActionPanelNavigationWrapper().navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(getModelObject().getItem().getUid()));
        });

        actionView.setState(getModelObject(), isForeignItem(getModelObject()));
        feedActionHandler.init(actionView, createActionPanelNavigationWrapper());
        ButterKnife.inject(feedItemHeaderHelper, itemView);

        feedItemHeaderHelper.set(getModelObject(), itemView.getContext(), sessionHolder.get().get().getUser().getId(), true);
        feedItemHeaderHelper.setOnEditClickListener(v -> getEventBus().post(new FeedEntityEditClickEvent(getModelObject(), v)));
    }

    @Override
    public void prepareForReuse() {

    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getUser() == null
                || sessionHolder.get().get().getUser().getId() == feedItem.getItem().getUser().getId();
    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                activityRouter, fragmentCompass, tabletAnalytic
        );
    }

}
