package com.worldventures.dreamtrips.core.api;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
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
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.ServerDownEvent;
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
import java.net.UnknownHostException;
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

    @Inject
    protected Context context;

    private Injector injector;

    public DreamSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        this.injector = injector;
        injector.inject(this);
    }

    public <T> void execute(final SpiceRequest<T> request, SuccessListener<T> successListener,
                            FailureListener failureListener) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 1));

        super.execute(request, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException error) {
                if (isLoginError(error) && isCredentialExist()) {
                    final UserSession userSession = appSessionHolder.get().get();
                    final String username = userSession.getUsername();
                    final String userPassword = userSession.getUserPassword();

                    loadGlobalConfig(userPassword, username, (loginResponse, spiceError) -> {
                        if (loginResponse != null) {
                            execute(request, successListener, failureListener);
                        } else {
                            failureListener.handleError(new SpiceException(spiceError.getMessage()));
                        }
                    });

                } else {
                    failureListener.handleError(new SpiceException(getErrorMessage(error)));
                }
            }

            @Override
            public void onRequestSuccess(T t) {
                successListener.onRequestSuccess(t);
            }
        });
    }

    public void login(RequestListener<LoginResponse> requestListener) {
        Optional<UserSession> userSessionOptional = appSessionHolder.get();
        if (userSessionOptional.isPresent()) {
            UserSession userSession = userSessionOptional.get();
            String username = userSession.getUsername();
            String userPassword = userSession.getUserPassword();

            loadGlobalConfig(userPassword, username, (loginResponse, error) -> {
                if (requestListener != null) {
                    if (loginResponse != null) {
                        requestListener.onRequestSuccess(loginResponse);
                    } else {
                        requestListener.onRequestFailure(error);
                    }
                }
            });
        }
    }

    public void loadGlobalConfig(String userPassword, String username, OnLoginSuccess onLoginSuccess) {
        execute(new GlobalConfigQuery.GetConfigRequest(), appConfig -> {
            ServerStatus.Status serv = appConfig.getServerStatus().getProduction();
            String status = serv.getStatus();
            String message = serv.getMessage();

            if (!"up".equalsIgnoreCase(status)) {
                onLoginSuccess.result(null, new SpiceException(message));
                eventBus.post(new ServerDownEvent(message));
            } else {
                loginUser(userPassword, username, onLoginSuccess, appConfig);
            }

        }, spiceError -> {
            onLoginSuccess.result(null, new SpiceException(getErrorMessage(spiceError)));
        });
    }

    private void loginUser(String userPassword, String username,
                           OnLoginSuccess onLoginSuccess, AppConfig appConfig) {
        execute(new LoginCommand(username, userPassword), session -> {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSession(session);
            loginResponse.setConfig(appConfig);
            handleSession(loginResponse.getSession(), loginResponse.getSession().getSsoToken(),
                    loginResponse.getConfig(), username, userPassword);
            loadStaticPagesContent(loginResponse, onLoginSuccess);
        }, spiceError -> {
            onLoginSuccess.result(null, new SpiceException(getErrorMessage(spiceError)));
        });
    }

    private void loadStaticPagesContent(LoginResponse loginResponse, OnLoginSuccess onLoginSuccess) {
        execute(new StaticPagesQuery(), staticPageConfig -> {
            updateSession(staticPageConfig);
            onLoginSuccess.result(loginResponse, null);
        }, spiceError -> {
            onLoginSuccess.result(null, new SpiceException(getErrorMessage(spiceError)));
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

    private boolean isCredentialExist() {
        Optional<UserSession> userSessionOptional = appSessionHolder.get();
        if (userSessionOptional.isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            return userSession.getUsername() != null && userSession.getUserPassword() != null;
        } else {
            return false;
        }
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

    private String getErrorMessage(SpiceException error) {
        String errorMessage = "";
        if (error != null && error.getCause() instanceof RetrofitError) {
            RetrofitError retrofitError = (RetrofitError) error.getCause();
            String body = getBody(retrofitError.getResponse());
            String message = grabDetailedMessage(body);

            if (message.isEmpty()) {
                Throwable t = retrofitError.getCause();
                if (t instanceof UnknownHostException) {
                    errorMessage = context
                            .getResources().getString(R.string.no_connection);
                }
            } else {
                errorMessage = message;
            }
        } else if (error != null && !TextUtils.isEmpty(error.getMessage())) {
            errorMessage = error.getMessage();
        }
        return errorMessage;
    }

    private String getBody(Response response) {
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

    public interface OnLoginSuccess {
        void result(LoginResponse loginResponse, SpiceException exception);
    }

    public static interface FailureListener {
        void handleError(SpiceException spiceException);
    }

    public static interface SuccessListener<T> {
        void onRequestSuccess(T t);
    }

}
