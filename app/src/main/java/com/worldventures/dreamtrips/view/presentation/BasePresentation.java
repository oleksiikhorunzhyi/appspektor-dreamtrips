package com.worldventures.dreamtrips.view.presentation;

import android.os.Handler;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.core.IllegalCuurentUserState;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.model.DTServerError;
import com.worldventures.dreamtrips.core.model.response.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.utils.CommonUtils;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.ConnectException;
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

    protected IInformView view;

    public BasePresentation(IInformView view, Injector injector) {
        this.view = view;
        injector.inject(this);
    }

    public void handleError(Exception ex) {
        Logs.e(ex);
        if (ex instanceof IllegalCuurentUserState) {
            sessionManager.logoutUser();
            activityRouter.finish();
            activityRouter.openLogin();
        }
        if (ex instanceof ConnectException) {
            noInternetConnectionErrorShow();
        } else if (ex instanceof RetrofitError) {
            handleRetrofitError(ex);
        } else if (ex.getCause() instanceof SocketTimeoutException) {
            noInternetConnectionErrorShow();

        }
    }

    private void handleRetrofitError(Exception ex) {
        Response response = ((RetrofitError) ex).getResponse();
        if (response != null) {
            int status = response.getStatus();
            String body = getBody(response);
            ErrorResponse er = null;
            try {
                er = getGson().fromJson(body, ErrorResponse.class);
            } catch (Exception e) {//error body is not json or it is just a null :)
                Logs.e(e);
            }
            switch (status) {
                case HttpStatus.SC_UNAUTHORIZED:
                    view.informUser("401 Unauthorized");
                    new Handler().postDelayed(() -> {
                        sessionManager.logoutUser();
                        activityRouter.finish();
                        activityRouter.openLogin();
                    }, 1000);
                    break;
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    //  view.informUser("Internal server error");
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    if (er != null) {
                        view.informUser(er.getErrors().getErrorString());
                    }
                    break;
            }
        }
    }

    private String getBody(Response response) {
        String body = null;
        try {
            body = CommonUtils.convertStreamToString(response.getBody().in());
        } catch (IOException e) {
            Logs.e(e);
        }
        return body;
    }

    private void noInternetConnectionErrorShow() {
        new Handler().postDelayed(() -> {
            view.informUser("No internet connection");
        }, 600);
    }

    private Gson getGson() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson;
    }
}
