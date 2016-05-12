package com.worldventures.dreamtrips.modules.dtl.store;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.AuthRetryPolicy;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.session.UserSession;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RetryLoginComposer<T> implements Observable.Transformer<T, T> {

    private final DreamTripsApi dreamTripsApi;
    private final SessionHolder<UserSession> appSessionHolder;

    private volatile boolean loginErrorHandled = false;
    private final AuthRetryPolicy retryPolicy;

    public RetryLoginComposer(DreamTripsApi dreamTripsApi,
                              SessionHolder<UserSession> appSessionHolder) {
        this.dreamTripsApi = dreamTripsApi;
        this.appSessionHolder = appSessionHolder;
        this.retryPolicy = new AuthRetryPolicy(appSessionHolder);
    }

    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable
                .retryWhen(observable -> observable.flatMap(e -> {
                    if (!loginErrorHandled) {
                        boolean shouldRetry = retryPolicy.handle(e, () -> {
                            UserSession userSession = appSessionHolder.get().get();
                            String username = userSession.getUsername();
                            String userPassword = userSession.getUserPassword();
                            try {
                                return dreamTripsApi.login(username, userPassword);
                            } catch (Throwable throwable) {
                                return null;
                            }
                        });
                        if (shouldRetry) {
                            loginErrorHandled = true;
                            return Observable.just(loginErrorHandled);
                        }
                    }
                    return Observable.error(e);
                }), Schedulers.io());
    }
}
