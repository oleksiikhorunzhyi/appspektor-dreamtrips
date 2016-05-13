package com.messenger.delegate;

import com.messenger.api.ErrorParser;
import com.messenger.api.GetFlagsAction;
import com.messenger.api.exception.UiMessageException;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FlagsDelegate {

    private ActionPipe<GetFlagsAction> flagsPipe;
    private ActionState<GetFlagsAction> getFlagsActionState;
    private ErrorParser errorParser;

    public FlagsDelegate(Janet janet, ErrorParser errorParser) {
        flagsPipe = janet.createPipe(GetFlagsAction.class, Schedulers.io());
        this.errorParser = errorParser;
    }

    public Observable<ActionState<GetFlagsAction>> getFlags() {
        Observable<ActionState<GetFlagsAction>> observable =
                flagsPipe.createObservable(new GetFlagsAction())
                .startWith(getFlagsActionState)
                .filter(actionState ->
                    actionState != null && (actionState.status == ActionState.Status.SUCCESS
                            || actionState.status == ActionState.Status.FAIL))
                .doOnNext(state -> {
                    if (state.status == ActionState.Status.FAIL) {
                        String message = errorParser.getErrorMessage(state.action, state.exception);
                        state.exception = new UiMessageException(message, state.exception);
                    }
                })
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
}
