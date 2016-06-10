package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;
import com.worldventures.dreamtrips.modules.feed.view.util.LikersPanelHelper;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

import javax.inject.Inject;

import butterknife.InjectView;

public abstract class BaseFeedCell<ITEM extends FeedItem> extends AbstractCell<ITEM> {

    @Inject
    FeedActionPanelViewActionHandler feedActionHandler;
    @Inject
    Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    protected FragmentManager fragmentManager;
    @Inject
    protected SessionHolder<UserSession> sessionHolder;

    @InjectView(R.id.actionView)
    FeedActionPanelView actionView;
    @InjectView(R.id.likers_panel)
    TextView likersPanel;

    LikersPanelHelper likersPanelHelper;

    private NavigationWrapper navigationWrapper;
    private boolean syncUIStateWithModelWasCalled = false;

    public BaseFeedCell(View view) {
        super(view);
        likersPanelHelper = new LikersPanelHelper();
    }

    @Override
    public void afterInject() {
        super.afterInject();
        navigationWrapper = new NavigationWrapperFactory()
                .componentOrDialogNavigationWrapper(router, fragmentManager, tabletAnalytic);
    }

    @Override
    protected void syncUIStateWithModel() {
        syncUIStateWithModelWasCalled = true;
        //
        actionView.setState(getModelObject(), isForeignItem(getModelObject()));
        actionView.setOnMoreClickListener(feedItem -> onMore());
        actionView.setOnDeleteClickListener(feedItem -> onDelete());
        actionView.setOnEditClickListener(feedItem -> onEdit());
        feedActionHandler.init(actionView, navigationWrapper);
        //
        if (likersPanel != null) {
            likersPanelHelper.setup(likersPanel, getModelObject().getItem());
            likersPanel.setOnClickListener(v -> navigationWrapper.navigate(Route.USERS_LIKED_CONTENT,
                    new UsersLikedEntityBundle(getModelObject().getItem().getUid(), getModelObject().getItem().getLikesCount())));
        }
    }

    private boolean isForeignItem(FeedItem feedItem) {
        Optional<UserSession> userSessionOptional = sessionHolder.get();
        return feedItem.getItem().getOwner() == null
                || !userSessionOptional.isPresent()
                || userSessionOptional.get().getUser().getId() == (feedItem.getItem().getOwner().getId());
    }

    @Override
    public void fillWithItem(ITEM item) {
        syncUIStateWithModelWasCalled = false;
        super.fillWithItem(item);
        if (!syncUIStateWithModelWasCalled) {
            throw new IllegalStateException("super.syncUIStateWithModel was not called");
        }
    }

    protected void showMoreDialog(@MenuRes int menuRes, @StringRes int headerDelete, @StringRes int textDelete) {
        actionView.showMoreDialog(menuRes, headerDelete, textDelete);
    }

    protected void onDelete() {
        sendAnalyticEvent(TrackingHelper.ATTRIBUTE_DELETE);
    }

    protected void onEdit() {
        sendAnalyticEvent(TrackingHelper.ATTRIBUTE_EDIT);
    }

    private void sendAnalyticEvent(String eventType) {
        FeedItem feedItem = getModelObject();
        getEventBus().post(new FeedItemAnalyticEvent(eventType, feedItem.getItem().getUid(), feedItem.getType()));
    }

    protected abstract void onMore();

    @Override
    public void prepareForReuse() {

    }
}
