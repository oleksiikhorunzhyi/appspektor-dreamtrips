package com.worldventures.dreamtrips.presentation;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> {

    public MainActivityPresentation(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
        updateFaqAndTermLinks();
    }

    private void updateFaqAndTermLinks() {

    }

    public static interface View extends BasePresentation.View {

    }
}
