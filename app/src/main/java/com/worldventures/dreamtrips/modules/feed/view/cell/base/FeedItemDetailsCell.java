package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.OnClick;
import butterknife.Optional;

public abstract class FeedItemDetailsCell<T extends FeedItem> extends BaseFeedCell<T> {

    FeedItemCommonDataHelper feedItemCommonDataHelper;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    public FeedItemDetailsCell(View view) {
        super(view);
        feedItemCommonDataHelper = new FeedItemCommonDataHelper(view.getContext());
        feedItemCommonDataHelper.attachView(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        feedItemCommonDataHelper.set(getModelObject(), sessionHolder.get().get().getUser().getId(), false);
    }

    public void openItemDetails() {
        Route detailsRoute = Route.FEED_ITEM_DETAILS;
        FeedDetailsBundle bundle = new FeedDetailsBundle(getModelObject());
        if (tabletAnalytic.isTabletLandscape()) {
            bundle.setSlave(true);
        }
        router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity()
                .data(bundle)
                .build());
    }

    @Optional
    @OnClick(R.id.feed_header_avatar)
    void eventOwnerClicked() {
        User user = getModelObject().getLinks().getUsers().get(0);
        router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(user))
                .build());
    }

}
