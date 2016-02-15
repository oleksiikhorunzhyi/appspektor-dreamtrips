package com.worldventures.dreamtrips.core.api.factory;

import java.util.concurrent.Callable;

import rx.Observable;

public interface RxApiFactory {

    <T> Observable<T> composeApiCall(Callable<T> callable);
}
