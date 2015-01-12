package com.worldventures.dreamtrips.view.presentation;

import android.os.Handler;

import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.apache.http.HttpStatus;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;

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
        Logs.e(ex);

        if (ex instanceof RetrofitError) {
            if (ex.getCause() instanceof SocketTimeoutException) {
                new Handler().postDelayed(() -> {
                    view.informUser("No internet connection");
                }, 600);
            }
            Response response = ((RetrofitError) ex).getResponse();
            if (response != null) {
                int status = response.getStatus();
                if (status == HttpStatus.SC_UNAUTHORIZED) {
                    view.informUser("401 Unauthorized");
                    new Handler().postDelayed(() -> {
                        sessionManager.logoutUser();
                        activityRouter.finish();
                        activityRouter.openLogin();
                    }, 600);
                } else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                  //  view.informUser("Internal server error");
                }
            }
        }
    }
}
