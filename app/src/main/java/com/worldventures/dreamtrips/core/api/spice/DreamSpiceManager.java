package com.worldventures.dreamtrips.core.api.spice;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.response.LoginResponse;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.busevents.UpdateUserInfoEvent;

import org.apache.http.HttpStatus;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;

public class DreamSpiceManager extends SpiceManager {

    @Inject
    AppSessionHolder appSessionHolder;

    @Inject
    @Global
    EventBus eventBus;

    public DreamSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        injector.inject(this);
    }


    public <T> void execute(final SpiceRequest<T> request, final RequestListener<T> requestListener) {
        super.execute(request, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException error) {
                if (isLoginError(error) && isCredentialExist()) {
                    String username = appSessionHolder.get().get().getUsername();
                    String userPassword = appSessionHolder.get().get().getUserPassword();

                    login((l, e) -> {
                        if (l != null) {
                            DreamSpiceManager.super.execute(request, requestListener);
                        } else {
                            requestListener.onRequestFailure(error);
                        }
                    }, username, userPassword);
                } else {
                    requestListener.onRequestFailure(error);
                }
            }

            @Override
            public void onRequestSuccess(T t) {
                requestListener.onRequestSuccess(t);
            }
        });
    }


    private boolean handleSession(Session session, String legacyToken, S3GlobalConfig globalConfig, String username, String userPassword) {
        String sessionToken = session.getToken();
        User sessionUser = session.getUser();

        UserSession userSession = new UserSession();
        userSession.setUser(sessionUser);
        userSession.setApiToken(sessionToken);
        userSession.setLegacyApiToken(legacyToken);

        userSession.setUsername(username);
        userSession.setUserPassword(userPassword);
        userSession.setLastUpdate(System.currentTimeMillis());

        userSession.setGlobalConfig(globalConfig);

        if (sessionUser != null & sessionToken != null) {
            appSessionHolder.put(userSession);
            return true;
        }
        eventBus.post(new UpdateUserInfoEvent());
        return false;
    }

    public void login(OnLoginSuccess onLoginSuccess, String username, String userPassword) {

        DreamSpiceManager.super.execute(new S3Request.GetConfigRequest(), new RequestListener<S3GlobalConfig>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                onLoginSuccess.result(null, spiceException);
            }

            @Override
            public void onRequestSuccess(S3GlobalConfig s3GlobalConfig) {
                DreamSpiceManager.super.execute(new DreamTripsRequest.Login(username, userPassword), new RequestListener<Session>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        onLoginSuccess.result(null, spiceException);
                    }

                    @Override
                    public void onRequestSuccess(Session session) {
                        LoginResponse l = new LoginResponse();
                        l.setSession(session);
                        l.setConfig(s3GlobalConfig);
                        handleSession(l.getSession(), l.getSession().getSso_token(), l.getConfig(), username, userPassword);
                        onLoginSuccess.result(l, null);
                    }
                });
            }
        });

    }

    public static interface OnLoginSuccess {
        void result(LoginResponse loginResponse, SpiceException exception);
    }

    private boolean isCredentialExist() {
        UserSession userSession = appSessionHolder.get().get();
        return userSession.getUsername() != null && userSession.getUserPassword() != null;
    }

    private boolean isLoginError(SpiceException error) {
        if (error.getCause() instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error.getCause();
            return cause.getResponse() != null && cause.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED;
        }
        return false;
    }

}
