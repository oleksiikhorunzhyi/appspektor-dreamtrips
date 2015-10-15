package com.worldventures.dreamtrips.modules.common.presenter;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;

import javax.inject.Inject;

public class NavigationDrawerPresenter extends Presenter<NavigationDrawerPresenter.View> {

    @Inject
    SnappyRepository db;

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.notificationCountChanged(db.getExclusiveNotificationsCount());
    }


    public interface View extends Presenter.View {
        void notificationCountChanged(int count);
    }
}
