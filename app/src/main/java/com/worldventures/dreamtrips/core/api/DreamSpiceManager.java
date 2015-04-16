package com.worldventures.dreamtrips.core.api;

import android.os.Handler;
import android.util.Log;

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
import com.worldventures.dreamtrips.modules.common.api.StaticPagesQuery;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

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

                    login(userPassword, username, (loginResponse, spiceError) -> {
                        if (loginResponse != null) {
                            DreamSpiceManager.super.execute(request, requestListener);
                        } else {
                            requestListener.onRequestFailure(new SpiceException(spiceError.getMessage()));
                        }
                    });

                } else if (error != null && error.getCause() instanceof RetrofitError) {
                    RetrofitError retrofitError = (RetrofitError) error.getCause();
                    String body = getBody(retrofitError.getResponse());
                    String message = grabDetailedMessage(body);
                    requestListener.onRequestFailure(new SpiceException(message));
                } else {
                    requestListener.onRequestFailure(new SpiceException(""));
                }
            }

            @Override
            public void onRequestSuccess(T t) {
                requestListener.onRequestSuccess(t);
            }
        });
    }

    private boolean handleSession(Session session, String legacyToken, AppConfig globalConfig,
                                  String username, String userPassword) {
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

    private void updateSession(StaticPageConfig staticPageConfig) {
        UserSession userSession = appSessionHolder.get().get();
        userSession.setStaticPageConfig(staticPageConfig);
        appSessionHolder.put(userSession);
    }

    public void login(RequestListener<LoginResponse> requestListener) {
        final UserSession userSession = appSessionHolder.get().get();
        final String username = userSession.getUsername();
        final String userPassword = userSession.getUserPassword();

        login(userPassword, username, (loginResponse, error) -> {
            if (requestListener != null) {
                if (loginResponse != null) {
                    requestListener.onRequestSuccess(loginResponse);
                } else {
                    requestListener.onRequestFailure(error);
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
                ServerStatus.Status serv = appConfig.getServerStatus().getProduction();
                String status = serv.getStatus();
                String message = serv.getMessage();

                if (!"up".equalsIgnoreCase(status)) {
                    onLoginSuccess.result(null, new SpiceException(message));
                } else {
                    DreamSpiceManager.super.execute(new LoginCommand(username, userPassword), new RequestListener<Session>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            onLoginSuccess.result(null, new SpiceException(""));
                        }

                        @Override
                        public void onRequestSuccess(Session session) {
                            LoginResponse loginResponse = new LoginResponse();
                            loginResponse.setSession(session);
                            loginResponse.setConfig(appConfig);
                            handleSession(loginResponse.getSession(), loginResponse.getSession().getSsoToken(),
                                    loginResponse.getConfig(), username, userPassword);
                            loadStaticPagesContent(loginResponse, onLoginSuccess);
                        }
                    });
                }
            }
        });
    }

    public void loadStaticPagesContent(LoginResponse loginResponse, OnLoginSuccess onLoginSuccess) {
        DreamSpiceManager.super.execute(new StaticPagesQuery(), new RequestListener<StaticPageConfig>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                onLoginSuccess.result(null, spiceException);
            }

            @Override
            public void onRequestSuccess(StaticPageConfig staticPageConfig) {
                updateSession(staticPageConfig);
                onLoginSuccess.result(loginResponse, null);
            }
        });
    }

    public void uploadPhoto(ImageUploadTask task) {
        try {
            UploadTripPhotoCommand request = new UploadTripPhotoCommand(task, injector);
            execute(request, new RequestListener<Photo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    new Handler().postDelayed(() -> eventBus.post(new PhotoUploadFailedEvent(task.getTaskId())), 300);
                }

                @Override
                public void onRequestSuccess(Photo photo) {
                    //nothing to do here
                }
            });
        } catch (Exception e) {
            Log.e(DreamSpiceManager.class.getSimpleName(), "", e);
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

    public interface OnLoginSuccess {
        void result(LoginResponse loginResponse, SpiceException exception);
    }

    public String getBody(Response response) {
        String result = "";
        try {
            TypedInput body = response.getBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }

            result = out.toString();
        } catch (Exception e) {
            Log.e(DreamSpiceManager.class.getSimpleName(), "", e);
        }
        return result;
    }

    private String grabDetailedMessage(String response) {
        try {
            JSONObject parent = new JSONObject(response);
            JSONObject errors = parent.getJSONObject("errors");

            Iterator<?> keys = errors.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONArray o = errors.getJSONArray(key);
                return o.getString(0);
            }

        } catch (Exception e) {
            Log.e(DreamSpiceManager.class.getSimpleName(), "", e);
        }
        return "";
    }
}
