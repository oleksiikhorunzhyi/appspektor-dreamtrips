package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import javax.inject.Inject;

import butterknife.InjectView;


@Layout(R.layout.adapter_item_feed_likers_panel)
public class ShortFeedItemDetailsCell extends AbstractCell<FeedItem> {

    @Inject
    Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    FragmentCompass fragmentCompass;
    @Inject
    FragmentManager fragmentManager;
    @Inject
    ActivityRouter activityRouter;

    LikersPanelHelper likersPanelHelper;

    @InjectView(R.id.likers_panel)
    TextView likersPanel;

    public ShortFeedItemDetailsCell(View view) {
        super(view);
        likersPanelHelper = new LikersPanelHelper();
    }

    @Override
    public void afterInject() {
        fragmentCompass.setContainerId(R.id.fragment_container);
        fragmentCompass.setSupportFragmentManager(fragmentManager);
        fragmentCompass.disableBackStack();
    }

    @Override
    protected void syncUIStateWithModel() {
        likersPanelHelper.setup(likersPanel, getModelObject().getItem());
        likersPanel.setOnClickListener(v -> {
            createActionPanelNavigationWrapper().navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(getModelObject().getItem().getUid()));
        });
    }

    @Override
    public void prepareForReuse() {

    }

    private NavigationWrapper createActionPanelNavigationWrapper() {
        return new NavigationWrapperFactory().componentOrDialogNavigationWrapper(
                activityRouter, fragmentCompass, tabletAnalytic
        );
    }

}
