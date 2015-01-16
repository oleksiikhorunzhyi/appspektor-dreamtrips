package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.IllegalCuurentUserState;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;

import javax.inject.Inject;

public class BasePresentation<VT extends BasePresentation.View> {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected SessionManager sessionManager;

    protected final VT view;

    public BasePresentation(VT view) {
        this.view = view;
    }

    public void resume() {

    }

    public void handleError(Exception ex) {
        if (ex instanceof IllegalCuurentUserState) {
            sessionManager.logoutUser();
            activityRouter.finish();
            activityRouter.openLogin();
        }
    }

    public interface View {
        public void informUser(String stringId);
    }
}
