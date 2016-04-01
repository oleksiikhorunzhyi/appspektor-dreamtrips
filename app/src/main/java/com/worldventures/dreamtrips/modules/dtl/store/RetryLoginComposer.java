package com.worldventures.dreamtrips.modules.dtl.store;

import android.content.Context;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.rx.goro.GoroObservable;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import retrofit.RetrofitError;
import rx.Observable;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class RetryLoginComposer<T> implements Observable.Transformer<T, T> {

    private final Context context;
    private final DreamTripsApi dreamTripsApi;
    private final SessionHolder<UserSession> appSessionHolder;

    private volatile boolean loginErrorHandled = false;

    public RetryLoginComposer(Context context, DreamTripsApi dreamTripsApi,
                              SessionHolder<UserSession> appSessionHolder) {
        this.context = context;
        this.dreamTripsApi = dreamTripsApi;
        this.appSessionHolder = appSessionHolder;
    }

    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable
                .retryWhen(observable -> observable.flatMap(e -> {
                    if (shouldRetry(e)) {
                        Timber.d("Login Error");
                        loginErrorHandled = true;
                        UserSession userSession = appSessionHolder.get().get();
                        String username = userSession.getUsername();
                        String userPassword = userSession.getUserPassword();
                        return GoroObservable
                                .onService(context, () -> dreamTripsApi.login(username, userPassword))
                                .doOnNext(RetryLoginComposer.this::handleSession);
                    }
                    return Observable.error(e);
                }));
    }

    private boolean shouldRetry(Throwable error) {
        Timber.d("Check retry");
        return !loginErrorHandled && isLoginError(error) && isCredentialExist(appSessionHolder);
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
        if (error instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        } else if (error.getCause() instanceof RetrofitError) {
            RetrofitError cause = (RetrofitError) error.getCause();
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
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
