package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;

import javax.inject.Inject;

public class ActivityPresenter<VT extends Presenter.View> extends Presenter<VT> {

    @Inject
    protected Activity activity;

    @Override
    public void dropView() {
        super.dropView();
        activity = null;
    }
}
