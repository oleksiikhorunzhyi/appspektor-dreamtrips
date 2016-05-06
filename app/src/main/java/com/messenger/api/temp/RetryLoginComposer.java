package com.messenger.api.temp;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.api.action.LoginAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.http.exception.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;


//TODO this is temporary composer until @alex.ko doesn't provide more appropriate wat to handle login error
public class RetryLoginComposer<T> implements Observable.Transformer<T, T> {

    private final SessionHolder<UserSession> appSessionHolder;
    private final ActionPipe<LoginAction> loginActionPipe;

    private volatile boolean loginErrorHandled = false;

    public RetryLoginComposer(SessionHolder<UserSession> appSessionHolder, Janet janet) {
        this.appSessionHolder = appSessionHolder;
        loginActionPipe = janet.createPipe(LoginAction.class, Schedulers.io());
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

                        return loginActionPipe.createObservableSuccess
                                (new LoginAction(username, userPassword))
                                .doOnNext(RetryLoginComposer.this::handleSession);
                    }
                    return Observable.error(e);
                }));
    }

    private boolean shouldRetry(Throwable error) {
        Timber.d("Check retry");
        return !loginErrorHandled && isLoginError(error) && isCredentialExist(appSessionHolder);
    }

    private void handleSession(LoginAction loginAction) {
        Timber.d("Handling user session");

        Session session = loginAction.getLoginResponse();
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
        if (error instanceof HttpException) {
            HttpException cause = (HttpException) error;
            return cause.getResponse() != null && cause.getResponse().getStatus() == HTTP_UNAUTHORIZED;
        } else if (error.getCause() instanceof HttpException) {
            HttpException cause = (HttpException) error.getCause();
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
