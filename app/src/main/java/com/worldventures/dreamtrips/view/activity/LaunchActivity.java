package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.adobe.mobile.Config;
import com.worldventures.dreamtrips.presentation.LaunchActivityPresentation;

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
