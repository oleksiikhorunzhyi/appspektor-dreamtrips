package com.worldventures.dreamtrips.core.api.factory;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.rx.goro.GoroObservable;
import com.worldventures.dreamtrips.modules.dtl.store.RetryLoginComposer;

import java.util.concurrent.Callable;

import rx.Observable;

public class GoroRxApiFactory implements RxApiFactory {

    private Context context;
    private Injector injector;

    public GoroRxApiFactory(Context context, Injector injector) {
        this.context = context;
        this.injector = injector;
    }

    @Override
    public <T> Observable<T> composeApiCall(Callable<T> callable) {
        return GoroObservable
                .onService(this.context, callable)
                .compose(new RetryLoginComposer<>(injector));
    }
}
