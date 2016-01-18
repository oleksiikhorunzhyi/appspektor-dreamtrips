package com.worldventures.dreamtrips.core.rx.goro;

import android.content.Context;

import com.stanfy.enroscar.goro.Goro;

import java.util.concurrent.Callable;

import rx.Observable;

public class GoroObservable {

    public static <T> Observable<T> onService(Context context, Callable<T> callable) {
        return onService(context, "default", callable);
    }

    public static <T> Observable<T> onService(Context context, String queue, Callable<T> callable) {
        Goro goro = Goro.bindOnDemandWith(context.getApplicationContext());
        return new RxGoro(goro).schedule(queue, callable);
    }
}
