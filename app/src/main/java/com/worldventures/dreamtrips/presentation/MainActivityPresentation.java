package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.config.ServerStatus;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.busevents.UpdateSelectionEvent;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class MainActivityPresentation extends BaseActivityPresentation<MainActivityPresentation.View> {

    private State currentState;

    @Global
    @Inject
    EventBus eventBus;

    public MainActivityPresentation(View view) {
        super(view);
    }

    public void create() {
        loadS3Config();
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
