package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;

import javax.inject.Inject;

public class NavigationDrawerPresenter extends Presenter<NavigationDrawerPresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    LogoutDelegate logoutDelegate;

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.notificationCountChanged(db.getExclusiveNotificationsCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        logoutDelegate.setDreamSpiceManager(dreamSpiceManager);
    }

    public void logout() {
        logoutDelegate.logout();
    }

    public interface View extends Presenter.View {
        void notificationCountChanged(int count);
    }
}
