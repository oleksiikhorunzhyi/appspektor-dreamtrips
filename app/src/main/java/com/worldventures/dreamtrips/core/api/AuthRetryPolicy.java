package com.worldventures.dreamtrips.core.api;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import io.techery.janet.http.exception.HttpException;
import retrofit.RetrofitError;
import rx.functions.Func0;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class AuthRetryPolicy {

    private final SessionHolder<UserSession> appSessionHolder;

    public AuthRetryPolicy(SessionHolder<UserSession> appSessionHolder) {
        this.appSessionHolder = appSessionHolder;
    }

    public boolean handle(Throwable apiError, Func0<Session> loginCall) {
        if (shouldRetry(apiError)) {
            Session session = loginCall.call();
            if (session != null) {
                handleSession(session);
                return true;
            }
        }
        return false;
    }

    private boolean shouldRetry(Throwable error) {
        Timber.d("Check retry");
        return isLoginError(error) && isCredentialExist(appSessionHolder);
    }

    private void handleSession(Session session) {
        Timber.d("Handling user session");

        User sessionUser = session.getUser();

        UserSession userSession = appSessionHolder.get().get();

        userSession.setUser(sessionUser);
        userSession.setApiToken(session.getToken());
        userSession.setLegacyApiToken(session.getSsoToken());

        userSession.setLastUpdate(System.currentTimeMillis());

        List<Feature> features = session.getPermissions();
        userSession.setFeatures(features);

        appSessionHolder.put(userSession);
    }


    public static boolean isLoginError(Throwable error) {
        if (error instanceof HttpException) { // for janet-http
            HttpException cause = (HttpException) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        } else if (error instanceof RetrofitError) { // for retrofit
            RetrofitError cause = (RetrofitError) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        } else if (error.getCause() != null) {
            return isLoginError(error.getCause());
        }
        return true;
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
}
