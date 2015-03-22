package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;

public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> {

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);

        if (getPresentationModel().isLogged()) {
            router.openMain();
        } else {
            router.openLogin();
        }

        router.finish();
    }
}
