package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresentation;

public class LaunchActivity extends PresentationModelDrivenActivity<LaunchActivityPresentation> {

    @Override
    protected LaunchActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresentation(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        this.getPresentationModel().onCreate();
    }
}
