package com.worldventures.dreamtrips.core.api.factory;

import android.content.Context;

import com.worldventures.dreamtrips.core.rx.goro.GoroObservable;
import com.worldventures.dreamtrips.modules.dtl.store.RetryLoginComposer;

import java.util.concurrent.Callable;

import rx.Observable;

public class GoroRxApiFactory implements RxApiFactory {

    private Context context;
    private RetryLoginComposer retryLoginComposer;

    public GoroRxApiFactory(Context context, RetryLoginComposer retryLoginComposer) {
        this.context = context;
        this.retryLoginComposer = retryLoginComposer;
    }

    @SuppressWarnings("unchecked") // Safe because of erasure.
    @Override
    public <T> Observable<T> composeApiCall(Callable<T> callable) {
        return GoroObservable
                .onService(this.context, callable)
                .compose(retryLoginComposer);
    }
}
