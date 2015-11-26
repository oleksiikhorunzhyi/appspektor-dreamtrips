package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedItemDetailsFragment;
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
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    @Override
    protected void syncUIStateWithModel() {
        syncEntityDetailsFragment();
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

    private void syncEntityDetailsFragment() {
        Pair<Route, Parcelable> entityData = fragmentFactory.create(getModelObject());
        //
        FragmentManager fm = getFragmentManager();
        Fragment entityFragment = fm.findFragmentById(R.id.fragment_container);
        boolean notAdded = entityFragment == null
                || entityFragment.getView() == null || entityFragment.getView().getParent() == null
                || !entityFragment.getClass().getName().equals(entityData.first.getClazzName());
        if (notAdded) {
            NavigationConfig config = NavigationConfigBuilder.forFragment()
                    .backStackEnabled(false)
                    .fragmentManager(fm)
                    .data(entityData.second)
                    .containerId(R.id.fragment_container)
                    .build();
            router.moveTo(entityData.first, config);
        }
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
                router, fragmentManager, tabletAnalytic
        );
    }

    private FragmentManager getFragmentManager() {
        BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentById(R.id.container_main);
        if (fragment instanceof BucketTabsFragment
                && getModelObject().getType() == FeedEntityHolder.Type.BUCKET_LIST_ITEM) {
            return Queryable.from(fragment.getChildFragmentManager().getFragments()).filter(element -> {
                return ((BucketItem.BucketType) element.getArguments().getSerializable("BUNDLE_TYPE")).getName().equals(((BucketItem) getModelObject().getItem()).getType());
            }).first().getChildFragmentManager().getFragments().get(0).getChildFragmentManager();
        } else if (fragmentCompass.getCurrentFragment() instanceof FeedItemDetailsFragment) {
            return fragmentCompass.getCurrentFragment().getChildFragmentManager();
        }

        return fragmentManager;
    }

}
