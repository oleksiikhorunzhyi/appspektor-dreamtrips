package com.worldventures.dreamtrips.core.api;

import android.os.Handler;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.auth.api.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.model.LoginResponse;
import com.worldventures.dreamtrips.modules.common.api.GlobalConfigQuery;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import org.apache.http.HttpStatus;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;

public class DreamSpiceManager extends SpiceManager {

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    @Inject
    @Global
    protected EventBus eventBus;

    private Injector injector;

    public DreamSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        this.injector = injector;
        injector.inject(this);
    }

    public <T> void execute(final SpiceRequest<T> request, final RequestListener<T> requestListener) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 1));
        super.execute(request, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException error) {
                if (isLoginError(error) && isCredentialExist()) {
                    final UserSession userSession = appSessionHolder.get().get();
                    final String username = userSession.getUsername();
                    final String userPassword = userSession.getUserPassword();

                    login(userPassword, username, (l, e) -> {
                        if (l != null) {
                            DreamSpiceManager.super.execute(request, requestListener);
                        } else {
                            requestListener.onRequestFailure(error);
                        }
                    });
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

    private boolean handleSession(Session session, String legacyToken, AppConfig globalConfig, String username, String userPassword) {
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

    public void login(RequestListener<LoginResponse> requestListener) {
        final UserSession userSession = appSessionHolder.get().get();
        final String username = userSession.getUsername();
        final String userPassword = userSession.getUserPassword();

        login(userPassword, username, (l, e) -> {
            if (requestListener != null) {
                if (l != null) {
                    requestListener.onRequestSuccess(l);
                } else {
                    requestListener.onRequestFailure(e);
                }
            }
        });
    }

    public void login(String userPassword, String username, OnLoginSuccess onLoginSuccess) {

        DreamSpiceManager.super.execute(new GlobalConfigQuery.GetConfigRequest(), new RequestListener<AppConfig>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                onLoginSuccess.result(null, spiceException);
            }

            @Override
            public void onRequestSuccess(AppConfig appConfig) {
                DreamSpiceManager.super.execute(new LoginCommand(username, userPassword), new RequestListener<Session>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        onLoginSuccess.result(null, spiceException);
                    }

                    @Override
                    public void onRequestSuccess(Session session) {
                        LoginResponse l = new LoginResponse();
                        l.setSession(session);
                        l.setConfig(appConfig);
                        handleSession(l.getSession(), l.getSession().getSsoToken(), l.getConfig(), username, userPassword);
                        onLoginSuccess.result(l, null);
                    }
                });
            }
        });
    }

    public void uploadPhoto(ImageUploadTask task) {
        try {
            UploadTripPhotoCommand request = new UploadTripPhotoCommand(task);
            injector.inject(request);
            execute(request, new RequestListener<Photo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    new Handler().postDelayed(() -> eventBus.post(new PhotoUploadFailedEvent(task.getTaskId())), 300);
                }

                @Override
                public void onRequestSuccess(Photo photo) {

                }
            });
        } catch (Exception e) {
            new Handler().postDelayed(() -> eventBus.post(new PhotoUploadFailedEvent(task.getTaskId())), 300);

        }
    }

    private boolean isCredentialExist() {
        UserSession userSession = appSessionHolder.get().get();
        return userSession.getUsername() != null && userSession.getUserPassword() != null;
    }

    public static boolean isLoginError(Exception error) {
        if (error instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED;
        } else if (error.getCause() instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error.getCause();
            return cause.getResponse() != null && cause.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED;
        }
        return false;
    }

    public static interface OnLoginSuccess {
        void result(LoginResponse loginResponse, SpiceException exception);
    }

}
