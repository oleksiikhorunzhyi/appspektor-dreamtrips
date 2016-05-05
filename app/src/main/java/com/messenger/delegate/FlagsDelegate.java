package com.messenger.delegate;

import com.messenger.api.temp.RetryLoginComposer;
import com.techery.spares.session.SessionHolder;
import com.messenger.api.GetFlagsAction;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FlagsDelegate {

    private final SessionHolder<UserSession> sessionHolder;
    private Janet janet;
    private ActionPipe<GetFlagsAction> flagsPipe;
    private GetFlagsAction getFlagsAction;

    public FlagsDelegate(SessionHolder<UserSession> sessionHolder, Janet janet) {
        this.sessionHolder = sessionHolder;
        this.janet = janet;
        flagsPipe = janet.createPipe(GetFlagsAction.class, Schedulers.io());
    }

    public Observable<GetFlagsAction> getFlags() {
        Observable<GetFlagsAction> observable =
                flagsPipe.createObservableSuccess(new GetFlagsAction())
                .compose(new RetryLoginComposer<>(sessionHolder, janet))
                .startWith(getFlagsAction)
                .compose(new NonNullFilter<>())
                .take(1)
                .publish()
                .refCount();
        observable.subscribe(getFlagsAction -> this.getFlagsAction = getFlagsAction,
                e -> Timber.e(e, "Could not get flags"));
        return observable;
    }
}
