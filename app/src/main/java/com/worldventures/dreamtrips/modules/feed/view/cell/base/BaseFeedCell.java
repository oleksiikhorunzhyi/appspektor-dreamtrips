package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedActionPanelViewActionHandler;

import javax.inject.Inject;

import butterknife.InjectView;

public class BaseFeedCell<ITEM extends FeedItem> extends AbstractCell<ITEM> {

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

    private NavigationWrapper navigationWrapper;
    private boolean syncUIStateWithModelWasCalled = false;

    public BaseFeedCell(View view) {
        super(view);
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

        actionView.setState(getModelObject(), isForeignItem(getModelObject()));

        feedActionHandler.init(actionView, navigationWrapper);
    }

    private boolean isForeignItem(FeedItem feedItem) {
        return feedItem.getItem().getOwner() == null
                || sessionHolder.get().get().getUser().getId() == (feedItem.getItem().getOwner().getId());
    }

    @Override
    public void fillWithItem(ITEM item) {
        syncUIStateWithModelWasCalled = false;
        super.fillWithItem(item);
        if (!syncUIStateWithModelWasCalled) {
            throw new IllegalStateException("super.syncUIStateWithModel was not called");
        }
    }

    @Override
    public void prepareForReuse() {

    }
}
