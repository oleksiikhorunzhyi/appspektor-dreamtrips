package com.worldventures.dreamtrips.view.presentation;

import android.os.Handler;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;


import org.apache.http.HttpStatus;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class BasePresentation {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;
    @Inject
    protected SessionManager sessionManager;

    private IInformView view;

    public BasePresentation(IInformView view, Injector injector) {
        this.view = view;
        injector.inject(this);
    }

    public void handleError(Exception ex) {
        if (ex instanceof RetrofitError) {
            if (((RetrofitError) ex).getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                view.informUser("401 Unauthorized");
                new Handler().postDelayed(() -> {
                    sessionManager.logoutUser();
                    activityRouter.finish();
                    activityRouter.openLogin();
                }, 600);
            }
        }
    }
}
