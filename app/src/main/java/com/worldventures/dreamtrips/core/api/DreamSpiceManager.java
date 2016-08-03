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
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
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
import java.net.ConnectException;
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

    @Inject protected Context context;
    @Inject protected SessionHolder<UserSession> appSessionHolder;
    @Inject @Global protected EventBus eventBus;
    @Inject DTErrorHandler dtErrorHandler;
    @Inject LogoutDelegate logoutDelegate;

    private final ErrorParser errorParser;

    public DreamSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        injector.inject(this);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
        errorParser = new ErrorParser(context);
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

    public <T> void clearExecute(final SpiceRequest<T> request, SuccessListener<T> successListener, FailureListener failureListener) {
        super.execute(request, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                failureListener.handleError(spiceException);
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
        failureListener.handleError(getParcedException(request, error));
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

    public void loginUser(String userPassword, String username, OnLoginSuccess onLoginSuccess) {
        LoginCommand request = new LoginCommand(username, userPassword);
        execute(request, session -> {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSession(session);
            handleSession(loginResponse.getSession(), loginResponse.getSession().getSsoToken(),
                    username, userPassword);
            onLoginSuccess.result(loginResponse, null);
        }, error -> {
            onLoginSuccess.result(null, getParcedException(request, error));
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
        userSession.setFeatures(features);

        if (sessionUser != null & sessionToken != null) {
            appSessionHolder.put(userSession);
            eventBus.postSticky(new UpdateUserInfoEvent(sessionUser));
            return true;
        }

        return false;
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

    private static class ErrorParser {

        Context context;

        public ErrorParser(Context context) {
            this.context = context;
        }

        public String parseErrorMessage(SpiceRequest request, SpiceException error) {
            String errorMessage = "";
            if (error != null && error.getCause() instanceof RetrofitError) {
                RetrofitError retrofitError = (RetrofitError) error.getCause();
                String message = getDetailedMessage(retrofitError);

                if (TextUtils.isEmpty(message)) {
                    Throwable t = retrofitError.getCause();
                    if (t instanceof UnknownHostException || t instanceof ConnectException) {
                        errorMessage = context.getResources().getString(R.string.no_connection);
                    } else {
                        if (isShouldToBeProcessedLocally(request, retrofitError)) {
                            errorMessage = context.getString(((DreamTripsRequest) request).getErrorMessage());
                        }
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
            return (retrofitError.getResponse() == null || retrofitError.getResponse().getStatus() != HttpStatus.SC_UNPROCESSABLE_ENTITY)
                    && request instanceof DreamTripsRequest && ((DreamTripsRequest) request).getErrorMessage() != 0;
        }

        private String getDetailedMessage(RetrofitError error) {
            Response response = error.getResponse();
            if (response == null) return null;
            String body = getBody(response);
            if (TextUtils.isEmpty(body)) return null;
            try {
                JSONObject parent = new JSONObject(body);
                JSONObject errors = parent.getJSONObject("errors");
                Iterator<?> keys = errors.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    try {
                        JSONArray arr = errors.getJSONArray(key);
                        return arr.getString(0);
                    } catch (JSONException e) {
                        return errors.getString(key);
                    }
                }

            } catch (Exception e) {
                Timber.e(e, "Can't get error message from response");
            }
            return null;
        }

        private String getBody(Response response) {
            String result = null;
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
    }

    private SpiceException getParcedException(SpiceRequest request, SpiceException error) {
        String detailMessage = errorParser.parseErrorMessage(request, error);
        Throwable handledError = dtErrorHandler.handleSpiceError(error);
        return new SpiceException(detailMessage, handledError);
    }
}
