package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.navigation.State;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> {

    private State currentState;

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

    public void selectItem(State state) {
        if (!state.equals(currentState)) {
            currentState = state;
            fragmentCompass.replace(state);
        }
    }

    public static interface View extends BasePresentation.View {

    }
}
