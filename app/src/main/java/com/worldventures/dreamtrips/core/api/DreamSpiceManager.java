package com.worldventures.dreamtrips.core.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.DTErrorHandler;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.session.AuthorizedDataUpdater;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.LegacyFeatureFactory;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.auth.api.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.model.LoginResponse;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
import roboguice.util.temp.Ln;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class DreamSpiceManager extends SpiceManager {

    @Inject
    protected Context context;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    protected LocalesHolder localeStorage;
    @Inject
    @Global
    protected EventBus eventBus;
    @Inject
    AuthorizedDataUpdater authorizedDataUpdater;
    @Inject
    DTErrorHandler dtErrorHandler;
    @Inject
    LogoutDelegate logoutDelegate;

    public DreamSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        injector.inject(this);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
        logoutDelegate.setDreamSpiceManager(this);
    }

    public <T> void execute(final SpiceRequest<T> request) {
        execute(request, SuccessListener.STUB, FailureListener.STUB);
    }

    public <T> void execute(final SpiceRequest<T> request, SuccessListener<T> successListener, FailureListener failureListener) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 1));
        super.execute(request, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException error) {
                processError(request, error, failureListener, (loginResponse, exception) -> {
                    if (loginResponse != null) {
                        execute(request, successListener, failureListener);
                    } else {
                        //logout, token is invalid
                        logoutDelegate.logout();
                    }
                });
            }

            @Override
            public void onRequestSuccess(T t) {
                successListener.onRequestSuccess(t);
            }
        });
    }

    public <T> void execute(final SpiceRequest<T> request, String cacheKey, long cacheExpiryDuration,
                            SuccessListener<T> successListener, FailureListener failureListener) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, 1));
        super.execute(request, cacheKey, cacheExpiryDuration, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException error) {
                processError(request, error, failureListener, (loginResponse, exception) -> {
                    if (loginResponse != null) {
                        execute(request, successListener, failureListener);
                    } else {
                        //logout, token is invalid
                        logoutDelegate.logout();
                    }
                });
            }

            @Override
            public void onRequestSuccess(T t) {
                successListener.onRequestSuccess(t);
            }
        });
    }

    private void processError(SpiceRequest request, SpiceException error, FailureListener failureListener, OnLoginSuccess onLoginSuccess) {
        if (isLoginError(error) && isCredentialExist(appSessionHolder)) {
            final UserSession userSession = appSessionHolder.get().get();
            final String username = userSession.getUsername();
            final String userPassword = userSession.getUserPassword();

            loginUser(userPassword, username, onLoginSuccess);
        } else {
            failureListener.handleError(new SpiceException(getErrorMessage(request, error), dtErrorHandler.handleSpiceError(error)));
        }
    }


    public void login(RequestListener<LoginResponse> requestListener) {
        if (isCredentialExist(appSessionHolder)) {
            UserSession userSession = appSessionHolder.get().get();
            String username = userSession.getUsername();
            String userPassword = userSession.getUserPassword();

            loginUser(userPassword, username, (loginResponse, error) -> {
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

    public void loginUser(String userPassword, String username,
                          OnLoginSuccess onLoginSuccess) {
        LoginCommand request = new LoginCommand(username, userPassword);
        execute(request, session -> {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSession(session);
            handleSession(loginResponse.getSession(), loginResponse.getSession().getSsoToken(),
                    username, userPassword);
            authorizedDataUpdater.updateData(this);
            onLoginSuccess.result(loginResponse, null);
        }, spiceError -> {
            onLoginSuccess.result(null, new SpiceException(getErrorMessage(request, spiceError), dtErrorHandler.handleSpiceError(spiceError)));
        });
    }

    public static boolean isLoginError(Exception error) {
        if (error instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        } else if (error.getCause() instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error.getCause();
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        }
        return false;
    }

    public static boolean isCredentialExist(SessionHolder<UserSession> appSessionHolder) {
        Optional<UserSession> userSessionOptional = appSessionHolder.get();
        if (userSessionOptional.isPresent()) {
            UserSession userSession = appSessionHolder.get().get();
            return userSession.getUsername() != null && userSession.getUserPassword() != null;
        } else {
            return false;
        }
    }

    private boolean handleSession(Session session, String legacyToken,
                                  String username, String userPassword) {
        String sessionToken = session.getToken();
        User sessionUser = session.getUser();

        UserSession userSession;
        if (appSessionHolder.get().isPresent()) {
            userSession = appSessionHolder.get().get();
        } else {
            userSession = new UserSession();
        }

        userSession.setUser(sessionUser);
        userSession.setApiToken(sessionToken);
        userSession.setLegacyApiToken(legacyToken);

        userSession.setUsername(username);
        userSession.setUserPassword(userPassword);
        userSession.setLastUpdate(System.currentTimeMillis());

        List<Feature> features = session.getPermissions();
        // TODO remote legacy features factory when server is ready
        List<Feature> legacyFeatures = new LegacyFeatureFactory(sessionUser).create();
        if (features != null) features.addAll(legacyFeatures);
        userSession.setFeatures(features);

        if (sessionUser != null & sessionToken != null) {
            appSessionHolder.put(userSession);
            eventBus.postSticky(new UpdateUserInfoEvent(sessionUser));
            return true;
        }

        return false;
    }

    private String getErrorMessage(SpiceRequest request, SpiceException error) {
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
            } else if (isShouldToBeProcessedLocally(request, retrofitError)) {
                errorMessage = context.getString(((DreamTripsRequest) request).getErrorMessage());
            } else {
                errorMessage = message;
            }
        } else if (error != null && !TextUtils.isEmpty(error.getMessage())) {
            errorMessage = error.getMessage();
        }
        return errorMessage;
    }

    private boolean isShouldToBeProcessedLocally(SpiceRequest request, RetrofitError retrofitError) {
        return retrofitError.getResponse().getStatus() != HttpStatus.SC_UNPROCESSABLE_ENTITY
                && request instanceof DreamTripsRequest && ((DreamTripsRequest) request).getErrorMessage() != 0;
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
            Timber.e(e, "Cant parse response body");
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
                try {
                    JSONArray o = errors.getJSONArray(key);
                    return o.getString(0);
                } catch (JSONException e) {
                    return errors.getString(key);
                }
            }

        } catch (Exception e) {
            Timber.e(e, "");
        }
        return "";
    }

    @Override
    public synchronized void start(Context context) {
        super.start(context);
    }

    public interface OnLoginSuccess {
        void result(LoginResponse loginResponse, SpiceException exception);
    }

    public interface FailureListener {
        void handleError(SpiceException spiceException);

        FailureListener STUB = spiceException -> {
        };
    }

    public interface SuccessListener<T> {
        void onRequestSuccess(T t);

        SuccessListener STUB = t -> {
        };
    }
}
