package com.worldventures.dreamtrips.presentation;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.S3Api;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
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
public class MainActivityPresentation extends BasePresentation<MainActivityPresentation.View> {

    private State currentState;

    @Global
    @Inject
    EventBus eventBus;
    @Inject
    S3Api s3Api;

    public MainActivityPresentation(View view) {
        super(view);
    }

    public void create() {
        s3Api.getConfig(new Callback<S3GlobalConfig>() {
            @Override
            public void success(S3GlobalConfig jsonObject, Response response) {
                UserSession userSession = appSessionHolder.get().get();
                if (userSession == null) userSession = new UserSession();
                userSession.setGlobalConfig(jsonObject);
                appSessionHolder.put(userSession);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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
