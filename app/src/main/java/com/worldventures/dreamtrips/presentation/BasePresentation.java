package com.worldventures.dreamtrips.presentation;

import android.util.Log;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;

import javax.inject.Inject;

public class BasePresentation<VT extends BasePresentation.View> {

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected AppSessionHolder appSessionHolder;

    protected final VT view;

    public BasePresentation(VT view) {
        this.view = view;
    }

    public void init() {

    }

    public void resume() {

    }

    public void handleError(Exception ex) {
        Log.e(this.getClass().getSimpleName(), "", ex);
    }

    public interface View {
        public void informUser(String stringId);
    }
}
