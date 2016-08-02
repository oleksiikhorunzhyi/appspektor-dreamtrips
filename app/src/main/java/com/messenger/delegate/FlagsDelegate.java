package com.messenger.delegate;

import com.messenger.api.GetFlagsAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FlagsDelegate {

    private ActionPipe<GetFlagsAction> flagsPipe;
    private ActionState<GetFlagsAction> getFlagsActionState;

    public FlagsDelegate(Janet janet) {
        flagsPipe = janet.createPipe(GetFlagsAction.class, Schedulers.io());
    }

    public Observable<ActionState<GetFlagsAction>> getFlags() {
        Observable<ActionState<GetFlagsAction>> observable =
                flagsPipe.createObservable(new GetFlagsAction())
                .startWith(getFlagsActionState)
                .filter(actionState ->
                    actionState != null && (actionState.status == ActionState.Status.SUCCESS
                            || actionState.status == ActionState.Status.FAIL))
                .take(1)
                .publish()
                .refCount();
        observable.subscribe(actionState -> {
                    if (actionState.status == ActionState.Status.SUCCESS) {
                        getFlagsActionState = actionState;
                    }
                },
                e -> Timber.e(e, "Could not get flags"));
        return observable;
    }

    public void clearCache() {
        getFlagsActionState = null;
    }
}
