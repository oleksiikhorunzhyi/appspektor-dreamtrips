package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;

import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;

import javax.inject.Inject;

public class ActivityPresenter<VT extends ActivityPresenter.View> extends Presenter<VT> {

    @Inject
    protected Activity activity;

    @Override
    public void dropView() {
        super.dropView();
        activity = null;
    }

    public void onEventMainThread(UpdateUserInfoEvent event) {
        if (event.user == null || event.user.isTermsAccepted() || !canShowTermsDialog()) return;
        //
        view.showTermsDialog();
        eventBus.removeStickyEvent(event);
    }

    protected boolean canShowTermsDialog() {
        return true;
    }

    public interface View extends Presenter.View {
        void showTermsDialog();
    }
}
