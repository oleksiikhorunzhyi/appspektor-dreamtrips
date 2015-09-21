package com.worldventures.dreamtrips.modules.common.presenter;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.NotificationsCountChangedEvent;

import javax.inject.Inject;

public class NavigationDrawerPresenter extends Presenter<NavigationDrawerPresenter.View> {

    @Inject
    SnappyRepository db;

    public void onEventMainThread(NotificationsCountChangedEvent event) {
        view.notificationCountChanged(db.getNotificationCount());
    }


    public interface View extends Presenter.View {
        void notificationCountChanged(int count);
    }
}
