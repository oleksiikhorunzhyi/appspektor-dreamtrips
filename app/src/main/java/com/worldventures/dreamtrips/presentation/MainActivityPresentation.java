package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.utils.busevents.UpdateSelectionEvent;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

@PresentationModel
public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> {

    private State currentState;

    @Global
    @Inject
    EventBus eventBus;

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

    public void onBackPressed() {
        currentState = fragmentCompass.getPreviousFragment();
        String title = currentState.getTitle();
        eventBus.post(new UpdateSelectionEvent());
        view.setTitle(title);
    }

    public void selectItem(State state) {
        if (!state.equals(currentState)) {
            currentState = state;
            fragmentCompass.replace(state);
        }
    }

    public static interface View extends BasePresentation.View {
        void setTitle(String title);
    }
}
