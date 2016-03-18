package com.messenger.ui.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;

import javax.inject.Inject;

public class MessengerActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    @Inject
    LogoutDelegate logoutDelegate;

    @Override
    public void onResume() {
        super.onResume();
        logoutDelegate.setDreamSpiceManager(dreamSpiceManager);
    }

    public void logout() {
        logoutDelegate.logout();
    }

}
